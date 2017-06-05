package ryan.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import ryan.game.Utils;
import ryan.game.controls.Button;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.drive.*;

public class Robot extends Entity {

    public final int id;
    public Body left, right;
    private int controllerIndex;
    private DriveController[] scrollOptions = {new Arcade(false), new Arcade(true), new Tank(), new CheesyDrive()};
    private FieldCentricStrafe fieldCentric;
    private Sprite gear;

    private Button changeAlliance;
    private Button changeControls;
    private Button gearToggle;
    private Button dozerToggle;

    private boolean dozer = false;
    private boolean dozerUnlocked = false;
    private Long dozerHoldStart = null;

    private float maxTurn = 1.5f;
    private boolean blue;
    private boolean hasGear = false;

    private static final float maxSpeed = 4.572f * 3.0f;
    private static final float maxAccel = 4.572f * 3.4f;
    private static final float robot_size = 0.9144f;

    private static final Texture blueTex = new Texture(Gdx.files.internal("core/assets/robot_blue.png"));
    private static final Texture redTex = new Texture(Gdx.files.internal("core/assets/robot_red.png"));

    private static final Texture blueDozerTex = new Texture(Gdx.files.internal("core/assets/dozer_blue.png"));
    private static final Texture redDozerTex = new Texture(Gdx.files.internal("core/assets/dozer_red.png"));

    private static final Texture gearTex = new Texture(Gdx.files.internal("core/assets/gear.png"));

    private static final Texture joyTex = new Texture(Gdx.files.internal("core/assets/joystick.png"));
    private static final Texture joysTex = new Texture(Gdx.files.internal("core/assets/joysticks.png"));
    private static final Texture tankTex = new Texture(Gdx.files.internal("core/assets/tank.png"));
    private static final Texture cheeseTex = new Texture(Gdx.files.internal("core/assets/cheese.png"));

    private Sprite icon;

    private static int robots = 0;

    private Robot(Body left, Body right) {
        super(robot_size, robot_size, left, right);
        id = robots++;
        this.left = left;
        this.right = right;
        controllerIndex = 0;
        //setPrimary(null);
        blue = !Utils.hasDecimal(id / 2.0);
        setSprite(blue ? blueTex : redTex);
        Gamepad g = ControllerManager.getGamepad(id);
        changeAlliance = g.getButton(7);
        changeControls = g.getButton(6);
        gearToggle = g.getButton(5);
        dozerToggle = g.getButton(1);
        gear = new Sprite(gearTex);
        gear.setBounds(-999, -999, 1f, 1f);
        fieldCentric = new FieldCentricStrafe(this);
    }

    @Override
    public Vector2 getPhysicsPosition() {
        float xAvg = 0, yAvg = 0;
        for (Body b : getBodies()) {
            Vector2 pos = b.getPosition();
            xAvg += pos.x;
            yAvg += pos.y;
        }
        xAvg /= getBodies().size();
        yAvg /= getBodies().size();
        return new Vector2(xAvg, yAvg);
    }

    @Override
    public float getPhysicsAngle() {
        float angle = 0;
        for (Body b : getBodies()) {
            angle += b.getAngle();
        }
        angle /= getBodies().size();
        return (float) Math.toDegrees(angle);
    }

    boolean changeAllianceWasTrue = false;
    boolean changeControlsWasTrue = false;
    boolean gearToggleWasTrue = false;
    boolean dozerToggleWasTrue = false;

    float iconAlpha;

    @Override
    public void tick() {
        super.tick();
        gear.setPosition(getX() - gear.getWidth()/2, getY() - gear.getHeight()/2);
        gear.setOriginCenter();
        gear.setRotation(getAngle());
        if (icon != null) {
            icon.setPosition(getX() - icon.getWidth() / 2, getY() + 1f);
            icon.setAlpha(iconAlpha);
            iconAlpha -= 0.01;
            if (iconAlpha <= 0) icon = null;
        }
        float leftMotor, rightMotor;
        Float middleMotor = null;
        if (ControllerManager.getGamepads().isEmpty()) {
            leftMotor = Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0;
            rightMotor = Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0;
        } else {
            Gamepad g = ControllerManager.getGamepad(id);

            boolean val = changeControls.get();
            if (val && !changeControlsWasTrue) {
                controllerIndex++;
                Utils.log("Robot " + id + " changing controls (" + g.hasSecondJoystick() + ")");
                while ((controllerIndex == 1 || controllerIndex == 2 || controllerIndex == 3) && !g.hasSecondJoystick()) {
                    controllerIndex++;
                    if (controllerIndex >= scrollOptions.length) controllerIndex = 0;
                }
                if (controllerIndex >= scrollOptions.length) controllerIndex = 0;
                Texture newTex = null;
                if (controllerIndex == 0) newTex = joyTex;
                if (controllerIndex == 1) newTex = joysTex;
                if (controllerIndex == 2) newTex = tankTex;
                if (controllerIndex == 3) newTex = cheeseTex;
                icon = new Sprite(newTex);
                icon.setBounds(-999, -999, 1.25f, 1.25f);
                iconAlpha = 1f;
            }
            changeControlsWasTrue = val;

            DriveOrder o = scrollOptions[controllerIndex].calculate(g);
            //DriveOrder o = fieldCentric.calculate(g);

            leftMotor = o.left;
            rightMotor = o.right;
            if (o.hasMiddle()) middleMotor = o.middle;


            val = dozerToggle.get();
            if (val && !dozerUnlocked) {
                if (dozerHoldStart == null) dozerHoldStart = System.currentTimeMillis();
                if (System.currentTimeMillis() - dozerHoldStart >= 5000) {
                    dozerUnlocked = true;
                    Utils.log("Robot " + id + " unlocked Dozer!");
                }
            } else {
                dozerHoldStart = null;
            }
            if (val && !dozerToggleWasTrue && dozerUnlocked) {
                dozer = !dozer;
                if (blue) {
                    if (dozer) setSprite(blueDozerTex);
                    else setSprite(blueTex);
                } else {
                    if (dozer) setSprite(redDozerTex);
                    else setSprite(redTex);
                }
            }
            dozerToggleWasTrue = val;

            val = changeAlliance.get();
            if (val && !changeAllianceWasTrue) {
                blue = !blue;
                if (blue) {
                    if (dozer) setSprite(blueDozerTex);
                    else setSprite(blueTex);
                } else {
                    if (dozer) setSprite(redDozerTex);
                    else setSprite(redTex);
                }
                Utils.log("Swap");
            }
            changeAllianceWasTrue = val;

            val = gearToggle.get();
            if (val && !gearToggleWasTrue) {
                hasGear = !hasGear;
            }
            gearToggleWasTrue = val;
        }
        leftMotor = Utils.cap(leftMotor, 1);
        rightMotor = Utils.cap(rightMotor, 1);
        if (middleMotor != null) Utils.cap(middleMotor, 1);
        updateMotors(leftMotor, rightMotor);
        if (middleMotor != null) {
            updateMiddleMotor(middleMotor);
        } else {
            doFriction(left);
            doFriction(right);
        }
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);
        if (hasGear) gear.draw(b);
        if (icon != null) icon.draw(b);
    }

    final float k = 2.25f; //2.25

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

        maxTurn = turnMult * 2f; //1.5f

        if (Math.abs(left.getAngularVelocity()) > maxTurn) {
            left.setAngularVelocity((maxTurn) * Utils.sign(left.getAngularVelocity()));
        }

        if (Math.abs(right.getAngularVelocity()) > maxTurn) {
            right.setAngularVelocity((maxTurn) * Utils.sign(right.getAngularVelocity()));
        }
    }

    public void updateMiddleMotor(float m) {
        //Vector2 vel = getLateralVelocity(left).add(getLateralVelocity(right));
        //vel.scl(0.5f);

        Utils.log("Bot angle: " + Utils.roundToPlace(getAngle() + 90, 2));

        float forceX = ((maxSpeed * 2) * (m * 2)) * (float) Math.sin(getAngle() + 90);
        float forceY = ((maxSpeed * 2) * (m * 2)) * (float) Math.cos(getAngle() + 90);

        left.applyForceToCenter(Utils.cap(forceX, maxAccel * 2), Utils.cap(forceY, maxAccel * 2), true);
        right.applyForceToCenter(Utils.cap(forceX, maxAccel * 2), Utils.cap(forceY, maxAccel * 2), true);
    }

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
