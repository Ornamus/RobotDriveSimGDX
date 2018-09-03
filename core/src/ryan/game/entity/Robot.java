package ryan.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.autonomous.pathmagic.RobotState;
import ryan.game.autonomous.pathmagic.RobotStateGenerator;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.PIDSource;
import ryan.game.competition.Match;
import ryan.game.competition.RobotStats;
import ryan.game.competition.Team;
import ryan.game.controls.Gamepad;
import ryan.game.controls.Gamepads;
import ryan.game.drive.*;
import ryan.game.entity.parts.Part;
import ryan.game.entity.steamworks.Boiler;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.steamworks.robots.*;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;
import ryan.game.sensors.Gyro;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Robot extends Entity {

    public final int id;
    public Body left, right;
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

    //TODO: make this not game-specific
    public static RobotStats[] statsOptions = {new SteamDefault(), new SteamDozer()/*, new SteamGearGod(), new Steam254(), new Steam1902(), new Steam16(), new Steam118(), new SteamGearIntakeGod(),
    new SteamRookie(), new Steam1114(), new StrykeForce(), new SteamSomething()*//*, new SteamTitanium(), new Steam1678()*/};
    //private RobotStats[] statsOptions = {new PowerRobotBase(), new Bacon()};

    public RobotStats stats = statsOptions[statsIndex];

    private int numberIndex = 0;

    public Command auto = null;

    public RobotMetadata metadata = null;

    private int changeAlliance;
    private int changeControls;
    private int reverseToggle;

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

    public boolean controllerDisconnect = false;

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

        if (stats.fieldCentric) fieldCentric = new FieldCentricStrafe(this);
        if (stats.needsStateGenerator) {
            state = new RobotState();
            generator = new RobotStateGenerator(state, this);
            generator.start();
        }
    }

    public void setupButtons(Gamepad g) {
        changeAlliance = Gamepad.SELECT;
        changeControls = Gamepad.START;
        reverseToggle = Gamepad.JOY_LEFT;
    }

    public void setTurret(boolean b) {
        hasTurret = b;
        if (b) {
            turretSprite = new Sprite(new Texture(((SteamRobotStats) stats).turretTexture));
            turretSprite.setPosition(-999, -999);
        }
    }

    public void updateSprite() {
        Color[] newColors;
        Color alliance = blue ? Main.BLUE : Main.RED;
        Team t = null;
        if (GameScreen.schedule != null) t = GameScreen.schedule.getTeam(getNumber());
        if (t != null) {
            newColors = new Color[]{alliance, t.primary, t.secondary};
        } else {
            newColors = new Color[]{alliance, alliance, alliance};
        }
        String tex;
        tex = stats.texture;
        if (stats.recolorIndex == 0) setSprite(Utils.colorImage(tex, newColors));
        else if (stats.recolorIndex == 1) setSprite(Utils.colorImage(tex, null, alliance));
        else if (stats.recolorIndex == 2) setSprite(Utils.colorImage(tex, null, null, alliance));
        else if (stats.recolorIndex == -1) setSprite(Utils.colorImage(tex, stats.custom_primary, alliance, stats.custom_secondary));
        for (Part p : parts) {
            p.onRobotColorChange(alliance);
        }

        /*
        if (intake != null) {
            intakeSprite = new Sprite(Utils.colorImage("core/assets/robot_intake.png", c));
            intakeSprite.setPosition(-999, -999);
        }*/

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

    public void claimGamepad(Gamepad g) {
        g.r = this;
    }

    /**
     *
     * @return If the Robot has been removed from existence.
     */
    public boolean onGamepadDisconnect() {
        if (Game.isPlaying() || GameScreen.MAKE_SCHEDULE) {
            controllerDisconnect = true;
            return false;
        } else {
            destroy();
            GameScreen.popSound.play(0.75f);
            return true;
        }
    }

    public void onGamepadReconnect() {
        controllerDisconnect = false;
    }

    boolean changeAllianceWasTrue = false;
    boolean changeControlsWasTrue = false;
    boolean statsToggleWasTrue = false;
    boolean numberChangeWasTrue = false;
    boolean reverseToggleWasTrue = false;

    float iconAlpha;

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact c) {
        super.collideStart(e, self, other, c);
        if (metadata != null) metadata.collideStart(this, e, self, other, c);
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact c) {
        super.collideStart(e, self, other, c);
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
        }
        rightPosOld = new Vector2(rightPos.x, rightPos.y);

        angleOld = angle;


        if (icon != null) {
            icon.setPosition(getX() - icon.getWidth() / 2, getY() + 1f);
            icon.setAlpha(iconAlpha);
            if (!controllerDisconnect) {
                iconAlpha -= 0.01;
                if (iconAlpha <= 0) icon = null;
            }
        }
        /*if (intakeSprite != null && intake != null) {
            Vector2 pos = intake.getPosition();
            intakeSprite.setBounds(pos.x - intakeSprite.getWidth()/2, pos.y - intakeSprite.getHeight()/2, stats.intakeWidth * 2, stats.robotHeight / 2);
            intakeSprite.setOriginCenter();
            intakeSprite.setRotation((float)Math.toDegrees(intake.getAngle()));
        }*/
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
        }
        if (Gamepads.getGamepads().isEmpty()) {
            //TODO: ?????
        } else {
            Gamepad g = getControllers().get(0);
            setupButtons(g);

            /*
            if (g != null) {
                for (Button b : g.getButtons()) {
                    if (b.get()) Utils.log(b.id + " pressed");
                }
            }*/

            boolean val = g.getButton(changeControls);
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

            if (controllerDisconnect && icon == null) {
                icon = new Sprite(new Texture("core/assets/disconnect.png"));
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

            val = g != null && g.getDPad() == PovDirection.south;
            if (val && !statsToggleWasTrue && !Game.isPlaying()) {
                statsIndex++;
                if (statsIndex >= statsOptions.length) {
                    statsIndex = 0;
                }
                if (!GameScreen.MAKE_SCHEDULE) updateStats();
            }
            statsToggleWasTrue = val;

            val = g != null && g.getDPad() == PovDirection.east;
            if (val && !numberChangeWasTrue && !Game.isPlaying()) {
                numberIndex++;
                if (numberIndex > 2) {
                    numberIndex = 0;
                }
                if (GameScreen.MAKE_SCHEDULE) updateStats();
            }
            numberChangeWasTrue = val;

            val = g.getButton(changeAlliance);
            if (val && !changeAllianceWasTrue && !Game.isPlaying()) {
                blue = !blue;
                metadata = metadata.getNewInstance();
                updateSprite();
                if (GameScreen.MAKE_SCHEDULE) updateStats();
            }
            changeAllianceWasTrue = val;

            val = g.getButton(reverseToggle);
            if (val && !reverseToggleWasTrue && !Game.isPlaying()) {
                g.setReverseSticks(!g.isSticksReversed());
            }
            reverseToggleWasTrue = val;
        }

        if (metadata != null) metadata.tick(this);
    }

    public void updateStats() {
        boolean schedule = GameScreen.MAKE_SCHEDULE;
        RobotStats oldStats = stats;
        stats = statsOptions[statsIndex];

        Team t;
        if (schedule && (t = GameScreen.schedule.getTeam(getNumber())) !=  null) {
            stats = t.robotStats;
            //System.out.println("Using " + t.number + "'s stats.");
        }

        Robot replacement = create(stats, getX()+(oldStats.robotWidth/2), getY(), id);
        for (Gamepad pad : Gamepads.getGamepads(this)) {
            replacement.claimGamepad(pad);
        }

        destroy();

        replacement.blue = blue;
        replacement.statsToggleWasTrue = true;
        replacement.numberChangeWasTrue = true;
        replacement.changeAllianceWasTrue = true;
        replacement.controllerIndex = controllerIndex;
        replacement.statsIndex = statsIndex;
        replacement.numberIndex = numberIndex;
        replacement.metadata = metadata;
        replacement.updateSprite();
        GameScreen.robots.add(replacement);
        Main.spawnEntity(replacement);
    }

    public void destroy() {
        state = null;
        if (generator != null) generator.actuallyStop();
        generator = null;
        Main.removeEntity(this);
        GameScreen.robots.remove(this);
        for (Gamepad g : Gamepads.getGamepads(this)) {
            g.r = null;
        }
    }

    public Vector2 getShooterPosition() {
        Vector2 roPos = getPhysicsPosition();
        Vector2 pos = new Vector2(roPos.x, roPos.y);
        //if (hasTurret) {
            SteamRobotStats steam = (SteamRobotStats) stats;
            Vector2 copy = new Vector2(steam.shooterPosition.x, steam.shooterPosition.y);
            pos.add(copy.rotate(getAngle()));
        //}
        return pos;
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);

        if (icon != null) icon.draw(b);
        //if (intake != null && intakeSprite != null) intakeSprite.draw(b);
        if (metadata != null) metadata.draw(b, this);
        if (hasTurret) {
            SteamRobotStats steam = (SteamRobotStats) stats;
            Vector2 pos = getShooterPosition();
            turretSprite.setBounds(pos.x - turretSprite.getWidth()/2, pos.y - turretSprite.getHeight()/2, steam.turretWidth * 2, steam.turretHeight * 2);
            turretSprite.setOriginCenter();
            turretSprite.setRotation(getAngle() + turretAngle);
            turretSprite.draw(b);
        }

        Vector2 pos = getPhysicsPosition();
        outline.setBounds(pos.x - 1, pos.y - 1.95f, 2f, .7f);
        outline.setAlpha(getAngle() > 110 && getAngle() < 250 ? .3f : .6f);
        outline.draw(b);
    }

    public void drawUnscaled(SpriteBatch b) {
        Match m = GameScreen.schedule.getCurrentMatch();
        if (m != null) {
            Fonts.fmsWhiteVerySmall.setColor(255, 255, 255, getAngle() > 110 && getAngle() < 250 ? .3f : 1);

            Vector2 pos = getPhysicsPosition();
            Fonts.drawCentered(Fonts.fmsWhiteVerySmall, getNumber()+"", pos.x * Main.mtpW, (pos.y * Main.mtpH) + 84, b);

            Fonts.fmsWhiteVerySmall.setColor(255, 255, 255, 1);
        }
    }

    public int getNumber() {
        Match m = GameScreen.schedule.getCurrentMatch();
        if (m != null) {
            if (blue) return m.blue.teams[numberIndex];
            else return m.red.teams[numberIndex];
        }
        return 0;
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

        float maxSpeed = stats.maxMPS;
        Gamepad g = getController();
        if (g != null && stats.maxMPSLow != 0 && (g.isLeftTriggerPressed() || g.isRightTriggerPressed())) {
            maxSpeed = stats.maxMPSLow;
        }

        float leftX = (maxSpeed * l) * (float) Math.sin(lAngle);
        float leftY = (maxSpeed * l) * (float) Math.cos(lAngle);

        left.applyForceToCenter(Utils.cap(k * (leftX - speed.x), stats.maxAccel), Utils.cap(k * (leftY - speed.y), stats.maxAccel), true);


        float rightX = (maxSpeed * r) * (float) Math.sin(rAngle);
        float rightY = (maxSpeed * r) * (float) Math.cos(rAngle);

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
        float angle = (float) gyro.getForPID() + 90f;

        m = Utils.deadzone(m, 0.1f);
        float pow = m * 1;

        float maxSpeed = stats.maxMPS;
        float strafeSpeed = maxSpeed * 5.2f;

        float forceX = (strafeSpeed * pow) * (float) Math.sin(Math.toRadians(angle));
        float forceY = (strafeSpeed  * pow) * (float) Math.cos(Math.toRadians(angle));

        forceX = Math.abs(speed.x) > maxSpeed * stats.fieldCentricStrafeMult ? 0 : forceX;
        forceY = Math.abs(speed.y) > maxSpeed * stats.fieldCentricStrafeMult ? 0 : forceY;

        float accel = stats.maxAccel * 1.7f;

        left.applyForceToCenter(Utils.cap(forceX, accel), Utils.cap(forceY, accel), true);
        right.applyForceToCenter(Utils.cap(forceX, accel), Utils.cap(forceY, accel), true);
    }

    @Override
    public List<Body> getFrictionlessBodies() {
        List<Body> no = new ArrayList<>();
        for (Part p : parts) {
            no.addAll(p.bodies);
        }
        //if (intake != null) no.add(intake);
        return no;
    }

    @Override
    public void addPart(Part p) {
        super.addPart(p);
        for (Body b : p.bodies) {
            weldJoint(left, b);
            weldJoint(right, b);
        }
        p.onRobotColorChange(blue ? Main.BLUE : Main.RED);
    }

    private static void weldJoint(Body a, Body b) {
        synchronized (Main.WORLD_USE) {
            WeldJointDef jointDef = new WeldJointDef();
            jointDef.collideConnected = true;
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

        weldJoint(left, right);

        Robot r = new Robot(stats, left, right, id);
        stats.addParts(x, y, r);

        //TODO: get this out of here
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
            return Gamepads.getGamepad(this);
        } else if (Main.currentRobot == id) {
            return Gamepads.getGamepad(0);
        } else {
            return null;
        }
    }

    public Gamepad getManipulator() {
        List<Gamepad> controllers = getControllers();
        if (!controllers.isEmpty()) {
            if (GameScreen.MANIPULATORS) {
                return controllers.get(1);
            }
            return controllers.get(0);
        }
        return null;
    }

    public List<Gamepad> getControllers() {
        List<Gamepad> gamepads = Gamepads.getGamepads(this);
        if (GameScreen.MANIPULATORS && gamepads.size() == 1) {
            gamepads.add(gamepads.get(0));
        }
        return gamepads;
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

    public static void addStatOption(RobotStats newStats) {
        RobotStats[] newOptions = new RobotStats[statsOptions.length+1];
        System.arraycopy(statsOptions, 0, newOptions, 0, statsOptions.length);
        newOptions[newOptions.length-1] = newStats;
        statsOptions = newOptions;
    }
}