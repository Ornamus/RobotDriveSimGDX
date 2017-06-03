package ryan.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import ryan.game.Utils;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.drive.Arcade;
import ryan.game.drive.CheesyDrive;
import ryan.game.drive.DriveController;
import ryan.game.drive.DriveOrder;

public class Robot {

    public final int id;
    public Body left, right;
    private DriveController controller;

    private static final float maxSpeed = 4.572f * 3.0f;
    private static final float maxAccel = 4.572f * 3.0f;
    private float maxTurn = 1.5f;

    private static int robots = 0;

    private Robot(Body left, Body right) {
        id = robots++;
        this.left = left;
        this.right = right;
        controller = new Arcade(id == 0 ? true : false);
    }

    public void tick() {
        float leftMotor, rightMotor;
        if (ControllerManager.getGamepads().isEmpty()) {
            leftMotor = Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0;
            rightMotor = Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0;
        } else {
            Gamepad g = ControllerManager.getGamepad(id);
            DriveOrder o = controller.calculate(g);
            leftMotor = o.left;
            rightMotor = o.right;
        }
        leftMotor = Utils.cap(leftMotor, 1);
        rightMotor = Utils.cap(rightMotor, 1);
        updateMotors(leftMotor, rightMotor);
        doFriction(left);
        doFriction(right);
    }

    final float k = 2.25f;

    public void updateMotors(float l, float r) {
        //if (l != 0 || r != 0) Utils.log(l + " / " + r);
        float lAngle = -left.getAngle();
        float rAngle = -right.getAngle();

        Vector2 lVel = left.getLinearVelocity();
        Vector2 rVel = right.getLinearVelocity();

        float leftX = (maxSpeed * l) * (float) Math.sin(lAngle);
        float leftY = (maxSpeed * l) * (float) Math.cos(lAngle);

        left.applyForceToCenter(Utils.cap(k * (leftX - lVel.x), maxAccel), Utils.cap(k * (leftY - lVel.y), maxAccel), true);


        float rightX = (maxSpeed * r) * (float) Math.sin(rAngle);
        float rightY = (maxSpeed * r) * (float) Math.cos(rAngle);

        right.applyForceToCenter(Utils.cap(k * (rightX - rVel.x), maxAccel), Utils.cap(k * (rightY - rVel.y), maxAccel), true);

        //TODO: still not perfect, stationary turning is bleh

        float turnMult = Math.abs(l - r);

        //Utils.log(l + " / " + r+  "  |  " +turnMult);
        if (turnMult > 1) {
            float twoCloseness = turnMult - 1f;
            turnMult += twoCloseness * .5;
        }

        maxTurn = turnMult * 1.5f;

        if (Math.abs(left.getAngularVelocity()) > maxTurn) {
            left.setAngularVelocity((maxTurn) * Utils.sign(left.getAngularVelocity()));
        }

        if (Math.abs(right.getAngularVelocity()) > maxTurn) {
            right.setAngularVelocity((maxTurn) * Utils.sign(right.getAngularVelocity()));
        }
    }

    private static final float robot_size = 0.9144f;

    public static Robot create(float x, float y, World w) {
        Body left = createRobotPart(x - (robot_size * 1), y, w);
        Body right = createRobotPart(x, y, w);

        WeldJointDef jointDef = new WeldJointDef ();
        jointDef.collideConnected = false;
        jointDef.initialize(left, right, new Vector2(0, 0));
        w.createJoint(jointDef);

        return new Robot(left, right);
    }

    private static Body createRobotPart(float x, float y, World w) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.DynamicBody;
        rightDef.position.set(x, y);

        Body right = w.createBody(rightDef);
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsBox(robot_size / 2, robot_size);

        FixtureDef rightFix = new FixtureDef();
        rightFix.shape = rightShape;
        rightFix.density = .5f;
        rightFix.restitution = 0.6f; // Make it bounce a little bit

        Fixture fixture = right.createFixture(rightFix);
        rightShape.dispose();
        return right;
    }

    //TODO: allow skid
    public void doFriction(Body b) {
        Vector2 impulse = getLateralVelocity(b).scl(-1, -1).scl(b.getMass(), b.getMass());
        b.applyLinearImpulse(impulse, b.getWorldCenter(), true);
    }

    private Vector2 getLateralVelocity(Body b) {
        Vector2 linear = b.getLinearVelocity();
        Vector2 currentRightNormal = b.getWorldVector(new Vector2(1, 0));
        float magic = currentRightNormal.dot(linear);
        return currentRightNormal.scl(magic, magic);
    }
}
