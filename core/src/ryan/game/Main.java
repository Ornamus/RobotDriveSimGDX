package ryan.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ryan.game.ai.Pathfinding;
import ryan.game.competition.RobotMaker;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.controls.Gamepad;
import ryan.game.controls.Gamepads;
import ryan.game.entity.*;
import ryan.game.entity.parts.Part;
import ryan.game.games.*;
import ryan.game.games.power.PowerRankings;
import ryan.game.games.power.PowerUp;
import ryan.game.games.power.robots.PowerRobotBase;
import ryan.game.games.steamworks.Steamworks;
import ryan.game.games.steamworks.robots.SteamDefault;
import ryan.game.render.Drawable;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;
import ryan.game.screens.Screen;
import ryan.game.screens.TitleScreen;
import ryan.game.screens.WinnerScreen;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter implements InputProcessor {

    public static final Object WORLD_USE = new Object();

    public static boolean DEBUG_RENDER = false;

    public static final Color BLUE = Utils.toColor(50, 50, 245);//Utils.toColor(63, 72, 204);
    public static final Color RED = Utils.toColor(237, 28, 36);

	SpriteBatch batch;
    SpriteBatch unscaledBatch;
    public World world;
    Box2DDebugRenderer debugRenderer;
    public OrthographicCamera camera;
    public OrthographicCamera unscaledCamera;
    public static List<Drawable> drawablesAdd = new ArrayList<>();
    public static List<Drawable> drawablesRemove = new ArrayList<>();
    public static List<Drawable> drawables = new ArrayList<>();
    public static List<CollisionListener.Collision> collisions = new ArrayList<>();

    private static Main self = null;

    public static float screenWidth = 1920, screenHeight = 1080;
    public static final int world_width = 56, world_height = 34;
    private static final int camera_y = -4;

    public static float mtpW = screenWidth/world_width;
    public static float mtpH = screenHeight/world_height;

    public static int currentRobot = -1;

    private Viewport viewport;
    private Viewport unscaledViewport;

    public Screen screen;

    @Override
	public void create () {
        self = this;
        Gdx.input.setInputProcessor(this);
        Fonts.init(fontScale);
        Gamepads.init();
        Box2D.init();
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera(world_width, world_height);
        camera.update();
        viewport = new FitViewport(world_width, world_height, camera);

        unscaledCamera = new OrthographicCamera(screenWidth, screenHeight);
        unscaledCamera.update();
        unscaledViewport = new FitViewport(screenWidth, screenHeight, unscaledCamera);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        unscaledBatch = new SpriteBatch();
        batch.setProjectionMatrix(unscaledCamera.combined);

        setScreen(new TitleScreen());
        //setScreen(new GameScreen());
        //setScreen(new WinnerScreen());
    }

    public void setScreen(Screen s) {
        drawables.clear();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        screen = s;
        screen.init();
    }

    public static float widthScale = 1, heightScale = 1, fontScale = 1.5f;

    @Override
    public void resize(int width, int height) {
        camera.position.set(0, camera_y, 0);
        camera.update();
        viewport.update(width, height);

        unscaledCamera.update();
        unscaledViewport.update(width, height);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        unscaledBatch = new SpriteBatch();
        unscaledBatch.setProjectionMatrix(unscaledCamera.combined);
    }

    public void addFriction(Body b) {
        addFriction(b, 8f);
    }

	public void addFriction(Body b, float force) {
        synchronized (WORLD_USE) {
            BodyDef frictionDef = new BodyDef();
            frictionDef.type = BodyDef.BodyType.StaticBody;
            frictionDef.position.set(0, 0);

            Body frictionBody = world.createBody(frictionDef);

            FixtureDef frictionFixture = new FixtureDef();

            PolygonShape frictionShape = new PolygonShape();
            frictionShape.setAsBox(100, 100);

            frictionFixture.shape = frictionShape;
            frictionFixture.density = 0f;
            frictionFixture.restitution = 0;

            FrictionJointDef friction = new FrictionJointDef();
            friction.maxForce = force;
            friction.maxTorque = 2f;
            friction.initialize(frictionBody, b, b.getPosition());
            world.createJoint(friction);
        }
    }

    public static void spawnEntity(Entity e) {
        addDrawable(e);
    }

    public static void spawnEntity(float friction, Entity e) {
        e.friction = friction;
        addDrawable(e);
    }

    public static void removeEntity(Entity e) {
        removeDrawable(e);
        synchronized (WORLD_USE) {
            for (Body b : e.getBodies()) {
                b.setActive(false);
                b.setAwake(false);
                b.setUserData(null);
            }
        }
    }

    public static void addDrawable(Drawable d) {
        drawablesAdd.add(d);
    }

    public static void removeDrawable(Drawable d) {
        drawablesRemove.add(d);
    }

    int ticksWaitedCuzDum = 0;
	@Override
	public void render () {
        ticksWaitedCuzDum++;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        doPhysicsStep(Gdx.graphics.getDeltaTime());

        batch.begin();

        for (Drawable e : drawables) {
            if (e.isDrawScaled() && !drawablesRemove.contains(e)) e.draw(batch);
        }
        for (Drawable d : drawables) {
            if (d instanceof Entity && !drawablesRemove.contains(d)) {
                for (Part p : ((Entity)d).getParts()) {
                    if (p.show) p.draw(batch);
                }
            }
        }
        screen.draw(batch);
        batch.end();

        unscaledBatch.begin();
        for (Drawable e : drawables) {
            if (!e.isDrawScaled() && !drawablesRemove.contains(e)) e.draw(unscaledBatch);
            else if (e instanceof Entity) {
                ((Entity)e).drawUnscaled(unscaledBatch);
            }
        }
        screen.drawUnscaled(unscaledBatch);
        unscaledBatch.end();

        /*if (points == null && ticksWaitedCuzDum > 4) {
            pathfinding = new Pathfinding();
            points = pathfinding.findPath(2, 0, -4, 0);
            points.add(new Point2D.Float(2, 0));
        }*/
        /*
        shape.begin();
        if (points != null) {
            shape.setColor(Color.GREEN);
            for (GridCell[] array : pathfinding.cells) {
                for (GridCell c : array) {
                    if (c.isWalkable()) {
                        Point2D.Float loc = pathfinding.toWorldCoords(c.getX(), c.getY());
                        shape.rect(loc.x, loc.y, pathfinding.resolutionInMeters, pathfinding.resolutionInMeters);
                    }
                }
            }

            shape.setColor(Color.RED);
            for (GridCell[] array : pathfinding.cells) {
                for (GridCell c : array) {
                    if (!c.isWalkable()) {
                        Point2D.Float loc = pathfinding.toWorldCoords(c.getX(), c.getY());
                        shape.rect(loc.x, loc.y, pathfinding.resolutionInMeters, pathfinding.resolutionInMeters);
                    }
                }
            }

            shape.setColor(Color.BLUE);
            for (GridCell[] array : pathfinding.cells) {
                for (GridCell c : array) {
                    Point2D.Float loc = pathfinding.toWorldCoords(c.getX(), c.getY());
                    boolean isOnPath = false;
                    for (Point2D.Float p : points) {
                        if (p.x == loc.x && p.y == loc.y) {
                            isOnPath = true;
                            break;
                        }
                    }
                    if (isOnPath) {
                        shape.rect(loc.x, loc.y, pathfinding.resolutionInMeters, pathfinding.resolutionInMeters);
                    }
                }
            }
        }
        shape.end();*/

        if (DEBUG_RENDER) debugRenderer.render(world, camera.combined);
	}

    private float accumulator = 0;
    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Constants.TIME_STEP) {
            tick();
            synchronized (WORLD_USE) {
                world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            }
            if (screen instanceof GameScreen) GameScreen.time += Constants.TIME_STEP;
            accumulator -= Constants.TIME_STEP;
        }
    }

    private void tick() {
        for (Drawable e : new ArrayList<>(drawablesRemove)) {
            drawables.remove(e);
            drawablesRemove.remove(e);
            /*
            if (e instanceof Entity) {
                synchronized (WORLD_USE) {
                    for (Body b : ((Entity) e).getBodies()) {
                        world.destroyBody(b);
                    }
                }
            }*/
        }
        for (Drawable d : new ArrayList<>(drawablesAdd)) {
            if (d instanceof Entity) {
                Entity e = (Entity) d;
                synchronized (WORLD_USE) {
                    List<Body> no = e.getFrictionlessBodies();
                    for (Body b : e.getBodies()) {
                        if (!no.contains(b)) addFriction(b, e.friction);
                    }
                }
            }
            drawables.add(d);
        }
        drawablesAdd.clear();
        for (CollisionListener.Collision c : collisions) {
            c.a.onCollide(c.b, c.bA, c.bB, c.c);
            c.b.onCollide(c.a, c.bB, c.bA, c.c);
        }
        collisions.clear();
        screen.tick();
        for (Drawable e : drawables) {
            e.tick();
        }
    }

    public List<Entity> getEntities() {
        List<Entity> ents = new ArrayList<>();
        for (Drawable d : drawables) {
            if (d instanceof Entity) ents.add((Entity)d);
        }
        return ents;
    }
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public static Main getInstance() {
        return self;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    Vector3 getMousePosInGameWorld() {
        return Main.getInstance().unscaledCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 pos = getMousePosInGameWorld();
        return screen.click(pos, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
