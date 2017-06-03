package ryan.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.entity.Robot;
import ryan.game.entity.Entity;
import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    List<Robot> robots = new ArrayList<Robot>();
    List<ryan.game.entity.Entity> entities = new ArrayList<Entity>();

	@Override
	public void create () {
        ControllerManager.init();
        Box2D.init();
        world = new World(new Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(1100, 600);
        camera.position.set(0, 0, 0);
        camera.zoom = .25f / 5f;
        camera.update();
        int index = 0;
        for (Gamepad g : ControllerManager.getGamepads()) {
            robots.add(Robot.create(0 + (index * 3), 0, world));
            index++;
        }

        for (Robot r : robots) {
            addFriction(r.left);
            addFriction(r.right);
        }

        Entity e = Entity.rectangleEntity(5, 5, .9f, .9f, world);
        addFriction(e.body, 12f);
        entities.add(e);

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

		batch = new SpriteBatch();
        img = new Texture(Gdx.files.internal("core/assets/steamworks.png"));
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

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        debugRenderer.render(world, camera.combined);
        doPhysicsStep(Gdx.graphics.getDeltaTime());
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
        for (Robot r : robots) {
            r.tick();
        }
    }
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
