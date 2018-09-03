package ryan.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Robot;
import ryan.game.games.steamworks.robots.SteamDefault;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.render.Fonts;
import java.util.ArrayList;
import java.util.List;

public class TitleScreen extends Screen {

    Subpage subpage = Subpage.TITLE;
    float ui_visibility = 0;

    Music music = Gdx.audio.newMusic(Gdx.files.internal("core/assets/music/title_2.wav"));
    Sprite frc_logo = new Sprite(new Texture(Gdx.files.internal("core/assets/frc_logo2.png")));
    Texture background = new Texture(Gdx.files.internal("core/assets/background.png"));

    Button tournamentButton, builderButton, settingsButton;

    int maxPoints = 20;
    String[] custom_robots = {"core/assets/robot_custom.png", "core/assets/robot_custom3.png", "core/assets/robot_custom2.png", "core/assets/robot_custom4.png"};
    int custom_robot_index = 0;
    Sprite custom_robot_current;
    Sprite arrow = new Sprite(new Texture("core/assets/ui/arrow.png"));
    List<StatSlider> sliders = new ArrayList<>();
    public static Texture plus = new Texture("core/assets/ui/plus.png"), minus = new Texture("core/assets/ui/minus.png");
    Button saveRobotButton, backButton;

    Color primary = Color.GRAY, secondary = Color.PURPLE;


    @Override
    public void init() {
        frc_logo.setScale(.65f);
        frc_logo.setPosition((-Main.screenWidth/2) - 90, (Main.screenHeight/2) - 200);

        tournamentButton = new Button(0, 0, 450, 70, "Sandbox Mode", ()->{
            music.stop();
            //TODO: pretty transition
            Main.getInstance().setScreen(new GameScreen());
        });

        builderButton = new Button(0, -100, 400, 70, "Robot Builder", ()->{
            setSubpage(Subpage.ROBOT_BUILDER);
        });

        settingsButton = new Button(0, -200, 300, 70, "Settings", ()->{
            Utils.log("TODO: implement settings");
        });

        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


        music.setLooping(true);
        music.setVolume(0);
        music.play();

        //Subpage Robot Builder setup
        arrow.setScale(2);
        arrow.setPosition(115, 150);

        String[] text = {"Speed", "Gear Intake", "Shooter Speed", "Shooter Accuracy", "Climber"};
        float[] max = {11f, 5f, 7f, 10f, 10f};
        for (int i=0; i<5; i++) {
            int x = 0;
            int y = -50 - (60 * i);
            sliders.add(new StatSlider(x, y, (int)max[i], text[i]));
        }

        saveRobotButton = new Button(0, -350, 250, 70, "Save", ()-> {
            saveRobot();
            setSubpage(Subpage.TITLE);
        });

        backButton = new Button(0, -440, 250, 70, "Back", ()-> {
            setSubpage(Subpage.TITLE);
        });
    }

    public void setSubpage(Subpage p) {
        subpage = p;
        if (subpage == Subpage.TITLE) {
            //?
        } else if (subpage == Subpage.ROBOT_BUILDER) {
            refreshCustomRobot();
        }
    }

    @Override
    public void tick() {
        music.setVolume(ui_visibility * 0.25f);
        if (subpage == Subpage.ROBOT_BUILDER) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && maxPoints == 0) {
                saveRobot();
            }
        }
    }

    public void saveRobot() {
        //TODO: for now, assuming Steamworks. Need to make this work by default for all games via a vague calculation system
        SteamRobotStats stats = new SteamDefault();
        //"Speed", "Gear Intake", "Shooter Speed", "Shooter Accuracy", "Climber"};
        stats.maxMPS = (8 + (14 * sliders.get(0).getProgress())) / 3.28084f;

        float val = sliders.get(1).getProgress();
        stats.gearIntake = val != 0;
        stats.gearIntakeRate = 1000 - (700 * val);
        stats.gearIntakeStrength = 9f + (3 * val);

        val = sliders.get(2).getProgress();
        stats.shooter = val != 0;
        stats.timePerShoot = 1000 - (800 * val);

        val = sliders.get(3).getProgress();
        stats.shootPowerVariance = 2.5f - (1.5f * val);
        stats.shootAngleVariance = 5 - (4 * val);

        val = sliders.get(4).getProgress();
        stats.climber = val != 0;
        stats.climbSpeed = 8 - (6.5f * val);

        stats.texture = custom_robots[custom_robot_index];
        stats.recolorIndex = -1;

        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Utils.log(g.toJson(stats));

        Robot.statsOptions[1] = stats;
    }

    public void nextCustomRobotSprite() {
        custom_robot_index++;
        if (custom_robot_index == custom_robots.length) custom_robot_index = 0;
        refreshCustomRobot();
    }

    public void refreshCustomRobot() {
        custom_robot_current = new Sprite(Utils.colorImage(custom_robots[custom_robot_index], primary, Color.BLUE, secondary));
        custom_robot_current.setSize(150, 150);
        custom_robot_current.setPosition(0-75, 100);
    }

    float scrollPoint = -Main.screenWidth/2;
    float backX1 = scrollPoint;
    float backX2 = 9999;

    int blockLength = 387*5; //5 complete loops of the image are just over the width of the screen

    @Override
    public void draw(SpriteBatch b) {
        if (subpage == Subpage.TITLE) {
        } else if (subpage == Subpage.ROBOT_BUILDER) {
        }
    }

    @Override
    public void drawUnscaled(SpriteBatch b) {
        float delta = Gdx.graphics.getDeltaTime();

        float inc = 10f * delta;
        backX1 += inc;
        backX2 += inc;

        if (backX1 >= scrollPoint && backX2 > backX1) {
            backX2 = backX1 - blockLength;
        } else if (backX2 >= scrollPoint && backX1 > backX2) {
            backX1 = backX2 - blockLength;
        }

        b.draw(background, backX1, -550, 387, 832, blockLength, (int)Main.screenHeight*2);
        b.draw(background, backX2, -550, 387, 832, blockLength, (int)Main.screenHeight*2);

        ui_visibility += 0.75f * delta;
        if (ui_visibility > 1) ui_visibility = 1;

        frc_logo.setAlpha(ui_visibility);
        frc_logo.draw(b);

        if (subpage == Subpage.TITLE) {
            tournamentButton.setAlpha(ui_visibility);
            tournamentButton.draw(b);

            builderButton.setAlpha(ui_visibility);
            builderButton.draw(b);

            settingsButton.setAlpha(ui_visibility);
            settingsButton.draw(b);

            Fonts.draw(Fonts.monoWhiteLarge, "The FRC Simulator, v0.1a", Main.screenWidth/2 -640, -Main.screenHeight/2 +40, b);
        } else if (subpage == Subpage.ROBOT_BUILDER) {
            custom_robot_current.draw(b);
            arrow.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, maxPoints + " Points Remaining", 0, 70, b);
            for (StatSlider s : sliders) {
                s.draw(b);
            }
            saveRobotButton.draw(b);
            backButton.draw(b);
        }
    }

    @Override
    public boolean click(Vector3 pos, int button) {
        if (subpage == Subpage.TITLE) {
            tournamentButton.click(pos, button);
            builderButton.click(pos, button);
            settingsButton.click(pos, button);
        } else if (subpage == Subpage.ROBOT_BUILDER) {
            saveRobotButton.click(pos, button);
            backButton.click(pos, button);
            if (arrow.getBoundingRectangle().contains(pos.x, pos.y)) {
                nextCustomRobotSprite();
                return true;
            }
            for (StatSlider s : sliders) {
                if (s.plus.getBoundingRectangle().contains(pos.x, pos.y)) {
                    if (maxPoints > 0) {
                        maxPoints--;
                        s.current++;
                        if (s.current > s.max) s.current = s.max;
                        return true;
                    }
                } else if (s.minus.getBoundingRectangle().contains(pos.x, pos.y)) {
                    s.current--;
                    if (s.current < 0) s.current = 0;
                    else maxPoints++;
                    return true;
                }
            }
        }
        return false;
    }

    public enum Subpage {
        TITLE,
        ROBOT_BUILDER,
        SETTINGS
    }

    class StatSlider {

        int x, y;
        int max, current = 0;
        String label;
        Sprite plus, minus;

        public StatSlider(int x, int y, int max, String label) {
            this.x = x;
            this.y = y;
            this.max = max;
            this.label = label;

            plus = new Sprite(TitleScreen.plus);
            plus.setBounds(x-200, y, 40, 40);

            minus = new Sprite(TitleScreen.minus);
            minus.setBounds(x-150, y, 40, 40);
        }

        public float getProgress() {
            return (current*1f)/(max*1f);
        }

        public void draw(SpriteBatch b) {
            Utils.drawUnscaledProgressBar(x, y, 200, 40, getProgress(), b);
            Fonts.draw(Fonts.fmsWhiteSmall, label + " (" + current + "/" + max + ")", x+110, y + 28.5f, b);
            plus.draw(b);
            minus.draw(b);
        }
    }
}