package ryan.game.games.destination;

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
import ryan.game.entity.destination.*;
import ryan.game.games.Game;
import ryan.game.games.RobotMetadata;
import ryan.game.screens.GameScreen;
import java.awt.geom.Point2D;

public class DestinationMetadata extends RobotMetadata {

    Sprite panel, cargo;

    public boolean crossedBaseline = false;

    public Entity peg = null;
    public Entity pegOnBack = null;

    public boolean hasPanel = false;
    public Entity intakeablePanel = null;
    public Long panelIntakeStart = null;
    boolean startedIntakingWithPanel = false;
    boolean panelToggleWasTrue = false;

    public boolean hasCargo = false;
    public Entity intakeableCargo = null;
    public Long cargoIntakeStart = null;
    boolean startedIntakingWithCargo = false;
    boolean cargoToggleWasTrue = false;

    public Hab climbingHab = null;
    public Long habClimbStart = null;

    public HumanPlayer hpStation = null;

    private final int cargoToggle = Gamepad.BUMPER_LEFT;
    private final int panelToggle = Gamepad.BUMPER_RIGHT;

    public DestinationMetadata() {
        panel = new Sprite(Panel.TEXTURE);
        panel.setBounds(-999, -999, 1.25f, 1.25f);

        cargo = new Sprite(Cargo.TEXTURE);
        cargo.setBounds(-999, -999, 1, 1);
    }

    @Override
    public void tick(Robot r) {
        Gamepad gamepad = r.getManipulator();
        DestinationRobotStats stats = (DestinationRobotStats) r.stats;

        boolean cargoIntake = stats.cargoIntake;

        if (gamepad != null) {
            boolean val = gamepad.getButton(panelToggle);
            boolean cargoVal = gamepad.getButton(cargoToggle);

            if (val && !panelToggleWasTrue) {
                startedIntakingWithPanel = hasPanel;
            }

            if (cargoVal && !cargoToggleWasTrue) {
                startedIntakingWithCargo = hasCargo;
            }

            if (val) {
                if (hasPanel && startedIntakingWithPanel) {
                    ejectPanel(r);
                }

                if (!hasPanel && !hasCargo && intakeablePanel != null && !startedIntakingWithPanel && stats.panelFloor) {
                    if (panelIntakeStart == null) panelIntakeStart = GameScreen.getTime();
                    double a = Math.toRadians(Utils.getAngle(new Point2D.Float(intakeablePanel.getX(), intakeablePanel.getY()), new Point2D.Float(r.getX(), r.getY())));
                    synchronized (Main.WORLD_USE) {
                        intakeablePanel.getPrimary().applyForceToCenter(stats.panelIntakeStrength * (float) Math.cos(a), stats.panelIntakeStrength * (float) Math.sin(a), true);
                    }
                    if (GameScreen.getTime() - panelIntakeStart >= stats.panelIntakeRate) {
                        Main.getInstance().removeEntity(intakeablePanel);
                        intakeablePanel = null;
                        hasPanel = true;
                        panelIntakeStart = null;
                    }
                } else if (hpStation != null && !hasPanel && !startedIntakingWithPanel){
                    float diff = Math.abs(r.getAngle() - hpStation.getAngle());
                    if (Math.abs(diff - 270) <= 15 || Math.abs(diff - 90) <= 15) {
                        hasPanel = true;
                    }
                }

            }
            if (cargoVal) {
                if (hasCargo && startedIntakingWithCargo) {
                    ejectCargo(r);
                }

                if (!hasCargo && !hasPanel && intakeableCargo != null && !startedIntakingWithCargo && cargoIntake) {
                    if (cargoIntakeStart == null) cargoIntakeStart = GameScreen.getTime();
                    double a = Math.toRadians(Utils.getAngle(new Point2D.Float(intakeableCargo.getX(), intakeableCargo.getY()), new Point2D.Float(r.getX(), r.getY())));
                    synchronized (Main.WORLD_USE) {
                        intakeableCargo.getPrimary().applyForceToCenter(stats.cargoIntakeStrength * (float) Math.cos(a), stats.cargoIntakeStrength * (float) Math.sin(a), true);
                    }
                    if (GameScreen.getTime() - cargoIntakeStart >= stats.cargoIntakeRate) {
                        Main.getInstance().removeEntity(intakeableCargo);
                        intakeableCargo = null;
                        hasCargo = true;
                        cargoIntakeStart = null;
                    }
                }
            }
            panelToggleWasTrue = val;
            cargoToggleWasTrue = cargoVal;
        }
    }

    @Override
    public void collideStart(Robot r, Entity e, Body self, Body other, Contact contact) {
        DestinationRobotStats stats = (DestinationRobotStats) r.stats;
        boolean canPanel = !stats.differentiateBetweenIntakes || r.isPart("panel", self);
        boolean canCargo = !stats.differentiateBetweenIntakes || r.isPart("cargo", self);
        if (r.isPart("intake", self)) {
            if (e.getName().equalsIgnoreCase("peg")) {
                if (canPanel) peg = e;
                contact.setEnabled(false);
            } else if (e instanceof Panel) {
                if (canPanel && !((Panel)e).failed && stats.panelFloor) intakeablePanel = e;
                contact.setEnabled(false);
            } else if (e instanceof Cargo) {
                if (canCargo) intakeableCargo = e;
                contact.setEnabled(false);
            } else if (e instanceof HumanPlayer && ((HumanPlayer)e).blue == r.blue && canPanel && stats.panelIntake) {
                hpStation = (HumanPlayer) e;
            }
        }
        if (r.isPart("cargo_eject", self)) {
            if (e.getName().equalsIgnoreCase("peg")) {
                pegOnBack = e;
            }
        }

        if (e instanceof Hab) {
            Hab h = (Hab) e;
            if (h.level <= stats.hab_level) {
                contact.setEnabled(false);
                if (h.blue == r.blue && (climbingHab == null || h.level < climbingHab.level)) {
                    climbingHab = h;
                    habClimbStart = GameScreen.getTime();
                }
            }
        }
    }

    @Override
    public void onCollide(Robot r, Entity e, Body self, Body other, Contact contact) {
    }

    @Override
    public void collideEnd(Robot r, Entity e, Body self, Body other, Contact contact) {
        DestinationRobotStats stats = (DestinationRobotStats) r.stats;
        boolean canPanel = !stats.differentiateBetweenIntakes || r.isPart("panel", self);
        boolean canCargo = !stats.differentiateBetweenIntakes || r.isPart("cargo", self);
        if (r.isPart("intake", self)) {
            if (e == peg && canPanel) {
                peg = null;
            } else if (e == intakeablePanel && canPanel) {
                intakeablePanel = null;
                panelIntakeStart = null;
            } else if (e == intakeableCargo && canCargo) {
                intakeableCargo = null;
                cargoIntakeStart = null;
            } else if (e == hpStation && canPanel && stats.panelIntake) {
                hpStation = null;
            }
        }
        if (r.isPart("cargo_eject", self)) {
            if (e == pegOnBack) {
                pegOnBack = null;
            }
        }

        if (e == climbingHab) {
            climbingHab = null;
            habClimbStart = null;
        }
    }

    @Override
    public void draw(SpriteBatch batch, Robot r) {
        DestinationRobotStats stats = (DestinationRobotStats) r.stats;
        Vector2 pos = r.getPhysicsPosition();

        if (hasPanel) {
            panel.setPosition(pos.x - panel.getWidth() / 2, pos.y - panel.getHeight() / 2);
            panel.setOriginCenter();
            panel.setRotation(r.getAngle());
            panel.draw(batch);
        }

        if (hasCargo) {
            cargo.setPosition(pos.x - cargo.getWidth() / 2, pos.y - cargo.getHeight() / 2);
            cargo.setOriginCenter();
            cargo.setRotation(r.getAngle());
            cargo.draw(batch);
        }

        if (habClimbStart != null && (Game.getMatchTime() <= 30 || !Game.isPlaying())) {
            float speed = 99999;
            if (climbingHab.level == 1) speed = stats.habLevel1Speed;
            else if (climbingHab.level == 2) speed = stats.habLevel2Speed;
            else if (climbingHab.level == 3) speed = stats.habLevel3Speed;
            Utils.drawProgressBar(r.getX(), r.getY() + 1f, 2f, .5f, ((GameScreen.getTime() - habClimbStart)/((speed*1000))), batch);
        }
    }

    public void ejectPanel(Robot r) {
        boolean canScore = false;
        SpotToScore s = null;
        DestinationRobotStats stats  = (DestinationRobotStats) r.stats;
        if (peg != null) {
            float diff = Math.abs(r.getAngle() - peg.getAngle());
            if (Math.abs(diff - 270) <= 25 || Math.abs(diff - 90) <= 25) canScore = true;
            s = (SpotToScore) peg;
            if (s.maxPanel == 0 || s.maxPanel == s.numPanel) canScore = false;
            if (!stats.elevator && s.numPanel >= 1) {
                canScore = false;
            }
        }
        if (hasPanel) {
            if (!canScore) {
                // Drop the panel, placing it into the world
                float distance = 1.25f; //1.75f
                float xChange = -distance * (float) Math.sin(Math.toRadians(r.getAngle()));
                float yChange = distance * (float) Math.cos(Math.toRadians(r.getAngle()));

                Panel e = new Panel(r.getX() + xChange, r.getY() + yChange, r.getAngle());
                //e.failed = true;

                Main.spawnEntity(e);
                synchronized (Main.WORLD_USE) {
                    for (Body b : e.getBodies()) {
                        float xPow = 50 * (float) Math.sin(Math.toRadians(-r.getAngle()));
                        float yPow = 50 * (float) Math.cos(Math.toRadians(-r.getAngle()));
                        b.applyForceToCenter(xPow, yPow, true);
                    }
                }
                hasPanel = false;
            } else {
                hasPanel = false;
                //if (GameScreen.matchPlay) {
                s.numPanel++;
                //}
            }
        }
    }

    public void ejectCargo(Robot r) {
        boolean canScore = false;
        SpotToScore s = null;
        Entity basisForDrop = r;
        DestinationRobotStats stats  = (DestinationRobotStats) r.stats;
        if (pegOnBack != null) {
            float diff = Math.abs(r.getAngle() - pegOnBack.getAngle());
            if (Math.abs(diff - 270) <= 25 || Math.abs(diff - 90) <= 25) canScore = true;
            s = (SpotToScore) pegOnBack;
            if (s.numCargo >= s.maxCargo || (s.maxPanel > 0 && s.numPanel == 0)) canScore = false;
            if (!stats.elevator && s.numCargo >= 2) canScore = false;
            if (s.panelRequirements != null) {
                int currentMax = 0;
                SpotToScore lastEmpty = null;
                for (SpotToScore spot : s.panelRequirements) {
                    if (spot.maxPanel > 0 && spot.numPanel > 0) {
                        currentMax += spot.numPanel;
                    } else {
                        lastEmpty = spot;
                    }
                }
                if (s.numCargo >= currentMax) {
                    canScore = false;
                    /*if (lastEmpty != null) {
                        basisForDrop = lastEmpty;
                        Utils.log("last empty");
                    }*/
                }
            }
        }
        if (hasCargo) {
            if (!canScore) {

                // Drop the cargo, placing it into the world
                float distance = 1.6f; //-1.5f;
                float xChange = -distance * (float) Math.sin(Math.toRadians(basisForDrop.getAngle()));
                float yChange = distance * (float) Math.cos(Math.toRadians(basisForDrop.getAngle()));

                Cargo e = new Cargo(basisForDrop.getX() + xChange, basisForDrop.getY() + yChange);

                Main.spawnEntity(e);
                synchronized (Main.WORLD_USE) {
                    for (Body b : e.getBodies()) {
                        float xPow = -10 * (float) Math.sin(Math.toRadians(-basisForDrop.getAngle()));
                        float yPow = -10 * (float) Math.cos(Math.toRadians(-basisForDrop.getAngle()));
                        b.applyForceToCenter(xPow, yPow, true);
                    }
                }
                hasCargo = false;
            } else {
                hasCargo = false;
                s.numCargo++;
            }
        }
    }
}
