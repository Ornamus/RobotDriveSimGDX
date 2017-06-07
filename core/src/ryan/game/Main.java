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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.entity.Hopper;
import ryan.game.entity.Robot;
import ryan.game.entity.Entity;
import ryan.game.image.Image;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
    public World world;
    Box2DDebugRenderer debugRenderer;
    ExtendViewport viewport;
    OrthographicCamera camera;
    public static List<Robot> robots = new ArrayList<Robot>();
    public static List<Entity> entitiesAdd = new ArrayList<Entity>();
    public static List<Entity> entities = new ArrayList<Entity>();
    public static List<CollisionListener.Collision> collisions = new ArrayList<CollisionListener.Collision>();

    public static boolean playMusic = true;

    BitmapFont font;
    Sprite field;
    FileHandle[] musicChoices;
    Music music = null;

    boolean matchPlay = false;
    long matchStart = 0;
    Sound matchStartSound;
    Sound ropeDropSound;
    Sound matchEndSound;

    private static Main self = null;
    public static final float PPM = 32;

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
        entities.add(Entity.barrier(-23, 12, 3f, 2f, world).setAngle(26)); //Blue load
        entities.add(Entity.barrier(22, 12, 3f, 2f, world).setAngle(-26)); //Red load

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

        //entities.add(Entity.rectangleEntity(-18.7f, -.57f, .8f, .12f, world).setName("peg"));
        //entities.add(Entity.rectangleEntity(4, 4, 1, .25f, world).setName("peg"));



		batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        field = new Sprite(new Texture(Gdx.files.internal("core/assets/steamworks_pegs.png")));
        field.setBounds(-27.5f, -15, 54, 30);

        matchStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/charge_3.wav"));
        ropeDropSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/whoop.wav"));
        matchEndSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/end.wav"));

        musicChoices = Gdx.files.internal("core/assets/music").list();

        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(.25f);
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

    boolean didWhoop = false;

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
        }

        batch.begin();
        field.draw(batch);
        for (Entity e : entities) {
            e.draw(batch);
        }
        if (matchPlay) {
            font.draw(batch, minutes +  ":" + (seconds < 10 ? "0" : "") + seconds, -3, 15);
        }
        batch.end();
        //debugRenderer.render(world, camera.combined);
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
        for (Entity e : entities) {
            e.tick();
        }
        for (CollisionListener.Collision c : collisions) {
            c.a.onCollide(c.b);
            c.b.onCollide(c.a);
        }
        collisions.clear();
        boolean xPressed = false;
        for (Gamepad g : ControllerManager.getGamepads()) {
            if (g.getButton(2).get()) {
                xPressed = true;
                break;
            }
        }
        if ((xPressed || Gdx.input.isKeyPressed(Input.Keys.P)) && !matchPlay) {
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
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
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
