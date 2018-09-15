package ryan.game.entity.powerup;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.controls.Gamepad;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.entity.Robot;
import ryan.game.games.power.PowerDisplay;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;

public class PixelReceiver extends Entity {

    static float WIDTH =.7f;
    static float HEIGHT = .5f;

    public int totalPixels = 0;
    public int pixels = 0;

    public boolean blue;

    public boolean boostDone = false;
    public boolean forceDone = false;

    public PixelReceiver(float x, float y, boolean blue) {
        super(BodyFactory.getRectangleStatic(x, y, WIDTH, HEIGHT, 0.1f));
        this.blue = blue;
    }

    @Override
    public void tick(){
        super.tick();
        if (!GameScreen.matchPlay) {
            boostDone = false;
            forceDone = false;
            pixels = 0;
            totalPixels = 0;
        }
        for (Robot r : GameScreen.robots) {
            if (blue == r.blue) {
                Gamepad g = r.getController();
                if (g != null && pixels > 0) {
                    int level = pixels;
                    if (level > 3) level = 3;
                    if (blue) {
                        PowerDisplay.blue_powLevel = level;
                    } else {
                        PowerDisplay.red_powLevel = level;
                    }
                    int forceClimbs = blue ? PowerDisplay.blue_forceClimbs : PowerDisplay.red_forceClimbs;
                    if (PowerDisplay.powerUp == PowerDisplay.LiteralPowerUp.NONE) {
                        if (g.getButton(Gamepad.TWO) && !boostDone) {
                            PowerDisplay.powerUp = PowerDisplay.LiteralPowerUp.BOOST;
                            PowerDisplay.powerUpLevel = level;
                            PowerDisplay.powerUpStart = System.currentTimeMillis();

                            if (pixels > 3) pixels -= 3;
                            else pixels = 0;

                            PowerDisplay.powerUpForBlue = blue;
                            boostDone = true;
                        } else if (g.getButton(Gamepad.ONE) && !forceDone) {
                            PowerDisplay.powerUp = PowerDisplay.LiteralPowerUp.FORCE;
                            PowerDisplay.powerUpLevel = level;
                            PowerDisplay.powerUpStart = System.currentTimeMillis();

                            if (pixels > 3) pixels -= 3;
                            else pixels = 0;

                            PowerDisplay.powerUpForBlue = blue;
                            forceDone = true;
                        }
                    }
                    if (g.getButton(Gamepad.FOUR) && level == 3 && forceClimbs == 0) {
                        if (pixels > 3) pixels -= 3;
                        else pixels = 0;
                        if (blue) {
                            PowerDisplay.blue_forceClimbs++;
                        } else {
                            PowerDisplay.red_forceClimbs++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
        if (e instanceof Pixel) {
            Main.getInstance().removeEntity(e);
            pixels++;
            totalPixels++;
            if (totalPixels <= 9) {
                if (blue) {
                    PowerDisplay.blue_vault += 5;
                } else {
                    PowerDisplay.red_vault += 5;
                }
            }
        }
    }

    @Override
    public void drawUnscaled(SpriteBatch b) {
        super.drawUnscaled(b);
        Vector2 pos = getPhysicsPosition();

        Fonts.drawCentered(Fonts.fmsWhiteVerySmall, totalPixels + "", pos.x * Main.mtpH + (blue ? 60 : -60), pos.y * Main.mtpH + (53 * 1.7f), b);
    }
}
