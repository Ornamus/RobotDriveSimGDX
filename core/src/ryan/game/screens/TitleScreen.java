package ryan.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.RobotStats;
import ryan.game.competition.Team;
import ryan.game.entity.Robot;
import ryan.game.games.RobotStatBuilder;
import ryan.game.games.RobotStatSlider;
import ryan.game.games.destination.DestinationRobotStats;
import ryan.game.games.destination.DestinationStatBuilder;
import ryan.game.games.steamworks.SteamStatBuilder;
import ryan.game.render.Button;
import ryan.game.render.Fonts;
import sun.security.krb5.internal.crypto.Des;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TitleScreen extends Screen {

    Subpage subpage = Subpage.TITLE;
    float ui_visibility = 0;

    public static int customBotsLoaded = -1;
    public static int tournamentTeamsLoaded = -1;

    Music music = Gdx.audio.newMusic(Gdx.files.internal("core/assets/music/title_2.wav"));
    Sprite frc_logo = new Sprite(new Texture(Gdx.files.internal("core/assets/frc_logo2.png")));
    Texture background = new Texture(Gdx.files.internal("core/assets/background.png"));

    Button tournamentButton, sandboxButton, builderButton, settingsButton;

    boolean buildingTournamentBot = true; //TODO: only allow this to become true if the current eventKey doesn't already have a matches folder
    int maxPoints;
    String[] custom_robots = {"core/assets/robot_custom.png", "core/assets/robot_custom3.png", "core/assets/robot_custom2.png",
            "core/assets/robot_custom4.png", "core/assets/robot_custom5.png", "core/assets/robot_custom6.png", "core/assets/robot_custom7.png"};
    int custom_robot_index = 0;
    Sprite custom_robot_current;
    Sprite arrow = new Sprite(new Texture("core/assets/ui/arrow.png"));
    RobotStatBuilder statBuilder;
    Button saveRobotButton, backButton, randomColorButton;
    String builderBonusText = "";

    Color primary = Color.GRAY, secondary = Color.PURPLE;

    @Override
    public void init() {
        frc_logo.setScale(.65f);
        frc_logo.setPosition((-Main.screenWidth / 2) - 90, (Main.screenHeight / 2) - 200);

        tournamentButton = new Button(0, 50, 520, 70, "Tournament Mode", () -> {
            music.stop();
            //TODO: pretty transition
            GameScreen.MAKE_SCHEDULE = true;
            Main.getInstance().setScreen(new GameScreen());
        });

        sandboxButton = new Button(0, -50, 450, 70, "Sandbox Mode", () -> {
            music.stop();
            //TODO: pretty transition
            GameScreen.MAKE_SCHEDULE = false;
            Main.getInstance().setScreen(new GameScreen());
        });

        builderButton = new Button(0, -150, 400, 70, "Robot Builder", () -> {
            setSubpage(Subpage.ROBOT_BUILDER);
        });

        settingsButton = new Button(0, -250, 300, 70, "Settings", () -> {
            Utils.log("TODO: implement settings");
        });

        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        if (GameScreen.PLAY_MUSIC) {
            music.setLooping(true);
            music.setVolume(0);
            music.play();
        }

        //Loading up custom robot files
        if (customBotsLoaded == -1) {
            customBotsLoaded = 0;
            FileHandle[] custom_robots = Gdx.files.internal("custom_robots").list();
            for (FileHandle h : custom_robots) {
                if (h.exists()) {
                    File f = h.file();
                    RobotStats s;
                    s = Utils.fromJSON(f, DestinationRobotStats.class); //TODO: make this not game specific
                    Robot.addStatOption(s);
                    customBotsLoaded++;
                }
            }
        }

        // This isn't used for actual tournament stuff (Schedule does that), this is just so the end user can see what's currently loaded
        if (tournamentTeamsLoaded == -1) {
            tournamentTeamsLoaded = 0;
            File f = new File(GameScreen.EVENT_KEY + "/teams.json");
            if (f.exists()) {
                List<Team> teams = Utils.teamListFromJSON(f);
                tournamentTeamsLoaded = teams.size();
            }
        }

        // Subpage Robot Builder setup
        arrow.setScale(2);
        arrow.setPosition(115, 250);

        saveRobotButton = new Button(0, -350, 250, 70, "Save", () -> {
            saveRobot();
            setSubpage(Subpage.TITLE);
        });

        backButton = new Button(0, -440, 250, 70, "Back", () -> {
            setSubpage(Subpage.TITLE);
        });

        randomColorButton = new Button(0, 170, 450, 70, "Random Colors", () -> {
            randomColors();
        });
    }

    public void randomColors() {
        primary = Utils.toColor(Utils.randomInt(0, 255), Utils.randomInt(0, 255), Utils.randomInt(0, 255));
        secondary = Utils.toColor(Utils.randomInt(0, 255), Utils.randomInt(0, 255), Utils.randomInt(0, 255));
        refreshCustomRobot();
    }

    public void setSubpage(Subpage p) {
        subpage = p;
        if (subpage == Subpage.TITLE) {
            //?
        } else if (subpage == Subpage.ROBOT_BUILDER) {
            custom_robot_index = Utils.randomInt(0, custom_robots.length-1);
            randomColors();

            statBuilder = new DestinationStatBuilder();
            List<RobotStatSlider> steamSliders = statBuilder.getSliders();
            maxPoints = statBuilder.getMaxPoints();

            int x = -400;
            int y = -50;
            int i = 0;
            for (RobotStatSlider s : steamSliders) {
                int internalIndex = i;
                int internalX = x;
                if (i >= 5) {
                    internalX += 650;
                    internalIndex -= 5;
                }
                s.setPosition(internalX, y - (60 * internalIndex));
                i++;
            }

            refreshCustomRobot();
            updateBonusText();
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
        //TODO: for now, assuming Deep Space. Need to make this work by default for all games
        DestinationRobotStats stats = new DestinationRobotStats();
        stats.texture = custom_robots[custom_robot_index];
        stats.recolorIndex = -1;
        stats.custom_primary = primary;
        stats.custom_secondary = secondary;
        statBuilder.applyStats(stats);

        Gson g = new GsonBuilder().setPrettyPrinting().create();

        if (buildingTournamentBot) {
            // TODO: make not ugly
            String num = JOptionPane.showInputDialog("Please enter team number:");
            int number = Integer.parseInt(num);
            List<Team> tournamentTeams = Utils.teamListFromJSON(new File(GameScreen.EVENT_KEY + "/teams.json"));
            if (tournamentTeams == null) tournamentTeams = new ArrayList<>();
            tournamentTeams.add(new Team(number, primary, secondary, stats));
            Utils.saveTeamList(tournamentTeams);

            tournamentTeamsLoaded++;
        } else {
            //FileHandle[] existingRobots = Gdx.files.internal("custom_robots").list();
            //Utils.writeFile("custom_robots/robot_" + (existingRobots.length+1) + ".json", g.toJson(stats));
            //Robot.addStatOption(stats);
            //customBotsLoaded++;
        }
        // TODO: one day, don't save tournament robots into the sandbox too. For now we save them to the sandbox for ease of practice
        FileHandle[] existingRobots = Gdx.files.internal("custom_robots").list();
        Utils.writeFile("custom_robots/robot_" + (existingRobots.length+1) + ".json", g.toJson(stats));
        Robot.addStatOption(stats);
        customBotsLoaded++;
    }

    public void nextCustomRobotSprite() {
        custom_robot_index++;
        if (custom_robot_index == custom_robots.length) custom_robot_index = 0;
        refreshCustomRobot();
    }

    public void refreshCustomRobot() {
        custom_robot_current = new Sprite(Utils.colorImage(custom_robots[custom_robot_index], primary, Color.BLUE, secondary));
        custom_robot_current.setSize(150, 150);
        custom_robot_current.setPosition(0-75, 200);
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

    public void updateBonusText() {
        String string = "";
        int added = 0;
        for (RobotStatSlider s : statBuilder.getSliders()) {
            String piece = s.getText();
            if (piece !=  null) {
                string += "- " + piece + "\n";
                added++;
            }
        }
        if (added == 0) string = "";
        else if (added == 1) string = "Note:\n" + string;
        else string = "Notes:\n" + string;
        builderBonusText = string;
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

        b.draw(background, backX1 + (backX2 > backX1 ? 1 : 0), -550, 387, 832, blockLength, (int)Main.screenHeight*2);
        b.draw(background, backX2 + (backX1 > backX2 ? 1 : 0), -550, 387, 832, blockLength, (int)Main.screenHeight*2);

        ui_visibility += 0.75f * delta;
        if (ui_visibility > 1) ui_visibility = 1;

        frc_logo.setAlpha(ui_visibility);
        frc_logo.draw(b);

        if (subpage == Subpage.TITLE) {
            tournamentButton.setAlpha(ui_visibility);
            tournamentButton.draw(b);

            sandboxButton.setAlpha(ui_visibility);
            sandboxButton.draw(b);

            builderButton.setAlpha(ui_visibility);
            builderButton.draw(b);

            settingsButton.setAlpha(ui_visibility);
            settingsButton.draw(b);

            Fonts.drawRight(Fonts.monoWhiteLarge, "The FRC Simulator, v0.1a", Main.screenWidth/2 - 5, -Main.screenHeight/2 +40, b);
            Fonts.drawRight(Fonts.monoWhiteLarge, tournamentTeamsLoaded + " tournament team" + (tournamentTeamsLoaded != 1 ? "s" : ""), Main.screenWidth/2 - 5, -Main.screenHeight/2 +90, b);
        } else if (subpage == Subpage.ROBOT_BUILDER) {
            custom_robot_current.draw(b);
            arrow.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, maxPoints + " Point" + (maxPoints != 1 ? "s" : "") + " Remaining", 0, 70, b);
            for (RobotStatSlider s : statBuilder.getSliders()) {
                s.draw(b);
            }
            Fonts.drawCentered(Fonts.monoWhiteLarge, builderBonusText, 600, 500, b);
            saveRobotButton.draw(b);
            backButton.draw(b);
            randomColorButton.draw(b);
        }
    }

    @Override
    public boolean click(Vector3 pos, int button) {
        if (subpage == Subpage.TITLE) {
            tournamentButton.click(pos, button);
            sandboxButton.click(pos, button);
            builderButton.click(pos, button);
            settingsButton.click(pos, button);
        } else if (subpage == Subpage.ROBOT_BUILDER) {
            saveRobotButton.click(pos, button);
            backButton.click(pos, button);
            randomColorButton.click(pos, button);
            if (arrow.getBoundingRectangle().contains(pos.x, pos.y)) {
                nextCustomRobotSprite();
                return true;
            }
            for (RobotStatSlider s : statBuilder.getSliders()) {
                if (s.plus.getBoundingRectangle().contains(pos.x, pos.y)) {
                    if (s.allOrNothing) {
                        if (maxPoints >= s.max && s.current < s.max) {
                            maxPoints -= s.max;
                            s.current = s.max;
                            updateBonusText();
                            return true;
                        }
                    } else {
                        if (maxPoints > 0 && s.current < s.max) {
                            maxPoints--;
                            s.current++;
                            updateBonusText();
                            return true;
                        }
                    }
                } else if (s.minus.getBoundingRectangle().contains(pos.x, pos.y)) {
                    if (s.allOrNothing) {
                        if (s.current > 0) {
                            s.current = 0;
                            maxPoints += s.max;
                        }
                        updateBonusText();
                        return true;
                    } else {
                        s.current--;
                        if (s.current < 0) s.current = 0;
                        else maxPoints++;
                        updateBonusText();
                        return true;
                    }
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
}