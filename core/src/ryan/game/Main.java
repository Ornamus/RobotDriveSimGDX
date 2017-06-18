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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.entity.*;
import ryan.game.games.Field;
import ryan.game.games.pirate.PirateField;
import ryan.game.games.steamworks.SteamworksField;
import ryan.game.render.Drawable;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {

    private final boolean DEBUG_RENDER = false;

    public static final Color BLUE = Utils.toColor(63, 72, 204);
    public static final Color RED = Utils.toColor(237, 28, 36);

	SpriteBatch batch;
    SpriteBatch nonScaled;
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
    Sound ropeDropSound;
    Sound matchEndSound;

    private static Main self = null;

    private static final int world_width = 56, world_height = 30; //56, 29
    private static final int camera_y = -4;

    private Field gameField;

	@Override
	public void create () {
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
        for (Gamepad g : ControllerManager.getGamepads()) {
            robots.add(Robot.create(0 + (index * 3), 0));
            index++;
        }

        gameField = new PirateField();
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

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        doPhysicsStep(Gdx.graphics.getDeltaTime());

        long timeIn = System.currentTimeMillis() - matchStart;
        long timeLeft = (((2 * 60) + 15) * 1000) - timeIn;
        int seconds = Math.round(timeLeft / 1000f);
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
            world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);
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
                music.setVolume(.25f);
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
