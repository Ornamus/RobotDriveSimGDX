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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.ai.Pathfinding;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.entity.*;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.steamworks.SteamResultDisplay;
import ryan.game.games.steamworks.Steamworks;
import ryan.game.games.steamworks.robots.SteamDefault;
import ryan.game.render.Drawable;
import ryan.game.render.Fonts;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {

    public static final Object WORLD_USE = new Object();

    public static boolean DEBUG_RENDER = false;

    public static final Color BLUE = Utils.toColor(50, 50, 245);//Utils.toColor(63, 72, 204);
    public static final Color RED = Utils.toColor(237, 28, 36);

    SteamResultDisplay results = null;
	SpriteBatch batch;
    SpriteBatch nonScaled;
    ShapeRenderer shape;
    public World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    OrthographicCamera nonScaledCamera;
    public static List<Robot> robots = new ArrayList<>();
    public static List<Drawable> drawablesAdd = new ArrayList<>();
    public static List<Drawable> drawablesRemove = new ArrayList<>();
    public static List<Drawable> drawables = new ArrayList<>();
    public static List<CollisionListener.Collision> collisions = new ArrayList<>();

    public static boolean playMusic = true;
    public static boolean makeSchedule = false;
    public static String eventName = "FIRST Championship";
    public static String eventKey = "debug";

    public static boolean isShowingResults = false;

    FileHandle[] musicChoices;
    Music music = null;

    private static long time = 0;

    public static boolean matchPlay = false;
    public static long matchStart = 0;
    public static long matchEnd = 0;

    Sound matchStartSound;
    public Sound teleopStartSound;
    public Sound ropeDropSound;
    Sound matchEndSound;
    Sound foghornSound;
    Pathfinding pathfinding;
    List<Point2D.Float> points;

    List<Team> allTeams = new ArrayList<>();
    public static Schedule schedule;

    private static Main self = null;

    public static final int world_width = 56, world_height = 30; //56, 29
    private static final int camera_y = -4;

    public static float meterToPixelWidth = 1100f/world_width;
    public static float meterToPixelHeight = 630f/world_height;

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
        //Gson g = new GsonBuilder().setPrettyPrinting().create();
        //Utils.writeFile("teams.txt", g.toJson(allTeams));
        //TODO: load teams from "teams.txt"

        schedule = new Schedule();
        schedule.generate(allTeams, 8);
        self = this;
        Fonts.init();
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
            robots.add(Robot.create(new SteamDefault(), 2 + (index * 3), -11));
            index++;
        }

        //gameField = new PirateField();
        gameField = new Steamworks();
        gameField.affectRobots();
        drawables.addAll(gameField.generateField());

        robots.forEach(this::spawnEntity);

        gameField.updateMatchInfo();

        //drawables.add(new SteamResultDisplay(0,0));

		batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        nonScaled = new SpriteBatch();
        nonScaled.setProjectionMatrix(nonScaledCamera.combined);

        matchStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/charge_3.wav"));
        teleopStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/teleop.wav"));
        ropeDropSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/whoop.wav"));
        matchEndSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/end.wav"));
        foghornSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/foghorn.wav"));

        musicChoices = Gdx.files.internal("core/assets/music").list();

        shape = new ShapeRenderer();
        shape.setAutoShapeType(true);
        shape.setProjectionMatrix(camera.combined);

        Utils.log("Making pathfinder");
	}

    @Override
    public void resize(int width, int height) {
        float screenAR = width / (float) height;

        meterToPixelHeight = 630f/((world_height * 2) /screenAR);
        camera = new OrthographicCamera(world_width, (world_height * 2) /screenAR);

        camera.position.set(0, camera_y, 0);
        camera.update();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
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

    public void spawnEntity(Entity e) {
        addDrawable(e);
    }

    public void spawnEntity(float friction, Entity e) {
        e.friction = friction;
        addDrawable(e);
    }

    public void removeEntity(Entity e) {
        removeDrawable(e);
        synchronized (WORLD_USE) {
            for (Body b : e.getBodies()) {
                b.setActive(false);
                b.setAwake(false);
                b.setUserData(null);
            }
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
        //long start = System.currentTimeMillis();

        batch.begin();
        for (Drawable e : drawables) {
            if (e.isDrawScaled() && !drawablesRemove.contains(e)) e.draw(batch);
        }
        gameField.draw(batch);
        batch.end();

        nonScaled.begin();
        for (Drawable e : drawables) {
            if (!e.isDrawScaled() && !drawablesRemove.contains(e)) e.draw(nonScaled);
            else if (e instanceof Entity) {
                ((Entity)e).drawUnscaled(nonScaled);
            }
        }
        nonScaled.end();

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
        //long time = System.currentTimeMillis() - start;
        //if (time > 8) Utils.log(time + "ms");
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
            time += Constants.TIME_STEP;
            accumulator -= Constants.TIME_STEP;
        }
    }

    Long upHeld = null;

    private void tick() {
        if (Game.isPlaying() && Game.getMatchTime() <= 0) {
            matchEndSound.play(.6f);
            if (playMusic && music != null) {
                if (music.isPlaying()) music.stop();
                music.dispose();
                music = null;
            }
            matchPlay = false;
            didWhoop = false;
            resetField = true;
            if (gameField instanceof Steamworks) {
                results = new SteamResultDisplay(0, 0); //TODO: game specific, move to steamworks code or make standardized version
                drawables.add(results);
                isShowingResults = true;
            }
            matchEnd = System.currentTimeMillis();
            gameField.onMatchEnd();
        }

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
        for (Drawable e : drawables) {
            e.tick();
        }
        gameField.tick();

        boolean controllerStartMatch = false;
        boolean anyHeld = false;
        for (Gamepad g : ControllerManager.getGamepads()) {
            if (g.getDPad() == .25) {
                anyHeld = true;
                if (upHeld == null) upHeld = System.currentTimeMillis();
                else if (System.currentTimeMillis() - upHeld >= 2000) {
                    controllerStartMatch = true;
                }
                break;
            }
        }
        if (!anyHeld) upHeld = null;
        if ((controllerStartMatch || Gdx.input.isKeyPressed(Input.Keys.P)) && !matchPlay && !isShowingResults) {
            matchEnd = 0;
            gameField.onMatchStart();
            resetField = true;
            matchPlay = true;
            matchStart = System.currentTimeMillis();
            matchStartSound.play(.45f);
            if (playMusic) {
                music = Gdx.audio.newMusic(musicChoices[Utils.randomInt(0, musicChoices.length - 1)]);
                music.setVolume(.1f);
                music.play();
            }
            controllerStartMatch = false;
            upHeld = null;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I) || controllerStartMatch) {
            if (matchPlay) {
                foghornSound.play(.25f);
                matchPlay = false;
                didWhoop = false;
                if (playMusic && music.isPlaying()) music.stop();
            } else if (isShowingResults) {
                drawables.remove(results);
                results = null;
                isShowingResults = false;
            }
            upHeld = null;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R) || resetField) {
            gameField.resetField(drawables);
            resetField = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            ControllerManager.init();
        }
        if (ControllerManager.getGamepads().size() != robots.size() && ControllerManager.getGamepads().size() == 1) {
            Gamepad one = ControllerManager.getGamepad(0);
            if (one.getButton(10).get()) {
                if (!wasHeld) {
                    currentRobot++;
                    if (currentRobot == robots.size()) {
                        currentRobot = 0;
                    }
                }
                wasHeld = true;
            } else {
                wasHeld = false;
            }
        }
    }

    boolean wasHeld = false;

    public List<Entity> getEntities() {
        List<Entity> ents = new ArrayList<>();
        for (Drawable d : drawables) {
            if (d instanceof Entity) ents.add((Entity)d);
        }
        return ents;
    }

    public static long getTime() {
        return System.currentTimeMillis();//time;
    }
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public static Main getInstance() {
        return self;
    }
}
