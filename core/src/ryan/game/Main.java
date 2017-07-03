package ryan.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import org.xguzm.pathfinding.grid.GridCell;
import ryan.game.ai.Pathfinding;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.entity.*;
import ryan.game.games.Field;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.steamworks.SteamworksField;
import ryan.game.games.steamworks.robots.SteamDefault;
import ryan.game.render.Drawable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {

    private final boolean DEBUG_RENDER = false;

    public static final Color BLUE = Utils.toColor(63, 72, 204);
    public static final Color RED = Utils.toColor(237, 28, 36);

	SpriteBatch batch;
    SpriteBatch nonScaled;
    ShapeRenderer shape;
    public World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    OrthographicCamera nonScaledCamera;
    public static List<Robot> robots = new ArrayList<Robot>();
    public static List<Drawable> drawablesAdd = new ArrayList<Drawable>();
    public static List<Drawable> drawablesRemove = new ArrayList<Drawable>();
    public static List<Drawable> drawables = new ArrayList<Drawable>();
    public static List<CollisionListener.Collision> collisions = new ArrayList<CollisionListener.Collision>();

    public static boolean playMusic = true;

    public static BitmapFont bigFont, smallFont;
    GlyphLayout layout;
    FileHandle[] musicChoices;
    Music music = null;

    public static boolean matchPlay = false;
    public static long matchStart = 0;

    Sound matchStartSound;
    public Sound teleopStartSound;
    Sound ropeDropSound;
    Sound matchEndSound;
    Pathfinding pathfinding;
    List<Point2D.Float> points;

    List<Team> allTeams = new ArrayList<>();
    public static Schedule schedule;

    private static Main self = null;

    public static final int world_width = 56, world_height = 30; //56, 29
    private static final int camera_y = -4;

    private Field gameField;

    public static int currentRobot = -1;

	@Override
	public void create () {
        List<Integer> taken = new ArrayList<>();
        for (int i=0; i<62; i++) {
            int num;
            while (taken.contains((num = Utils.randomInt(1, 6499)))) {}
            taken.add(num);
            allTeams.add(new Team(num, "null"));
        }
        schedule = new Schedule();
        schedule.generate(allTeams, 8);
        self = this;
        ControllerManager.init();
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(world_width, world_height);
        camera.position.set(0, camera_y, 0);
        camera.update();
        nonScaledCamera = new OrthographicCamera(1100, 630);
        nonScaledCamera.update();
        int index = 0;

        int extraRobots = 0;
        if (extraRobots > 0) currentRobot = 0;

        for (int i=0; i<ControllerManager.getGamepads().size() + extraRobots; i++) {
            robots.add(Robot.create(new SteamDefault(), 0 + (index * 3), 0));
            index++;
        }

        gameField = new SteamworksField();//new PirateField();
        gameField.affectRobots();
        drawables.addAll(gameField.generateField());

        for (Robot r : robots) {
            addFriction(r.left);
            addFriction(r.right);
            drawables.add(r);
        }

		batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        nonScaled = new SpriteBatch();
        nonScaled.setProjectionMatrix(nonScaledCamera.combined);

        matchStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/charge_3.wav"));
        teleopStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/teleop.wav"));
        ropeDropSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/whoop.wav"));
        matchEndSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/end.wav"));

        musicChoices = Gdx.files.internal("core/assets/music").list();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/Kozuka.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 52;
        param.borderColor = Color.BLACK;
        param.color = Color.ORANGE;
        param.borderWidth = 2f;
        param.shadowColor = Color.BLACK;
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;
        bigFont = generator.generateFont(param);
        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/fonts/DTM-Mono.otf"));
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 26;
        param.borderWidth = 2f;
        param.borderColor = Color.BLACK;
        smallFont = generator.generateFont(param);
        generator.dispose();

        layout = new GlyphLayout(bigFont, "");

        shape = new ShapeRenderer();
        shape.setAutoShapeType(true);
        shape.setProjectionMatrix(camera.combined);

        Utils.log("Making pathfinder");
	}

    @Override
    public void resize(int width, int height) {
        // Lets check aspect ratio of our visible window
        float screenAR = width / (float) height;
        // Our camera needs to be created with new aspect ratio
        // Our visible gameworld width is still 20m but we need to
        // calculate what height keeps the AR correct.
        camera = new OrthographicCamera(world_width, (world_height * 2) /screenAR);
        // Finally set camera position so that (0,0) is at bottom left
        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.position.set(0, camera_y, 0);
        camera.update();

        // If we use spritebatch to draw lets update it here for new camera
        batch = new SpriteBatch();
        // This line says:"Camera lower left corner is 0,0. Width is 20 and height is 20/AR. Draw there!"
        batch.setProjectionMatrix(camera.combined);
    }

    public void addFriction(Body b) {
        addFriction(b, 8f);
    }

	public void addFriction(Body b, float force) {
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

    public void spawnEntity(Entity e) {
        addDrawable(e);
    }

    public void spawnEntity(float friction, Entity e) {
        e.friction = friction;
        addDrawable(e);
    }

    public void removeEntity(Entity e) {
        removeDrawable(e);
        for (Body b : e.getBodies()) {
            b.setActive(false);
            b.setAwake(false);
            b.setUserData(null);
        }
    }

    public void addDrawable(Drawable d) {
        drawablesAdd.add(d);
    }

    public void removeDrawable(Drawable d) {
        drawablesRemove.add(d);
    }

    boolean didWhoop = false;

    boolean resetField = false;

    int ticksWaitedCuzDum = 0;
	@Override
	public void render () {
        ticksWaitedCuzDum++;
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        doPhysicsStep(Gdx.graphics.getDeltaTime());

        int seconds = ScoreDisplay.getMatchTime();
        int minutes = 0;
        while (seconds >= 60) {
            seconds-=60;
            minutes++;
        }

        if (minutes == 0 && seconds <= 30 && !didWhoop && matchPlay) {
            ropeDropSound.play(.35f);
            didWhoop = true;
        }

        if (minutes == 0 && seconds <= 0 && matchPlay) {
            matchEndSound.play(.6f);
            if (playMusic) {
                if (music.isPlaying()) music.stop();
                music.dispose();
                music = null;
            }
            matchPlay = false;
            didWhoop = false;
            resetField = true;
        }

        batch.begin();
        for (Drawable e : drawables) {
            if (e.isDrawScaled() && !drawablesRemove.contains(e)) e.draw(batch);
        }
        gameField.draw(batch);
        batch.end();

        nonScaled.begin();
        for (Drawable e : drawables) {
            if (!e.isDrawScaled() && !drawablesRemove.contains(e)) e.draw(nonScaled);
        }
        nonScaled.end();

        /*if (points == null && ticksWaitedCuzDum > 4) {
            pathfinding = new Pathfinding();
            points = pathfinding.findPath(2, 0, -4, 0);
            points.add(new Point2D.Float(2, 0));
        }*/
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
        shape.end();

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
            synchronized (world) {
                world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
            }
            accumulator -= Constants.TIME_STEP;
        }
    }

    private void tick() {
        for (Drawable e : new ArrayList<>(drawablesRemove)) {
            drawables.remove(e);
        }
        for (Drawable d : drawablesAdd) {
            if (d instanceof Entity) {
                Entity e = (Entity) d;
                for (Body b : e.getBodies()) {
                    addFriction(b, e.friction);
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
        for (Drawable e : drawables) {
            e.tick();
        }
        gameField.tick();

        boolean aPressed = false;
        for (Gamepad g : ControllerManager.getGamepads()) {
            if (g.getButton(0).get()) {
                aPressed = true;
                break;
            }
        }
        if ((aPressed || Gdx.input.isKeyPressed(Input.Keys.P)) && !matchPlay) {
            gameField.matchStart();
            resetField = true;
            matchPlay = true;
            matchStart = System.currentTimeMillis();
            matchStartSound.play(.45f);
            if (playMusic) {
                music = Gdx.audio.newMusic(musicChoices[Utils.randomInt(0, musicChoices.length - 1)]);
                music.setVolume(.2f);
                music.play();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I) && matchPlay) {
            //TODO: play foghorn hoise
            matchPlay = false;
            didWhoop = false;
            if (playMusic && music.isPlaying()) music.stop();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R) || resetField) {
            gameField.resetField(drawables);
            resetField = false;
        }
        if (ControllerManager.getGamepads().size() != robots.size() && ControllerManager.getGamepads().size() == 1) {
            Gamepad one = ControllerManager.getGamepad(0);
            if (one.getButton(8).get()) {
                currentRobot++;
                if (currentRobot == robots.size()) {
                    currentRobot = 0;
                }
            }
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
}
