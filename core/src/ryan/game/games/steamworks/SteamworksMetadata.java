package ryan.game.games.steamworks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.controls.Gamepad;
import ryan.game.entity.*;
import ryan.game.entity.steamworks.Fuel;
import ryan.game.entity.steamworks.Gear;
import ryan.game.entity.steamworks.LoadingZone;
import ryan.game.entity.steamworks.Rope;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.steamworks.robots.SteamRobotStats;

public class SteamworksMetadata extends RobotMetadata {

    Sprite gear;

    public boolean crossedBaseline = false;
    public boolean hasGear = false;
    public Long onRope = null;
    public int fuel = 0;
    public Entity peg = null;
    public Entity intakeableFuel = null;
    public Entity intakeableGear = null;

    boolean startedIntakingWithGear = false;
    boolean gearToggleWasTrue = false;

    boolean inLoadingZone = false;

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

        gear.setPosition(r.getX() - gear.getWidth() / 2, r.getY() - gear.getHeight() / 2);
        gear.setOriginCenter();
        gear.setRotation(r.getAngle());

        if (gamepad != null) {
            boolean val = gamepad.getButton(gearToggle).get();

            if (val && !gearToggleWasTrue) {
                startedIntakingWithGear = hasGear;
            }

            if (val) {
                boolean canScore = false;
                if (peg != null) {
                    float diff = Math.abs(r.getAngle() - peg.getAngle());
                    if (Math.abs(diff - 270) < 6 || Math.abs(diff - 90) < 6) canScore = true;
                }
                if (hasGear && startedIntakingWithGear) {
                    ejectGear(r);
                }

                if (!hasGear && intakeableGear != null && !startedIntakingWithGear && gearIntake) {
                    Main.getInstance().removeEntity(intakeableGear);
                    intakeableGear = null;
                    hasGear = true;
                }

                if (intakeableFuel != null && fuel < stats.maxFuel && fuelIntake) {
                    Main.getInstance().removeEntity(intakeableFuel);
                    intakeableFuel = null;
                    fuel++;
                }

            }
            gearToggleWasTrue = val;

            if (gamepad.getButton(shoot).get()) {
                shootFuel(r);
            }
        }
    }

    long zoneHit = 0;
    long ropeTouch = 0;

    @Override
    public void collideStart(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (self == r.intake) {
            if (e.getName().equalsIgnoreCase("peg")) {
                peg = e;
                contact.setEnabled(false);
            } else if (e instanceof Gear) {
                intakeableGear = e;
                contact.setEnabled(false);
            } else if (e instanceof Fuel) {
                intakeableFuel = e;
                contact.setEnabled(false);
            }
        }
        if (e instanceof LoadingZone && self != r.intake) {
            if (((LoadingZone)e).blue == r.blue) {
                inLoadingZone = true;
            }
        }
        if (e instanceof Rope) {
            if (((Rope)e).blue == r.blue) {
                if (onRope == null) onRope = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onCollide(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Robot) {
            Robot otherR = (Robot) e;
            if (r.blue != otherR.blue && Game.isPlaying()) {
                if (onRope != null && Game.getMatchTime() <= 30 && System.currentTimeMillis() - ropeTouch >= 2000) {
                    if (r.blue) SteamworksField.blueBonusClimbs++;
                    else SteamworksField.redBonusClimbs++;
                    ropeTouch = System.currentTimeMillis();
                    //TODO: some sort of foul visual
                } else if (inLoadingZone && System.currentTimeMillis() - zoneHit >= 2000) {
                    if (r.blue) SteamworksField.redFouls += 25;
                    else SteamworksField.blueFouls += 25;
                    zoneHit = System.currentTimeMillis();
                    //TODO: some sort of foul visual
                }
            }
        }
    }

    @Override
    public void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (self == r.intake) {
            if (e == peg) {
                peg = null;
                //Utils.log("off peg");
            } else if (e == intakeableGear) {
                intakeableGear = null;
            } else if (e == intakeableFuel) {
                intakeableFuel = null;
            }
        }
        if (e instanceof LoadingZone && self != r.intake) {
            if (((LoadingZone)e).blue == r.blue) {
                inLoadingZone = false;
            }
        }
        if (e instanceof Rope) {
            onRope = null;
        }
    }

    @Override
    public void draw(SpriteBatch batch, Robot r) {
        SteamRobotStats stats = (SteamRobotStats) r.stats;
        if (fuel > 0) {
            float size = 1 * (fuel / stats.maxFuel);
            batch.draw(fuel == stats.maxFuel ? Fuel.TEXTURE_MAX : Fuel.TEXTURE, r.getX() - (size / 2), r.getY() - (size / 2), size, size);
        }
        if (hasGear) gear.draw(batch);
        if (onRope != null && Game.getMatchTime() <= 30) {
            Utils.drawProgressBar(r.getX(), r.getY() + 1f, 2f, .5f, ((System.currentTimeMillis() - onRope)/((stats.climbSpeed*1000))), batch);
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

                Entity e = Gear.create(r.getX() + xChange, r.getY() + yChange, r.getAngle());

                Main.getInstance().spawnEntity(e);
                for (Body b : e.getBodies()) {
                    float xPow = 50 * (float) Math.sin(Math.toRadians(-r.getAngle()));
                    float yPow = 50 * (float) Math.cos(Math.toRadians(-r.getAngle()));
                    b.applyForceToCenter(xPow, yPow, true);
                }
                hasGear = false;
            } else {
                hasGear = false;
                if (Main.matchPlay) {
                    if (r.blue) SteamworksField.blueGearQueue++;
                    else SteamworksField.redGearQueue++;
                    /*
                    if (Game.isAutonomous()) {
                        if (r.blue) SteamworksField.blueGearsInAuto++;
                        else SteamworksField.redGearsInAuto++;
                    }*/
                }
            }
        }
    }

    public void shootFuel(Robot r) {
        SteamRobotStats stats = (SteamRobotStats) r.stats;
        if (fuel > 0 && System.currentTimeMillis() - timeOfLastFire >= stats.timePerShoot && stats.shooter) {
            float distance = 1.25f; //1.75f
            float xChange = -distance * (float) Math.sin(Math.toRadians(r.getAngle()));
            float yChange = distance * (float) Math.cos(Math.toRadians(r.getAngle()));
            //Utils.log("angle: " + getAngle());

            Entity f = Fuel.create(r.getX() + xChange, r.getY() + yChange, false);//shootFuel(getX() + xChange, getY() + yChange, 1);
            f.setAirMomentum(1);
            Main.getInstance().spawnEntity(.2f, f);
            for (Body b : f.getBodies()) {
                float xPow = 25 * (float) Math.sin(Math.toRadians(-r.getAngle()));
                float yPow = 25 * (float) Math.cos(Math.toRadians(-r.getAngle()));
                b.applyForceToCenter(xPow, yPow, true);
            }
            fuel--;
            timeOfLastFire = System.currentTimeMillis();
        }
    }
}
