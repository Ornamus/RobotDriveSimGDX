package ryan.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.controls.Button;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.drive.*;
import ryan.game.games.RobotMetadata;

public class Robot extends Entity {

    public final int id;
    public Body left, right, intake = null;
    private int controllerIndex;
    private DriveController[] scrollOptions = {new Arcade(false), new Arcade(true), new Tank(), new CheesyDrive()};
    private FieldCentricStrafe fieldCentric;

    public RobotMetadata metadata = null;

    private Button changeAlliance;
    private Button changeControls;
    private Button dozerToggle;
    private Button reverseToggle;

    public boolean dozer = false;
    private boolean dozerUnlocked = false;
    private Long dozerHoldStart = null;

    private float maxTurn = 1.5f;
    public boolean blue;

    static final float maxMps = 16 / 3.28084f; //5

    private static final float maxAccel = 4.572f * 3.4f;
    private static final float robot_size = 0.9144f;

    private static final Texture gearTex = new Texture(Gdx.files.internal("core/assets/gear.png"));
    private static final Texture joyTex = new Texture(Gdx.files.internal("core/assets/joystick.png"));
    private static final Texture joysTex = new Texture(Gdx.files.internal("core/assets/joysticks.png"));
    private static final Texture tankTex = new Texture(Gdx.files.internal("core/assets/tank.png"));
    private static final Texture cheeseTex = new Texture(Gdx.files.internal("core/assets/cheese.png"));

    private Sprite intakeSprite;
    private Sprite icon;

    private static int robots = 0;

    private Robot(Body left, Body right) {
        super(robot_size, robot_size, left, right);
        setName("Robot");
        id = robots++;
        this.left = left;
        this.right = right;
        controllerIndex = 0;

        blue = !Utils.hasDecimal(id / 2.0);
        updateSprite();

        fieldCentric = new FieldCentricStrafe(this);

        Gamepad g = ControllerManager.getGamepad(id);
        changeAlliance = g.getButton(7);
        changeControls = g.getButton(6);
        dozerToggle = g.getButton(1);
        reverseToggle = g.getButton(9);
    }

    public void setIntake(Body b) {
        intake = b;
        addBody(b);
    }

    public void updateSprite() {
        Color c;
        if (blue) c = Main.BLUE;
        else c = Main.RED;
        String tex;
        if (dozer) tex = "core/assets/dozer_recolor.png";
        else tex = "core/assets/robot_recolor.png";
        setSprite(Utils.colorImage(tex, c));

        intakeSprite = new Sprite(Utils.colorImage("core/assets/robot_intake.png", c));
        intakeSprite.setPosition(-999, -999);
    }

    @Override
    public Vector2 getPhysicsPosition() {
        float xAvg = 0, yAvg = 0;
        Body[] important = new Body[]{left, right};
        for (Body b : important) {
            Vector2 pos = b.getPosition();
            xAvg += pos.x;
            yAvg += pos.y;
        }
        xAvg /= important.length;
        yAvg /= important.length;
        return new Vector2(xAvg, yAvg);
    }

    @Override
    public float getPhysicsAngle() {
        float angle = 0;
        Body[] important = new Body[]{left, right};
        for (Body b : important) {
            angle += b.getAngle();
        }
        angle /= important.length;
        return (float) Math.toDegrees(angle);
    }

    boolean changeAllianceWasTrue = false;
    boolean changeControlsWasTrue = false;
    boolean dozerToggleWasTrue = false;
    boolean reverseToggleWasTrue = false;

    float iconAlpha;

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact c) {
        if (metadata != null) metadata.collideStart(this, e, self, other, c);
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact c) {
        if (metadata != null) metadata.onCollide(this, e, self, other, c);
    }

    @Override
    public void collideEnd(Entity e, Body self, Body other, Contact c) {
        if (metadata != null) metadata.collideEnd(this, e, self, other, c);
    }

    @Override
    public void tick() {
        super.tick();

        if (icon != null) {
            icon.setPosition(getX() - icon.getWidth() / 2, getY() + 1f);
            icon.setAlpha(iconAlpha);
            iconAlpha -= 0.01;
            if (iconAlpha <= 0) icon = null;
        }
        if (intakeSprite != null) {
            Vector2 pos = intake.getPosition();
            intakeSprite.setBounds(pos.x - intakeSprite.getWidth()/2, pos.y - intakeSprite.getHeight()/2, robot_size * 2, robot_size / 2);
            intakeSprite.setOriginCenter();
            intakeSprite.setRotation((float)Math.toDegrees(intake.getAngle()));
        }
        float leftMotor, rightMotor;
        Float middleMotor = null;
        if (ControllerManager.getGamepads().isEmpty()) {
            leftMotor = Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0;
            rightMotor = Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0;
        } else {
            Gamepad g = ControllerManager.getGamepad(id);
            /*for (Button b : g.getButtons()) {
                if (b.get()) {
                    Utils.log(b.id + "");
                }
            }*/

            boolean val = changeControls.get();
            if (val && !changeControlsWasTrue) {
                controllerIndex++;
                //Utils.log("Robot " + id + " changing controls (" + g.hasSecondJoystick() + ")");
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
                if (System.currentTimeMillis() - dozerHoldStart >= 2500) {
                    dozerUnlocked = true;
                    Utils.log("Robot " + id + " unlocked Dozer!");
                }
            } else {
                dozerHoldStart = null;
            }
            if (val && !dozerToggleWasTrue && dozerUnlocked) {
                dozer = !dozer;
                updateSprite();
            }
            dozerToggleWasTrue = val;

            val = changeAlliance.get();
            if (val && !changeAllianceWasTrue) {
                blue = !blue;
                updateSprite();
            }
            changeAllianceWasTrue = val;

            val = reverseToggle.get();
            if (val && !reverseToggleWasTrue) {
                g.setReverseSticks(!g.isSticksReversed());
            }
            reverseToggleWasTrue = val;
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
        if (metadata != null) metadata.tick(this);
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);
        if (icon != null) icon.draw(b);
        if (intakeSprite != null) intakeSprite.draw(b);
        if (metadata != null) metadata.draw(b, this);;
    }

    final float k = 10.0f; //2.25

    public void updateMotors(float l, float r) {
        float lAngle = -left.getAngle();
        float rAngle = -right.getAngle();


        float leftX = (maxMps * l) * (float) Math.sin(lAngle);
        float leftY = (maxMps * l) * (float) Math.cos(lAngle);

        left.applyForceToCenter(Utils.cap(k * (leftX - speed.x), maxAccel), Utils.cap(k * (leftY - speed.y), maxAccel), true);


        float rightX = (maxMps * r) * (float) Math.sin(rAngle);
        float rightY = (maxMps * r) * (float) Math.cos(rAngle);

        right.applyForceToCenter(Utils.cap(k * (rightX - speed.x), maxAccel), Utils.cap(k * (rightY - speed.y), maxAccel), true);

        float turnMult = Math.abs(l - r);

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

        /*
        float forceX = ((maxSpeed * 2) * (m * 2)) * (float) Math.sin(getAngle() + 90);
        float forceY = ((maxSpeed * 2) * (m * 2)) * (float) Math.cos(getAngle() + 90);

        left.applyForceToCenter(Utils.cap(forceX, maxAccel * 2), Utils.cap(forceY, maxAccel * 2), true);
        right.applyForceToCenter(Utils.cap(forceX, maxAccel * 2), Utils.cap(forceY, maxAccel * 2), true);*/
    }

    private static void joint(Body a, Body b) {
        WeldJointDef jointDef = new WeldJointDef ();
        jointDef.collideConnected = false;
        jointDef.initialize(a, b, new Vector2(0, 0));
        Main.getInstance().world.createJoint(jointDef);
    }

    public static Robot create(float x, float y) {
        Body left = createRobotPart(x - (robot_size * 1), y);
        Body right = createRobotPart(x, y);
        Body intake = Entity.rectangleDynamicBody(x - (robot_size / 2), y + robot_size * 1.25f, robot_size, robot_size / 4);

        joint(left, right);
        joint(left, intake);
        joint(right, intake);

        Robot r = new Robot(left, right);
        r.setIntake(intake);

        return r;
    }

    private static Body createRobotPart(float x, float y) {
        BodyDef rightDef = new BodyDef();
        rightDef.type = BodyDef.BodyType.DynamicBody;
        rightDef.position.set(x, y);

        Body right = Main.getInstance().world.createBody(rightDef);
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
