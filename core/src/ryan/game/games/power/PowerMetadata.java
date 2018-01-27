package ryan.game.games.power;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.controls.Gamepad;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Part;
import ryan.game.entity.powerup.ClimbingBar;
import ryan.game.entity.powerup.NullTerritory;
import ryan.game.entity.powerup.Pixel;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.power.robots.PowerRobotBase;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerMetadata extends RobotMetadata {

    Sprite chest;

    private boolean intaking = false;

    public boolean armFront = true;

    public int pixels = 0;

    public int ejectGearButton = 5;
    boolean ejectChestWasHeld = false;
    boolean ejectPixelBackWasHeld = false;

    List<Entity> intakeablePixels = new ArrayList<>();
    public HashMap<Entity, Long> pixelIntakeTimes = new HashMap<>();

    public boolean crossedBaseline = false;

    public boolean protectedInNull = false;

    public Long climb = null;

    public PowerMetadata() {
        chest = new Sprite(Pixel.TEXTURE);
        chest.setBounds(-999, -999, .5f, 1f);
    }

    boolean heldArmSwap = false;

    @Override
    public void tick(Robot r) {
        Gamepad gamepad = r.getManipulator();
        PowerRobotBase stats = (PowerRobotBase) r.stats;

        if (gamepad != null) {
            boolean armSwap = gamepad.getButton(Gamepad.ONE);
            if (stats.arm && armSwap && !heldArmSwap) {
                armFront = !armFront;
            }
            heldArmSwap = armSwap;
            if (stats.pixelIntake && (gamepad.getButton(Gamepad.BUMPER_RIGHT) || intaking)) {
                for (Entity e : new ArrayList<>(intakeablePixels)) {
                    if (!intakeablePixels.isEmpty() && pixels < stats.maxPixels) {
                        pixelIntakeTimes.putIfAbsent(e, Main.getTime());
                        double a = Math.toRadians(Utils.getAngle(new Point2D.Float(e.getX(), e.getY()), new Point2D.Float(r.getX(), r.getY())));
                        synchronized (Main.WORLD_USE) {
                            e.getPrimary().applyForceToCenter(stats.pixelIntakeStrength * (float) Math.cos(a), stats.pixelIntakeStrength * (float) Math.sin(a), true);
                        }
                        if (Main.getTime() - pixelIntakeTimes.get(e) >= stats.pixelIntakeTime) {
                            Main.getInstance().removeEntity(e);
                            intakeablePixels.remove(e);
                            pixelIntakeTimes.remove(e);
                            pixels++;
                        }
                    }
                }
            }

            boolean val = gamepad.getButton(Gamepad.BUMPER_LEFT);
            if (val && !ejectChestWasHeld) {
                ejectChest(r, stats.arm ? !armFront : false);
            }
            ejectChestWasHeld = val;

            /*boolean valBack = gamepad.getButton(Gamepad.BUMPER_LEFT);
            if (valBack && stats.outtakeBack && !ejectPixelBackWasHeld) {
                ejectChest(r, true);
            }
            ejectPixelBackWasHeld = valBack;*/
        }
    }

    long nullFoul = 0;

    @Override
    public void collideStart(Robot r, Entity e, Body self, Body other, Contact contact) {
        PowerRobotBase stats = (PowerRobotBase) r.stats;
        if (e instanceof Robot) {
            Robot oR = (Robot) e;
            PowerMetadata oMeta = (PowerMetadata) oR.metadata;
            if (Main.matchPlay && r.blue != oR.blue && oMeta.protectedInNull && Main.getTime() - nullFoul >= 2000) {
                if (r.blue) {
                    Utils.log("The BLUE alliance has gotten a null zone technical foul");
                    PowerDisplay.red_foul += 25;
                } else {
                    Utils.log("The RED alliance has gotten a null zone technical foul");
                    PowerDisplay.blue_foul += 25;
                }
                nullFoul = Main.getTime();
            }
        }
        if (r.isPart("intake", self)) {
            Part intake = r.getPart(self);
            if (stats.arm && ((r.isPart("arm_front", self) && !armFront) || (r.isPart("arm_back", self) && armFront))) {
                contact.setEnabled(false);
                intake.show = false;
                return;
            }
            intake.show = true;
            if (e instanceof Pixel) {
                contact.setEnabled(false);
                if (intakeablePixels.size() < stats.maxPixelIntakeAtOnce && !intakeablePixels.contains(e) && !((Pixel)e).inTall) {
                    intakeablePixels.add(e);
                }
            }
        } else {
            if (e instanceof NullTerritory) {
                NullTerritory n = (NullTerritory) e;
                if (n.blue == r.blue && Main.matchPlay) {
                    protectedInNull = true;
                }
            }
        }
    }

    @Override
    public void onCollide(Robot r, Entity e, Body self, Body other, Contact contact) {}

    @Override
    public void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (r.isPart("intake", self)) {
            if (intakeablePixels.contains(e)) {
                intakeablePixels.remove(e);
                pixelIntakeTimes.remove(e);
            }
        } else {
            if (e instanceof NullTerritory) {
                NullTerritory n = (NullTerritory) e;
                if (n.blue == r.blue) {
                    protectedInNull = false;
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, Robot r) {
        PowerRobotBase stats = (PowerRobotBase) r.stats;
        if (pixels > 1) chest.setSize(.5f, 1f);
        else chest.setSize(.3f, .6f);

        Vector2 pos = r.getPhysicsPosition();
        chest.setPosition(pos.x - chest.getWidth() / 2, pos.y - chest.getHeight() / 2);
        chest.setOriginCenter();
        chest.setRotation(r.getAngle());

        if (pixels > 0) chest.draw(batch);

        if (climb != null && (Game.getMatchTime() <= 30 || !Game.isPlaying())) {
            Utils.drawProgressBar(r.getX(), r.getY() + 1f, 2f, .5f, ((Main.getTime() - climb)/((stats.climbTime*1000))), batch);
        }
    }

    public void setIntaking(boolean b) {
        intaking = b;
    }

    public void ejectChest(Robot r, boolean back) {
        PowerRobotBase stats = (PowerRobotBase) r.stats;
        if (pixels > 0) {
            float distance = 1.25f;
            float angle = r.getAngle();
            if (back) {
               distance = -1.35f;
            }
            float xChange = -distance * (float) Math.sin(Math.toRadians(angle));
            float yChange = distance * (float) Math.cos(Math.toRadians(angle));

            Pixel e = new Pixel(r.getX() + xChange, r.getY() + yChange, r.getAngle());//new Chest(r.getX() + xChange, r.getY() + yChange, r.getAngle(), f.heavy, f.alliance);
            e.owner = r;
            e.ejected = System.currentTimeMillis();

            Main.getInstance().spawnEntity(e);
            synchronized (Main.WORLD_USE) {
                for (Body b : e.getBodies()) {
                    float ejectAngle = -angle;
                    if (back) ejectAngle = angle;
                    float xPow = 10 * (float) Math.sin(Math.toRadians(ejectAngle));
                    float yPow = 10 * (float) Math.cos(Math.toRadians(ejectAngle));
                    b.applyForceToCenter(xPow, yPow, true);
                }
            }
            pixels--;
        }
    }
}