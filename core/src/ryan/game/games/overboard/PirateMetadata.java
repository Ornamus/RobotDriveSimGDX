package ryan.game.games.overboard;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.controls.Gamepad;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.entity.overboard.Chest;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.games.overboard.robots.OverRobotStats;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PirateMetadata extends RobotMetadata {

    Sprite chest;

    private boolean intaking = false;

    public List<ChestInfo> chests = new ArrayList<>();

    public int ejectGearButton = 5;
    boolean ejectChestWasHeld = false;

    List<Entity> intakeableChests = new ArrayList<>();
    public HashMap<Entity, Long> chestIntakeTimes = new HashMap<>();

    public PirateMetadata() {
        chest = new Sprite(Utils.colorImage("core/assets/chest_recolor.png", Utils.toColor(96, 64, 32)));
        chest.setBounds(-999, -999, .5f, 1f);
    }

    @Override
    public void tick(Robot r) {
        Gamepad gamepad = r.getController();
        OverRobotStats stats = (OverRobotStats) r.stats;

        boolean hasChests = chests.size() > 0;

        if (chests.size() > 1) chest.setSize(.5f, 1f);
        else chest.setSize(.3f, .6f);
        chest.setPosition(r.getX() - chest.getWidth() / 2, r.getY() - chest.getHeight() / 2);
        chest.setOriginCenter();
        chest.setRotation(r.getAngle());

        if (stats.chestIntake && (gamepad.isRightTriggerPressed() || intaking)) {
            for (Entity e : new ArrayList<>(intakeableChests)) {
                if (!intakeableChests.isEmpty() && chests.size() < stats.maxChests) {
                    chestIntakeTimes.putIfAbsent(e, System.currentTimeMillis());
                    double a = Math.toRadians(Utils.getAngle(new Point2D.Float(e.getX(), e.getY()), new Point2D.Float(r.getX(), r.getY())));
                    synchronized (Main.WORLD_USE) {
                        e.getPrimary().applyForceToCenter(stats.chestIntakeStrength * (float) Math.cos(a), stats.chestIntakeStrength * (float) Math.sin(a), true);
                    }
                    if (System.currentTimeMillis() - chestIntakeTimes.get(e) >= stats.chestIntakeTime) {
                        Main.getInstance().removeEntity(e);
                        intakeableChests.remove(e);
                        chestIntakeTimes.remove(e);
                        Chest c = (Chest) e;
                        chests.add(new ChestInfo(c.isHeavy(), c.alliance));
                    }
                }
            }
        }
        boolean val = gamepad.getButton(ejectGearButton).get();
        if (val && !ejectChestWasHeld) {
            ejectChest(r);
        }
        ejectChestWasHeld = val;
    }

    @Override
    public void collideStart(Robot r, Entity e, Body self, Body other, Contact contact) {
        OverRobotStats stats = (OverRobotStats) r.stats;
        if (self == r.intake) {
            if (e instanceof Chest) {
                contact.setEnabled(false);
                if (intakeableChests.size() < stats.maxChestIntakeAtOnce && !intakeableChests.contains(e)) {
                    Chest c = (Chest) e;
                    if ((r.blue && c.alliance != Game.ALLIANCE.RED) || (!r.blue && c.alliance != Game.ALLIANCE.BLUE))
                    intakeableChests.add(e);
                }
            }
        }
    }

    @Override
    public void onCollide(Robot r, Entity e, Body self, Body other, Contact contact) {}

    @Override
    public void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact) {
        if (self == r.intake) {
            if (intakeableChests.contains(e)) {
                intakeableChests.remove(e);
                chestIntakeTimes.remove(e);
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, Robot r) {
        if (chests.size() > 0) chest.draw(batch);
    }

    public void setIntaking(boolean b) {
        intaking = b;
    }

    public void ejectChest(Robot r) {
        if (chests.size() > 0) {
            float distance = 1.25f;
            float xChange = -distance * (float) Math.sin(Math.toRadians(r.getAngle()));
            float yChange = distance * (float) Math.cos(Math.toRadians(r.getAngle()));

            ChestInfo f = chests.get(0);
            Entity e = new Chest(r.getX() + xChange, r.getY() + yChange, r.getAngle(), f.heavy, f.alliance);

            Main.getInstance().spawnEntity(e);
            synchronized (Main.WORLD_USE) {
                for (Body b : e.getBodies()) {
                    float xPow = 10 * (float) Math.sin(Math.toRadians(-r.getAngle()));
                    float yPow = 10 * (float) Math.cos(Math.toRadians(-r.getAngle()));
                    b.applyForceToCenter(xPow, yPow, true);
                }
            }
            chests.remove(f);
        }
    }

    class ChestInfo {
        boolean heavy;
        Game.ALLIANCE alliance;

        public ChestInfo(boolean heavy, Game.ALLIANCE alliance) {
            this.heavy = heavy;
            this.alliance = alliance;
        }
    }
}
