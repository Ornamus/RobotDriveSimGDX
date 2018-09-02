package ryan.game.games.power;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.ScoreDisplay;
import ryan.game.games.power.robots.PowerRobotBase;
import ryan.game.render.Fonts;
import ryan.game.screens.GameScreen;

public class PowerDisplay extends ScoreDisplay {

    public static int blueTimeAcc = 0, redTimeAcc = 0;
    public static int blue_vault = 0, red_vault = 0;
    public static LiteralPowerUp powerUp = LiteralPowerUp.NONE;
    public static boolean powerUpForBlue = false;
    public static int blue_powLevel = 0;
    public static int red_powLevel = 0;
    public static int powerUpLevel = 0;
    public static long powerUpStart = 0;
    public static int blue_forceClimbs = 0;
    public static int red_forceClimbs = 0;

    public static int blue_foul = 0;
    public static int red_foul = 0;

    public enum LiteralPowerUp {
        NONE,
        BOOST,
        FORCE
    }

    public PowerDisplay() {
        super("core/assets/score_display_overboard.png");
    }

    @Override
    public int[] calculateScores() {
        return new int[]{calculateScore(Game.ALLIANCE.BLUE), calculateScore(Game.ALLIANCE.RED)};
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        if (PowerUp.blue_receiver != null && PowerUp.blue_receiver.pixels > 0) {
            boolean boost = !PowerUp.blue_receiver.boostDone;
            boolean force = !PowerUp.blue_receiver.forceDone;
            boolean levitate = (blue_powLevel == 3 && blue_forceClimbs == 0);

            String msg = "Level " + blue_powLevel + "\n";
            if (boost) {
                msg += "1. Boost\n";
            }
            if (force) {
                msg += "2. Force\n";
            }
            if (levitate) {
                msg += "3. Levitate";
            }

            Fonts.draw(Fonts.monoWhiteSmall, msg, 0, 0, 450, -100, batch);
        }
        if (PowerUp.red_receiver != null && PowerUp.red_receiver.pixels > 0) {
            boolean boost = !PowerUp.red_receiver.boostDone;
            boolean force = !PowerUp.red_receiver.forceDone;
            boolean levitate = (red_powLevel == 3 && red_forceClimbs == 0);

            String msg = "Level " + red_powLevel + "\n";
            if (boost) {
                msg += "1. Boost\n";
            }
            if (force) {
                msg += "2. Force\n";
            }
            if (levitate) {
                msg += "3. Levitate";
            }

            Fonts.draw(Fonts.monoWhiteSmall, msg, 0, 0, -540, -100, batch);
        }
        if (powerUp != LiteralPowerUp.NONE) {
            if (powerUpForBlue) {
                Fonts.monoWhiteLarge.setColor(Main.BLUE);
            } else  {
                Fonts.monoWhiteLarge.setColor(Main.RED);
            }
            String display = "BOOST!";
            if (powerUp == LiteralPowerUp.FORCE) {
                display = "FORCE!";
            }
            Fonts.draw(Fonts.monoWhiteLarge, display + " LV " + powerUpLevel, 0, 0, -90, 300, batch);

            Fonts.monoWhiteLarge.setColor(255,255,255,1);
        }
    }

    long blue_lastSecond = 0, red_lastSecond = 0;

    public int calculateScore(Game.ALLIANCE alliance) {
        if (System.currentTimeMillis()-powerUpStart>10000) {
            powerUp = LiteralPowerUp.NONE;
        }

        int score = 0;
        boolean blue = alliance == Game.ALLIANCE.BLUE;

        int climbs = 0;
        int baselines = 0;

        for (Robot r : GameScreen.robots) {
            if (r.blue == blue) {
                PowerMetadata meta = (PowerMetadata) r.metadata;
                if (meta.crossedBaseline) baselines++;
                if (Game.getMatchTime() <= 30) {
                    PowerRobotBase stats = (PowerRobotBase) r.stats;
                    if (meta.climb != null && Main.getTime() - meta.climb >= (stats.climbTime * 1000)) {
                        climbs++;
                    }
                }
            }
        }

        if (blue) {
            climbs += blue_forceClimbs;
        } else {
            climbs += red_forceClimbs;
        }
        if (climbs > 3) climbs = 3;


        long timeSince = blue ? blue_lastSecond : red_lastSecond;
        int acc = 0;
        if (System.currentTimeMillis() - timeSince >= 1000) {
            if (PowerUp.blue_bottom.alliance == alliance) {
                if (PowerUp.blue_bottom.pixels.size() > PowerUp.blue_top.pixels.size()) {
                    if (blue) {
                        acc++;
                        if (Game.isAutonomous()) acc++;
                    }
                    if (powerUp == LiteralPowerUp.BOOST && powerUpForBlue && blue && powerUpLevel != 2) {
                        acc++;
                    }
                } else if (powerUp == LiteralPowerUp.FORCE  && powerUpForBlue && blue && powerUpLevel != 2) {
                    acc++;
                }
            }
            if (PowerUp.blue_top.alliance == alliance) {
                if (PowerUp.blue_top.pixels.size() > PowerUp.blue_bottom.pixels.size()) {
                    if (blue) {
                        acc++;
                        if (Game.isAutonomous()) acc++;
                    }
                    if (powerUp == LiteralPowerUp.BOOST && powerUpForBlue && blue && powerUpLevel != 2) {
                        acc++;
                    }
                } else if (powerUp == LiteralPowerUp.FORCE  && powerUpForBlue && blue && powerUpLevel != 2) {
                    acc++;
                }
            }

            if (PowerUp.red_bottom.alliance == alliance) {
                if (PowerUp.red_bottom.pixels.size() > PowerUp.red_top.pixels.size()) {
                    if (!blue) {
                        acc++;
                        if (Game.isAutonomous()) acc++;
                    }
                    if (powerUp == LiteralPowerUp.BOOST && !powerUpForBlue && !blue && powerUpLevel != 2) {
                        acc++;
                    }
                } else if (powerUp == LiteralPowerUp.FORCE  && !powerUpForBlue && !blue && powerUpLevel != 2) {
                    acc++;
                }
            }
            if (PowerUp.red_top.alliance == alliance) {
                if (PowerUp.red_top.pixels.size() > PowerUp.red_bottom.pixels.size()) {
                    if (!blue) {
                        acc++;
                        if (Game.isAutonomous()) acc++;
                    }
                    if (powerUp == LiteralPowerUp.BOOST && !powerUpForBlue && !blue && powerUpLevel != 2) {
                        acc++;
                    }
                } else if (powerUp == LiteralPowerUp.FORCE  && !powerUpForBlue && !blue && powerUpLevel != 2) {
                    acc++;
                }
            }

            if (PowerUp.tall_bottom.alliance == alliance) {
                if (PowerUp.tall_bottom.pixels.size() > PowerUp.tall_top.pixels.size()) {
                    acc++;
                    if (Game.isAutonomous()) acc++;
                    if (powerUp == LiteralPowerUp.BOOST && powerUpForBlue == blue && powerUpLevel != 1) {
                        acc++;
                    }
                } else if (powerUp == LiteralPowerUp.FORCE && powerUpForBlue == blue && powerUpLevel != 1) {
                    acc++;
                }
            }
            if (PowerUp.tall_top.alliance == alliance) {
                if (PowerUp.tall_top.pixels.size() > PowerUp.tall_bottom.pixels.size()) {
                    acc++;
                    if (Game.isAutonomous()) acc++;
                    if (powerUp == LiteralPowerUp.BOOST && powerUpForBlue == blue && powerUpLevel != 1) {
                        acc++;
                    }
                } else if (powerUp == LiteralPowerUp.FORCE && powerUpForBlue == blue && powerUpLevel != 1) {
                    acc++;
                }
            }
            if (blue) {
                blue_lastSecond = System.currentTimeMillis();
                blueTimeAcc+=acc;
            } else {
                red_lastSecond = System.currentTimeMillis();
                redTimeAcc +=acc;
            }
        }

        int points = (baselines*5) + (climbs*30);

        if (blue) {
            points += blueTimeAcc + blue_vault + blue_foul;
        } else {
            points += redTimeAcc + red_vault + red_foul;
        }

        return points;
    }
}
