package ryan.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.autonomous.pathmagic.RobotState;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.PIDSource;
import ryan.game.competition.RobotStats;
import ryan.game.competition.Team;
import ryan.game.controls.Button;
import ryan.game.controls.ControllerManager;
import ryan.game.controls.FakeButton;
import ryan.game.controls.Gamepad;
import ryan.game.drive.*;
import ryan.game.autonomous.pathmagic.RobotStateGenerator;
import ryan.game.entity.steamworks.Boiler;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.overboard.robots.OverRobotStats;
import ryan.game.games.steamworks.robots.*;
import ryan.game.render.Drawable;
import ryan.game.render.Fonts;
import ryan.game.sensors.Gyro;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Robot extends Entity {

    public final int id;
    public Body left, right, intake = null;
    public boolean hasTurret = false;
    private int controllerIndex;
    private DriveController[] scrollOptions = {new Arcade(false), new Arcade(true), new Tank(), new CheesyDrive()};
    private FieldCentricStrafe fieldCentric = null;

    private Gyro gyro = null;
    private PIDSource leftEncoder = null;
    private PIDSource rightEncoder = null;
    public RobotState state = null;
    public RobotStateGenerator generator = null;

    private float leftMotor = 0, rightMotor = 0;
    private float middleMotor = 0;

    private int statsIndex = 0;
    private RobotStats[] statsOptions = {new SteamDefault(), new SteamDozer(), new SteamGearGod(), new Steam254(), new Steam1902(), new Steam16(), new Steam118()};
    //private RobotStats[] statsOptions = {new OverRobotStats()};
    public RobotStats stats = statsOptions[statsIndex];

    private int numberIndex = 0;

    public Command auto = null;

    public RobotMetadata metadata = null;

    private Button changeAlliance;
    private Button changeControls;
    private Button reverseToggle;

    private float maxTurn = 1.5f;
    public boolean blue;

    private static final Texture joyTex = new Texture(Gdx.files.internal("core/assets/joystick.png"));
    private static final Texture joysTex = new Texture(Gdx.files.internal("core/assets/joysticks.png"));
    private static final Texture tankTex = new Texture(Gdx.files.internal("core/assets/tank.png"));
    private static final Texture cheeseTex = new Texture(Gdx.files.internal("core/assets/cheese.png"));

    private Sprite intakeSprite;
    private Sprite icon;
    private Sprite turretSprite = null;
    private Sprite outline = null;

    public float turretAngle = 0;

    private static int robots = 0;

    private Robot(RobotStats stats, Body left, Body right, int id) {
        super(stats.robotWidth, stats.robotHeight, left, right);
        this.stats = stats;
        setName("Robot");
        this.id =id;
        this.left = left;
        this.right = right;
        controllerIndex = 0;

        blue = !Utils.hasDecimal(id / 2.0);
        updateSprite();

        setupButtons(getController());

        gyro = new Gyro(this);

        leftEncoder = new PIDSource() {
            double fakeReset = 0;
            @Override
            public double getForPID() {
                return leftDistance - fakeReset;
            }

            @Override
            public void reset() {
                fakeReset = leftDistance;
            }
        };

        rightEncoder = new PIDSource() {
            double fakeReset = 0;
            @Override
            public double getForPID() {
                return rightDistance - fakeReset;
            }

            @Override
            public void reset() {
                fakeReset = rightDistance;
            }
        };

        state = new RobotState();
        generator = new RobotStateGenerator(state, this);
        generator.start();

        if (stats.fieldCentric) fieldCentric = new FieldCentricStrafe(this);
    }

    public void setupButtons(Gamepad g) {
        if (g != null) {
            changeAlliance = g.getButton(7);
            changeControls = g.getButton(6);
            reverseToggle = g.getButton(9);
        } else {
            changeAlliance = new FakeButton();
            changeControls = new FakeButton();
            reverseToggle = new FakeButton();
        }
    }

    public void setIntake(Body b) {
        intake = b;
        addBody(b);
    }

    public void setTurret(boolean b) {
        hasTurret = b;
        if (b) {
            turretSprite = new Sprite(new Texture(((SteamRobotStats) stats).turretTexture));
            turretSprite.setPosition(-999, -999);
        }
    }

    public void updateSprite() {
        Color c;
        if (blue) c = Main.BLUE;
        else c = Main.RED;
        String tex;
        tex = stats.texture;
        //if (dozer) tex = "core/assets/dozer_recolor.png";
        //else tex = "core/assets/robot_recolor.png";
        if (tex.contains("254") || tex.contains("1902") || tex.contains("16")) {
            setSprite(Utils.colorImage(tex, null, c));
        } else if (tex.contains("118")) {
            setSprite(Utils.colorImage(tex, null, null, c));
        } else {
            setSprite(Utils.colorImage(tex, c));
        }

        intakeSprite = new Sprite(Utils.colorImage("core/assets/robot_intake.png", c));
        intakeSprite.setPosition(-999, -999);

        outline = new Sprite(Utils.colorImage("core/assets/whitepixel.png", blue ? Main.BLUE : Main.RED));
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
            if (b != null)
            angle += b.getAngle();
        }
        angle /= important.length;
        return (float) Math.toDegrees(angle);
    }

    public float getAngularVelocity() {
        float vel = 0;
        Body[] important = new Body[]{left, right};
        for (Body b : important) {
            vel += b.getAngularVelocity();
        }
        vel /= important.length;
        return vel;
    }

    boolean changeAllianceWasTrue = false;
    boolean changeControlsWasTrue = false;
    boolean statsToggleWasTrue = false;
    boolean numberChangeWasTrue = false;
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

    float leftDistance = 0, rightDistance = 0;
    Vector2 leftPosOld = null, rightPosOld = null;
    float angleOld = 0;

    @Override
    public void tick() {
        super.tick();


        float angle = (float) Math.toRadians(getPhysicsAngle());

        Vector2 leftPos = left.getPosition();
        if (leftPosOld != null) {
            Vector2 diff = new Vector2(leftPos.x, leftPos.y).sub(leftPosOld);
            diff = diff.rotateRad(-left.getAngle());
            diff = diff.scl(1000);
            float angleDiff = angle - angleOld;
            leftDistance += diff.y;
            leftDistance += (angleDiff * (stats.robotWidth / 2)) * 1000;
            //Utils.log(Utils.roundToPlace(rightDistance, 0) + "");
        }
        leftPosOld = new Vector2(leftPos.x, leftPos.y);

        Vector2 rightPos = right.getPosition();
        if (rightPosOld != null) {
            Vector2 diff = new Vector2(rightPos.x, rightPos.y).sub(rightPosOld);
            diff = diff.rotateRad(-right.getAngle());
            diff = diff.scl(1000);
            float angleDiff = angle - angleOld;
            rightDistance += diff.y;
            rightDistance += (angleDiff * (stats.robotWidth / 2)) * 1000;
            //Utils.log(Utils.roundToPlace(rightDistance, 0) + "");
        }
        rightPosOld = new Vector2(rightPos.x, rightPos.y);

        angleOld = angle;


        if (icon != null) {
            icon.setPosition(getX() - icon.getWidth() / 2, getY() + 1f);
            icon.setAlpha(iconAlpha);
            iconAlpha -= 0.01;
            if (iconAlpha <= 0) icon = null;
        }
        if (intakeSprite != null) {
            Vector2 pos = intake.getPosition();
            intakeSprite.setBounds(pos.x - intakeSprite.getWidth()/2, pos.y - intakeSprite.getHeight()/2, stats.intakeWidth * 2, stats.robotHeight / 2);
            intakeSprite.setOriginCenter();
            intakeSprite.setRotation((float)Math.toDegrees(intake.getAngle()));
        }
        outline.setBounds(getX() - 1, getY() - 1.95f, 2f, .7f);
        outline.setAlpha(getAngle() > 110 && getAngle() < 250 ? .3f : .6f);
        if (hasTurret) {
            SteamRobotStats steam = (SteamRobotStats) stats;
            for (Entity e : Main.getInstance().getEntities()) {
                if (e instanceof Boiler) {
                    Boiler b = (Boiler) e;
                    if (b.blue == blue) {
                        float dis = Utils.distance(getX(), getY(), b.getX(), b.getY());
                        if (dis < 15) {
                            float angleWanted = (float) Utils.getAngle(new Point2D.Float(getX(), getY()), new Point2D.Float(b.getX(), b.getY()));
                            float currentRealPos = Utils.fixAngle(turretAngle + getPhysicsAngle());

                            float diff = Utils.fixAngle((angleWanted+180) - currentRealPos);
                            if (diff > 180) diff = -(diff - 180);

                            if (Math.abs(diff) > 0.25) {
                                turretAngle += ((Math.abs(diff)*.08) * Utils.sign(diff));
                            }
                        }
                    }
                }
            }
            turretAngle = Utils.fixAngle(turretAngle);
            while (turretAngle > 360) turretAngle -= 360;
            while (turretAngle < 0) turretAngle = 360 + turretAngle;
            Vector2 pos = getTurretPosition();
            turretSprite.setBounds(pos.x - turretSprite.getWidth()/2, pos.y - turretSprite.getHeight()/2, steam.turretWidth * 2, steam.turretHeight * 2);
            turretSprite.setOriginCenter();
            turretSprite.setRotation(getAngle() + turretAngle);
        }
        if (ControllerManager.getGamepads().isEmpty()) {
            //TODO: ?????
        } else {
            Gamepad g = getController();
            setupButtons(g);

            /*
            if (g != null) {
                for (Button b : g.getButtons()) {
                    if (b.get()) Utils.log(b.id + " pressed");
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

            if ((!Game.isPlaying() || Game.getMatchTime() <= 134) && g != null) {
                DriveOrder o;
                if (stats.fieldCentric) {
                    o = fieldCentric.calculate(g);
                } else {
                    o = scrollOptions[controllerIndex].calculate(g);
                }

                setMotors(o.left, o.right);
                if (o.hasMiddle()) {
                    middleMotor = Utils.cap(o.middle, 1);
                }
            }
            updateMotors(leftMotor, rightMotor);
            if (stats.fieldCentric) {
                updateMiddleMotor(middleMotor);
            } else {
                doFriction(left);
                doFriction(right);
            }

            val = g != null && g.getDPad() == .75;
            if (val && !statsToggleWasTrue && !Game.isPlaying()) {
                statsIndex++;
                if (statsIndex >= statsOptions.length) {
                    statsIndex = 0;
                }
                RobotStats oldStats = stats;
                stats = statsOptions[statsIndex];
                state = null;
                generator.actuallyStop();
                generator = null;
                Main.getInstance().removeEntity(this);
                Main.robots.remove(this);
                Robot replacement = create(stats, getX()+(oldStats.robotWidth/2), getY(), id);
                replacement.blue = blue;
                replacement.statsToggleWasTrue = true;
                replacement.controllerIndex = controllerIndex;
                replacement.statsIndex = statsIndex;
                replacement.numberIndex = numberIndex;
                replacement.metadata = metadata;
                replacement.updateSprite();
                Main.robots.add(replacement);
                Main.getInstance().spawnEntity(replacement);
            }
            statsToggleWasTrue = val;

            val = g != null && g.getDPad() == .5;
            if (val && !numberChangeWasTrue && !Game.isPlaying()) {
                numberIndex++;
                if (numberIndex > 2) {
                    numberIndex = 0;
                }
            }
            numberChangeWasTrue = val;

            val = changeAlliance.get();
            if (val && !changeAllianceWasTrue && !Game.isPlaying()) {
                blue = !blue;
                updateSprite();
            }
            changeAllianceWasTrue = val;

            val = reverseToggle.get();
            if (val && !reverseToggleWasTrue && !Game.isPlaying()) {
                g.setReverseSticks(!g.isSticksReversed());
            }
            reverseToggleWasTrue = val;
        }

        if (metadata != null) metadata.tick(this);
    }

    public Vector2 getTurretPosition() {
        Vector2 pos = new Vector2(getX(), getY());
        if (hasTurret) {
            SteamRobotStats steam = (SteamRobotStats) stats;
            Vector2 copy = new Vector2(steam.shooterTurretPivot.x, steam.shooterTurretPivot.y);
            pos.add(copy.rotate(getAngle()));
        }
        return pos;
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);

        if (icon != null) icon.draw(b);
        if (intakeSprite != null) intakeSprite.draw(b);
        if (metadata != null) metadata.draw(b, this);
        if (hasTurret) turretSprite.draw(b);
        outline.draw(b);
    }

    public void drawUnscaled(SpriteBatch b) {
        Team t;
        if (blue) t = Main.schedule.getCurrentMatch().blue[numberIndex];
        else t = Main.schedule.getCurrentMatch().red[numberIndex];
        Fonts.fmsWhiteSmall.setColor(255, 255, 255, getAngle() > 110 && getAngle() < 250 ? .3f : 1);
        Fonts.drawCentered(t.number + "", getX() * Main.meterToPixelWidth, (getY()*Main.meterToPixelHeight) + (Main.meterToPixelHeight*2.7f), Fonts.fmsWhiteSmall, b);
        Fonts.fmsWhiteSmall.setColor(255, 255, 255, 1);
    }

    final float k = 10.0f; //2.25

    public void setMotors(float l, float r) {
        leftMotor = Utils.cap(l, 1);
        rightMotor = Utils.cap(r, 1);
    }

    public void setMiddleMotor(float m) {
        middleMotor = m;
    }

    public void updateMotors(float l, float r) {
        float lAngle = -left.getAngle();
        float rAngle = -right.getAngle();

        float leftX = (stats.maxMPS * l) * (float) Math.sin(lAngle);
        float leftY = (stats.maxMPS * l) * (float) Math.cos(lAngle);

        left.applyForceToCenter(Utils.cap(k * (leftX - speed.x), stats.maxAccel), Utils.cap(k * (leftY - speed.y), stats.maxAccel), true);


        float rightX = (stats.maxMPS * r) * (float) Math.sin(rAngle);
        float rightY = (stats.maxMPS * r) * (float) Math.cos(rAngle);

        right.applyForceToCenter(Utils.cap(k * (rightX - speed.x), stats.maxAccel), Utils.cap(k * (rightY - speed.y), stats.maxAccel), true);

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

        float angle = (float) gyro.getForPID() + 90f;
        //Utils.log("Pow: " + m + ", Angle of movement: " + angle);

        //Utils.log("Bot angle: " + Utils.roundToPlace(((float)gyro.getForPID() + 90), 2));
        m = Utils.deadzone(m, 0.1f);
        float pow = m * 1;

        float strafeSpeed = stats.maxMPS * 6;

        float forceX = (strafeSpeed * pow) * (float) Math.sin(Math.toRadians(angle));
        float forceY = (strafeSpeed  * pow) * (float) Math.cos(Math.toRadians(angle));

        forceX = Math.abs(speed.x) > stats.maxMPS * stats.fieldCentricStrafeMult ? 0 : forceX;
        forceY = Math.abs(speed.y) > stats.maxMPS * stats.fieldCentricStrafeMult ? 0 : forceY;

        float accel = stats.maxAccel * 2f;

        left.applyForceToCenter(Utils.cap(forceX, accel), Utils.cap(forceY, accel), true);
        right.applyForceToCenter(Utils.cap(forceX, accel), Utils.cap(forceY, accel), true);
    }

    @Override
    public List<Body> getFrictionlessBodies() {
        List<Body> no = new ArrayList<>();
        no.add(intake);
        return no;
    }

    private static void weldJoint(Body a, Body b) {
        synchronized (Main.WORLD_USE) {
            WeldJointDef jointDef = new WeldJointDef();
            jointDef.collideConnected = false;
            jointDef.initialize(a, b, new Vector2(0, 0));
            Main.getInstance().world.createJoint(jointDef);
        }
    }

    private static void revoluteJoint(Body a, Body b, Vector2 anchor) {
        synchronized (Main.WORLD_USE) {
            RevoluteJointDef def = new RevoluteJointDef();
            def.initialize(a, b, new Vector2(0,0));
            def.collideConnected = false;
            def.enableMotor = false;
            def.localAnchorA.set(anchor.x, anchor.y);
            def.localAnchorB.set(0,0);
            Main.getInstance().world.createJoint(def);
        }
    }

    public static Robot create(RobotStats stats, float x, float y) {
        return create(stats, x, y, robots++);
    }

    public static Robot create(RobotStats stats, float x, float y, int id) {
        Body left = createRobotPart(stats, x - stats.robotWidth, y);
        Body right = createRobotPart(stats, x, y);

        float width = stats.intakeWidth, height = stats.robotHeight / 4;
        Body intake = BodyFactory.getRectangleDynamic(x - (stats.robotWidth/2), y + stats.robotHeight * 1.25f, width, height, width*height);

        weldJoint(left, right);
        weldJoint(left, intake);
        weldJoint(right, intake);

        Robot r = new Robot(stats, left, right, id);
        r.setIntake(intake);

        if (stats instanceof SteamRobotStats) {
            SteamRobotStats steam = (SteamRobotStats) stats;
            if (steam.shooterIsTurret) {
                r.setTurret(true);
            }
        }

        return r;
    }

    private static Body createRobotPart(RobotStats stats, float x, float y) {
        return BodyFactory.getRectangleDynamic(x, y, stats.robotWidth/2, stats.robotHeight, .5f);
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

    public Gamepad getController() {
        if (Main.currentRobot == -1) {
            return ControllerManager.getGamepad(id);
        } else if (Main.currentRobot == id) {
            return ControllerManager.getGamepad(0);
        } else {
            return null;
        }
    }

    public Gyro getGyro() {
        return gyro;
    }

    public PIDSource getLeftEncoder() {
        return leftEncoder;
    }

    public PIDSource getRightEncoder() {
        return rightEncoder;
    }
}
