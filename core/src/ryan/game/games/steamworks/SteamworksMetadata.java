package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Match;
import ryan.game.controls.Gamepad;
import ryan.game.entity.*;
import ryan.game.entity.steamworks.Fuel;
import ryan.game.entity.steamworks.Gear;
import ryan.game.entity.steamworks.LoadingZone;
import ryan.game.entity.steamworks.Rope;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.screens.GameScreen;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SteamworksMetadata extends RobotMetadata {

    Sprite gear;

    public boolean crossedBaseline = false;
    public boolean hasGear = false;
    public Long onRope = null;
    public Long gearIntakeStart = null;
    public int fuel = 0;
    public Entity peg = null;
    public List<Entity> intakeableFuel = new ArrayList<>();
    public HashMap<Entity, Long> fuelIntakeTimes = new HashMap<>();
    public Entity intakeableGear = null;

    boolean startedIntakingWithGear = false;
    boolean gearToggleWasTrue = false;

    boolean inWrongLoadingZone = false;

    long timeOfLastFire = 0;

    private final int gearToggle = 5, shoot = 4;

    public SteamworksMetadata() {
        gear = new Sprite(Gear.TEXTURE);
        gear.setBounds(-999, -999, 1f, 1f);
    }

    @Override
    public void tick(Robot r) {
        Gamepad gamepad = r.getController();
        SteamRobotStats stats = (SteamRobotStats) r.stats;

        boolean gearIntake = stats.gearIntake;
        boolean fuelIntake = stats.fuelIntake;

        if (gamepad != null) {
            boolean val = gamepad.getButton(gearToggle);

            if (val && !gearToggleWasTrue) {
                startedIntakingWithGear = hasGear;
            }

            if (val) {
                if (hasGear && startedIntakingWithGear) {
                    ejectGear(r);
                }

                if (!hasGear && intakeableGear != null && !startedIntakingWithGear && gearIntake) {
                    if (gearIntakeStart == null) gearIntakeStart = GameScreen.getTime();
                    double a = Math.toRadians(Utils.getAngle(new Point2D.Float(intakeableGear.getX(), intakeableGear.getY()), new Point2D.Float(r.getX(), r.getY())));
                    synchronized (Main.WORLD_USE) {
                        intakeableGear.getPrimary().applyForceToCenter(stats.gearIntakeStrength * (float) Math.cos(a), stats.gearIntakeStrength * (float) Math.sin(a), true);
                    }
                    if (GameScreen.getTime() - gearIntakeStart >= stats.gearIntakeRate) {
                        Main.getInstance().removeEntity(intakeableGear);
                        intakeableGear = null;
                        hasGear = true;
                        gearIntakeStart = null;
                    }
                }

                if ( fuelIntake) {
                    for (Entity e : new ArrayList<>(intakeableFuel)) {
                        if (!intakeableFuel.isEmpty() && fuel < stats.maxFuel) {
                            fuelIntakeTimes.putIfAbsent(e, GameScreen.getTime());
                            double a = Math.toRadians(Utils.getAngle(new Point2D.Float(e.getX(), e.getY()), new Point2D.Float(r.getX(), r.getY())));
                            synchronized (Main.WORLD_USE) {
                                e.getPrimary().applyForceToCenter(stats.fuelIntakeStrength * (float) Math.cos(a), stats.fuelIntakeStrength * (float) Math.sin(a), true);
                            }
                            if (GameScreen.getTime() - fuelIntakeTimes.get(e) >= stats.fuelIntakeRate) {
                                Main.getInstance().removeEntity(e);
                                intakeableFuel.remove(e);
                                fuelIntakeTimes.remove(e);
                                fuel++;
                            }
                        }
                    }
                }

            }
            gearToggleWasTrue = val;

            if (gamepad.getButton(shoot)) {
                shootFuel(r);
            }
        }
    }

    long zoneHit = 0;
    long ropeTouch = 0;

    @Override
    public void collideStart(Robot r, Entity e, Body self, Body other, Contact contact) {
        SteamRobotStats stats = (SteamRobotStats) r.stats;
        if (r.isPart("intake", self)) {
            boolean canGear = !stats.differentiateBetweenIntakes || r.isPart("gear", self);
            boolean canFuel = !stats.differentiateBetweenIntakes || r.isPart("fuel", self);
            if (e.getName().equalsIgnoreCase("peg")) {
                if (canGear) peg = e;
                contact.setEnabled(false);
            } else if (e instanceof Gear) {
                if (canGear) intakeableGear = e;
                contact.setEnabled(false);
            } else if (e instanceof Fuel) {
                if (canFuel && !intakeableFuel.contains(e)) intakeableFuel.add(e);
                contact.setEnabled(false);
            }
        }
        if (e instanceof LoadingZone && !r.isPart("intake", self)) {
            if (((LoadingZone)e).blue != r.blue) {
                inWrongLoadingZone = true;
            }
        }
        if (e instanceof Rope) {
            Rope rope = (Rope) e;
            rope.robotTouching = true;
            if (rope.blue == r.blue && stats.climber && (Game.getMatchTime() <= 30 || !Game.isPlaying())) {
                Match m = GameScreen.schedule.getCurrentMatch();
                boolean numMatch = false;
                if (m != null) {
                    if (rope.blue) numMatch = r.getNumber() == m.blue.teams[rope.id];
                    else numMatch = r.getNumber() == m.red.teams[rope.id];
                }
                if (m == null || (m != null && numMatch)) {
                    if (onRope == null) onRope = GameScreen.getTime();
                }
            }
        }
    }

    @Override
    public void onCollide(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Robot) {
            Robot otherR = (Robot) e;
            if (r.blue != otherR.blue && Game.isPlaying()) {
                if (onRope != null && Game.getMatchTime() <= 30 && GameScreen.getTime() - ropeTouch >= 2000) {
                    if (r.blue) Steamworks.blue.bonusClimbs++;
                    else Steamworks.red.bonusClimbs++;
                    ropeTouch = GameScreen.getTime();
                    //TODO: some sort of foul visual
                } else if (inWrongLoadingZone && GameScreen.getTime() - zoneHit >= 2000) {
                    if (r.blue) Steamworks.blue.fouls += 25;
                    else Steamworks.red.fouls += 25;
                    zoneHit = GameScreen.getTime();
                    //TODO: some sort of foul visual
                }
            }
        }
    }

    @Override
    public void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact) {
        SteamRobotStats stats = (SteamRobotStats) r.stats;
        boolean canGear = !stats.differentiateBetweenIntakes || r.isPart("gear", self);
        boolean canFuel = !stats.differentiateBetweenIntakes || r.isPart("fuel", self);
        if (r.isPart("intake", self)) {
            if (e == peg && canGear) {
                peg = null;
            } else if (e == intakeableGear && canGear) {
                intakeableGear = null;
                gearIntakeStart = null;
            } else if (intakeableFuel.contains(e) && canFuel) {
                intakeableFuel.remove(e);
                fuelIntakeTimes.remove(e);
            }
        }
        if (e instanceof LoadingZone && !r.isPart("intake", self)) {
            if (((LoadingZone)e).blue != r.blue) {
                inWrongLoadingZone = false;
            }
        }
        if (e instanceof Rope) {
            onRope = null;
            ((Rope)e).robotTouching = false;
        }
    }

    @Override
    public void draw(SpriteBatch batch, Robot r) {
        Vector2 pos = r.getPhysicsPosition();
        gear.setPosition(pos.x - gear.getWidth() / 2, pos.y - gear.getHeight() / 2);
        gear.setOriginCenter();
        gear.setRotation(r.getAngle());

        SteamRobotStats stats = (SteamRobotStats) r.stats;
        if (fuel > 0) {
            float size = 1 * (fuel / stats.maxFuel);
            batch.draw(fuel == stats.maxFuel ? Fuel.TEXTURE_MAX : Fuel.TEXTURE, pos.x - (size / 2), pos.y - (size / 2), size, size);
        }
        if (hasGear) gear.draw(batch);
        if (onRope != null && (Game.getMatchTime() <= 30 || !Game.isPlaying())) {
            Utils.drawProgressBar(r.getX(), r.getY() + 1f, 2f, .5f, ((GameScreen.getTime() - onRope)/((stats.climbSpeed*1000))), batch);
        }
    }

    public void ejectGear(Robot r) {
        boolean canScore = false;
        if (peg != null) {
            float diff = Math.abs(r.getAngle() - peg.getAngle());
            if (Math.abs(diff - 270) <= 15 || Math.abs(diff - 90) <= 15) canScore = true;
        }
        if (hasGear) {
            if (!canScore) {
                //drop gear
                float distance = 1.25f; //1.75f
                float xChange = -distance * (float) Math.sin(Math.toRadians(r.getAngle()));
                float yChange = distance * (float) Math.cos(Math.toRadians(r.getAngle()));

                Entity e = new Gear(r.getX() + xChange, r.getY() + yChange, r.getAngle());

                Main.getInstance().spawnEntity(e);
                synchronized (Main.WORLD_USE) {
                    for (Body b : e.getBodies()) {
                        float xPow = 50 * (float) Math.sin(Math.toRadians(-r.getAngle()));
                        float yPow = 50 * (float) Math.cos(Math.toRadians(-r.getAngle()));
                        b.applyForceToCenter(xPow, yPow, true);
                    }
                }
                hasGear = false;
            } else {
                hasGear = false;
                if (GameScreen.matchPlay) {
                    if (peg.getX() > 0) {
                        Steamworks.blue.gearQueue++;
                    } else {
                        Steamworks.red.gearQueue++;
                    }
                    //if (r.blue) Steamworks.blue.gearQueue++;
                    //else Steamworks.red.gearQueue++;
                }
            }
        }
    }

    public void shootFuel(Robot r) {
        SteamRobotStats stats = (SteamRobotStats) r.stats;
        if (fuel > 0 && GameScreen.getTime() - timeOfLastFire >= stats.timePerShoot && stats.shooter) {


            Vector2 shootPos = r.getShooterPosition();
            Fuel f = new Fuel(shootPos.x, shootPos.y, false);//shootFuel(getX() + xChange, getY() + yChange, 1);
            f.setAirMomentum(stats.shootHeight + (Utils.randomFloat(-stats.shootHeightVariance, stats.shootHeightVariance)));
            f.setShot();
            Main.getInstance().spawnEntity(.2f, f);
            for (Body b : f.getBodies()) {
                float pow = stats.shootPower + (Utils.randomFloat(-stats.shootPowerVariance, stats.shootPowerVariance));

                float angle;
                if (r.hasTurret) {
                    angle = -r.getAngle() - (r.turretAngle+90);//(float)Math.toDegrees(-r.turret.getAngle())-90;
                } else {
                    angle = -r.getAngle();
                }
                angle += Utils.randomFloat(-stats.shootAngleVariance, stats.shootAngleVariance);
                angle = Utils.fixAngle(angle);

                float xPow = pow * (float) Math.sin(Math.toRadians(angle));
                float yPow = pow * (float) Math.cos(Math.toRadians(angle));
                synchronized (Main.WORLD_USE) {
                    b.applyForceToCenter(xPow, yPow, true);
                }
            }
            fuel--;
            timeOfLastFire = GameScreen.getTime();
        }
    }
}
