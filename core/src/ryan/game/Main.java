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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.entity.*;

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
    public static List<Entity> entitiesAdd = new ArrayList<Entity>();
    public static List<Entity> entitiesRemove = new ArrayList<Entity>();
    public static List<Entity> entities = new ArrayList<Entity>();
    public static List<CollisionListener.Collision> collisions = new ArrayList<CollisionListener.Collision>();

    List<Sprite> redRotors = new ArrayList<Sprite>();
    List<Sprite> blueRotors = new ArrayList<Sprite>();

    public static boolean playMusic = true;

    BitmapFont bigFont, smallFont;
    GlyphLayout layout;
    Sprite field;
    FileHandle[] musicChoices;
    Music music = null;

    public static boolean matchPlay = false;
    long matchStart = 0;
    Sound matchStartSound;
    Sound ropeDropSound;
    Sound matchEndSound;

    public static int blueGears = 0;
    public static int redGears = 0;

    private static Main self = null;

	@Override
	public void create () {
        self = this;
        ControllerManager.init();
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(56, 29);
        camera.update();
        nonScaledCamera = new OrthographicCamera(1100, 630);
        nonScaledCamera.update();
        int index = 0;
        for (Gamepad g : ControllerManager.getGamepads()) {
            robots.add(Robot.create(0 + (index * 3), 0, world));
            index++;
        }

        for (Robot r : robots) {
            addFriction(r.left);
            addFriction(r.right);
            entities.add(r);
        }

        /*
        Entity e = Entity.rectangleEntity(5, 5, .9f, .9f, world);
        for (Body b : e.getBodies()) {
            addFriction(b, 12f);
        }
        entities.add(e);
        */

        entities.add(Hopper.create(-9.5f, 12.25f, true, world)); //left top hopper
        entities.add(Hopper.create(8.35f, 12.25f, true, world)); //right top hopper


        entities.add(Hopper.create(-16f, -13.45f, false, world)); //left bottom hopper
        entities.add(Hopper.create(-0.6f, -13.45f, false, world)); //middle bottom hopper
        entities.add(Hopper.create(16f - 1.15f, -13.45f, false, world)); //right bottom hopper


        entities.add(Entity.barrier(0, 12, 28f, .5f, world)); //top wall
        entities.add(Entity.barrier(0, -13.2f, 28f, .5f, world)); //bottom wall
        entities.add(Entity.barrier(-25.5f, 0, .5f, 10f, world)); //left wall
        entities.add(Entity.barrier(24.5f, 0, .5f, 10f, world)); //right wall

        entities.add(LoadingStation.create(true, true, -24.8f, 11, 26)); //blue load left
        entities.add(LoadingStation.create(true, false, -21.6f, 12.65f, 26)); //blue load right

        entities.add(LoadingStation.create(false, true, 20.4f, 12.65f, -26)); //red load left
        entities.add(LoadingStation.create(false, false, 23.6f, 11, -26)); //red load right

        entities.add(Entity.barrier(-23, 12, 3f, 2f, world).setAngle(26)); //Blue load barrier
        entities.add(Entity.barrier(22, 12, 3f, 2f, world).setAngle(-26)); //Red load barrier

        entities.add(Entity.barrier(-24.8f, -12.9f, 2f, 2f, world).setAngle(46)); //Red boiler
        entities.add(Entity.barrier(23.8f, -12.9f, 2f, 2f, world).setAngle(-46)); //Blue boiler

        PolygonShape s = new PolygonShape();

        float acrossDistance = 3.77f;
        float pointDistance = 2.2f;
        float riseDistance = 4.4f;

        Vector2[] vertices = new Vector2[] {
                new Vector2(0, 0),
                new Vector2(acrossDistance, -pointDistance),
                new Vector2(acrossDistance * 2, 0),
                new Vector2(acrossDistance * 2, riseDistance),
                new Vector2(acrossDistance, riseDistance + pointDistance),
                new Vector2(0, riseDistance)
        };
        s.set(vertices);

        entities.add(Entity.barrier(8.9f, -2.75f, s, world)); //blue airship
        entities.add(Entity.barrier(-17.65f, -2.75f, s, world)); //red airship

        entities.add(Entity.peg(-18.7f, -.57f, 0));
        entities.add(Entity.peg(-16.25f, 3.25f, 360-60));
        entities.add(Entity.peg(-16f, -4.25f, 60));

        float xFix = 1.55f;

        entities.add(Entity.peg(18.9f - xFix, -.57f, 0));
        entities.add(Entity.peg(16.25f - xFix, 3.25f, 60));
        entities.add(Entity.peg(16.25f - xFix, -4.25f, 360-60));


        float sideSpace = 1f;

        for (int i=0; i<2; i++) {

            Color color = i == 0 ? RED : BLUE;
            List<Sprite> rotors = i == 0 ? redRotors : blueRotors;
            float startX = i == 0 ? -18 : 8.5f;
            float startY = i == 0 ? -3.5f : .4f;

            for (int r=0; r<3; r++) {
                Sprite sprite = new Sprite(Utils.colorImage("core/assets/rotor.png", color));

                float x = r == 0 ? startX : (r == 1 ? startX + 7f : startX + 3.5f);
                float y = r == 0 ? startY : (r == 1 ? startY : startY + (6f * (i == 0 ? 1 : -1)));
                sprite.setBounds(x, y, 2f, 2f);
                sprite.setOrigin(.7f, 1f);
                //sprite.setOriginCenter();
                if (i == 1) sprite.setRotation(180);
                rotors.add(sprite);
            }
        }

        //entities.add(Entity.rectangleEntity(-18.7f, -.57f, .8f, .12f, world).setName("peg"));
        //entities.add(Entity.rectangleEntity(4, 4, 1, .25f, world).setName("peg"));



		batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        nonScaled = new SpriteBatch();
        nonScaled.setProjectionMatrix(nonScaledCamera.combined);

        field = new Sprite(new Texture(Gdx.files.internal("core/assets/steamworks_pegs.png")));
        field.setBounds(-27.5f, -15, 54, 30);

        matchStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/charge_3.wav"));
        ropeDropSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/whoop.wav"));
        matchEndSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/end.wav"));

        musicChoices = Gdx.files.internal("core/assets/music").list();

        bigFont = new BitmapFont(Gdx.files.internal("core/assets/fonts/deter.fnt"));
        bigFont.setColor(Color.BLACK);
        bigFont.getData().setScale(2.5f);

        smallFont = new BitmapFont(Gdx.files.internal("core/assets/fonts/deter.fnt"));
        smallFont.setColor(Color.BLACK);
        smallFont.getData().setScale(1f);

        layout = new GlyphLayout(bigFont, "");
	}

    @Override
    public void resize(int width, int height) {
        // Lets check aspect ratio of our visible window
        float screenAR = width / (float) height;
        // Our camera needs to be created with new aspect ratio
        // Our visible gameworld width is still 20m but we need to
        // calculate what height keeps the AR correct.
        camera = new OrthographicCamera(56, (28 * 2) /screenAR);
        // Finally set camera position so that (0,0) is at bottom left
        //camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.position.set(0, 0, 0);
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
        for (Body b : e.getBodies()) {
            addFriction(b);
        }
        entitiesAdd.add(e);
    }

    public void spawnEntity(float friction, Entity e) {
        for (Body b : e.getBodies()) {
            addFriction(b, friction);
        }
        entitiesAdd.add(e);
    }

    public void removeEntity(Entity e) {
        entitiesRemove.add(e);
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
        field.draw(batch);
        for (Entity e : entities) {
            e.draw(batch);
        }
        int redSpinning = 0;
        if (redGears > 12) redSpinning = 3;
        else if (redGears > 6) redSpinning = 2;
        else if (redGears > 2) redSpinning = 1;
        int red = 0;
        for (Sprite s : redRotors) {
            if (matchPlay) {
                if (red < redSpinning) s.setRotation(s.getRotation() + 4f);
            } else s.setRotation(0);
            s.draw(batch);
            red++;
        }

        int blueSpinning = 0;
        if (blueGears > 12) blueSpinning = 3;
        else if (blueGears > 6) blueSpinning = 2;
        else if (blueGears > 2) blueSpinning = 1;
        int blue = 0;
        for (Sprite s : blueRotors) {
            if (matchPlay) {
                if (blue < blueSpinning) s.setRotation(s.getRotation() - 4f);
            } else s.setRotation(180);
            s.draw(batch);
            blue++;
        }
        batch.end();

        nonScaled.begin();
        if (matchPlay) {
            bigFont.draw(nonScaled, minutes +  ":" + (seconds < 10 ? "0" : "") + seconds, -90, 300);

            /*
            smallFont.setColor(Color.RED);
            smallFont.draw(nonScaled, "12", -290, 35);
            */

            float same = 55;
            drawGearDisplay(-287.5f, -25, redGears, redGears > 12 ? Color.YELLOW : Color.WHITE, nonScaled);
            drawGearDisplay(232.5f, -25, blueGears, blueGears > 12 ? Color.YELLOW : Color.WHITE, nonScaled);
        }
        nonScaled.end();

        if (DEBUG_RENDER) debugRenderer.render(world, camera.combined);
	}

	public void drawGearDisplay(float x, float y, int gears, Color c, SpriteBatch b) {
        b.draw(Gear.TEXTURE, x, y, 30f, 30f);
        smallFont.setColor(c);
        layout.setText(smallFont, gears + "");
        smallFont.draw(b, gears + "", x + 15 - (layout.width / 2), y + 55);
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
        entities.addAll(entitiesAdd);
        entitiesAdd.clear();
        for (Entity e : entitiesRemove) {
            for (Body b : e.getBodies()) {
                world.destroyBody(b);
            }
            entities.remove(e);
        }
        entitiesRemove.clear();
        for (Entity e : entities) {
            e.tick();
        }
        for (CollisionListener.Collision c : collisions) {
            c.a.onCollide(c.b, c.bA, c.bB);
            c.b.onCollide(c.a, c.bB, c.bA);
        }
        collisions.clear();
        boolean aPressed = false;
        for (Gamepad g : ControllerManager.getGamepads()) {
            if (g.getButton(0).get()) {
                aPressed = true;
                break;
            }
        }
        if ((aPressed || Gdx.input.isKeyPressed(Input.Keys.P)) && !matchPlay) {
            blueGears = 0;
            redGears = 0;
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
            if (playMusic && music.isPlaying()) music.stop();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R) || resetField) {
            for (Entity e : new ArrayList<Entity>(entities)) {
                if (e.getName().equalsIgnoreCase("fuel") || e.getName().equalsIgnoreCase("gear")) {
                    for (Body b : e.getBodies()) {
                        world.destroyBody(b);
                    }
                    entities.remove(e);
                } else if (e instanceof Hopper) {
                    ((Hopper)e).reset();
                }
            }
            resetField = false;
        }
    }
	
	@Override
	public void dispose () {
		batch.dispose();
		//field.dispose();
	}

	public static Main getInstance() {
        return self;
    }
}
