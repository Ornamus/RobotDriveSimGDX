package ryan.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.ai.Pathfinding;
import ryan.game.competition.RobotStats;
import ryan.game.competition.Schedule;
import ryan.game.competition.Team;
import ryan.game.controls.Gamepad;
import ryan.game.controls.Gamepads;
import ryan.game.entity.Robot;
import ryan.game.games.AllianceSelection;
import ryan.game.games.Field;
import ryan.game.games.Game;
import ryan.game.games.RankingDisplay;
import ryan.game.games.power.PowerRankings;
import ryan.game.games.power.PowerUp;
import ryan.game.games.steamworks.SteamRankings;
import ryan.game.games.steamworks.Steamworks;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.render.Drawable;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends Screen  {

    public static GameScreen self = null;

    public static boolean MANIPULATORS = false;
    public static int EXTRA_ROBOTS = 0;
    public static int SCHEDULE_ROUNDS = 8;
    public static boolean PLAY_MUSIC = true;
    public static boolean MAKE_SCHEDULE = false;
    public static boolean CUSTOM_TEAMS = false;
    public static int RANDOM_TEAMS = 24;
    public static String EVENT_NAME = "FIRST Championship";
    public static String EVENT_KEY = "debug";

    public Field field;
    public static Schedule schedule;
    public int currentRobot = 0;
    public static List<Robot> robots = new ArrayList<>();

    public static Sound matchStartSound, teleopStartSound, ropeDropSound, matchEndSound, foghornSound, popSound;
    FileHandle[] musicChoices;

    public static Drawable results = null;
    public Drawable rankings = null;
    Box2DDebugRenderer debugRenderer;


    public static boolean isShowingResults = false;
    public static Drawable allianceSelection = null;

    Music music = null;

    public static float time = 0;

    public static boolean matchPlay = false;
    public static long matchStart = 0;
    public static long matchEnd = 0;
    boolean didWhoop = false;
    boolean resetField = false;
    boolean wasHeld = false;
    Long upHeld = null;

    Pathfinding pathfinding;
    List<Point2D.Float> points;

    public List<Team> allTeams = new ArrayList<>();

    public GameScreen(){ }

    public void init() {
        self = this;
        field = new Steamworks();

        int index = 0;

        if (EXTRA_ROBOTS > 0) currentRobot = 0;

        if (MANIPULATORS) {
            boolean newRobot = true;
            Robot robot = null;
            Utils.log(Gamepads.getGamepads().size() + " controls");
            for (int i = 0; i < Gamepads.getGamepads().size(); i++) {
                if (newRobot) {
                    robot = Robot.create(field.getDefaultRobotStats(), 2 + (index * 3), -11);
                    robots.add(robot);
                    newRobot = false;
                } else {
                    newRobot = true;
                }
                if (i < Gamepads.getGamepads().size()) {
                    robot.claimGamepad(Gamepads.getGamepad(i));
                    Utils.log("Controller");
                }
                index++;
            }
        } else {
            for (int i = 0; i < Gamepads.getGamepads().size() + EXTRA_ROBOTS; i++) {
                Robot r = Robot.create(field.getDefaultRobotStats(), 2 + (index * 3), -11);
                robots.add(r);
                if (i < Gamepads.getGamepads().size()) {
                    r.claimGamepad(Gamepads.getGamepad(i));

                }
                index++;
            }
        }

        field.affectRobots();

        for (Drawable d : field.generateField()) {
            Main.getInstance().addDrawable(d);
        }

        for (Robot r : robots) {
            Main.getInstance().spawnEntity(r);
        }

        /*batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        unscaledBatch = new SpriteBatch();
        batch.setProjectionMatrix(unscaledCamera.combined);*/

        matchStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/charge_3.wav"));
        teleopStartSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/teleop.wav"));
        ropeDropSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/whoop.wav"));
        matchEndSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/end.wav"));
        foghornSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/foghorn.wav"));
        popSound = Gdx.audio.newSound(Gdx.files.internal("core/assets/sound/pop.wav"));

        musicChoices = Gdx.files.internal("core/assets/music/match_music").list();

        /*shape = new ShapeRenderer();
        shape.setAutoShapeType(true);
        shape.setProjectionMatrix(camera.combined);*/

        schedule = new Schedule(new SteamRankings());
        schedule.generate(SCHEDULE_ROUNDS);

        /*
        Match fake = new Match(4, new int[]{1,2,3}, new int[]{4,5,6});
        fake.qualifier = false;
        fake.blue.breakdown = new AllianceScoreData(true);
        fake.red.breakdown = new AllianceScoreData(false);
        drawables.add(new SteamResultDisplay(fake));
        */

        //schedule.getRankings().addFakeRankings();
        //drawables.add(new AllianceSelection());

        //field.updateMatchInfo();
        field.updateHumanSprites();
    }



    @Override
    public void tick() {
        if (Game.isPlaying() && Game.getMatchTime() <= 0) {
            matchEndSound.play(.6f);
            if (PLAY_MUSIC && music != null) {
                if (music.isPlaying()) music.stop();
                music.dispose();
                music = null;
            }
            matchPlay = false;
            didWhoop = false;
            resetField = true;
            matchEnd = getTime();
            field.onMatchEnd();
        }

        if (field != null) field.tick();

        boolean controllerStartMatch = false;
        boolean anyHeld = false;
        for (Gamepad g : Gamepads.getGamepads()) {
            if (g.getDPad() == PovDirection.north) {
                anyHeld = true;
                if (upHeld == null) upHeld = getTime();
                else if (getTime() - upHeld >= 2000) {
                    controllerStartMatch = true;
                }
                break;
            }
        }
        if (!anyHeld) upHeld = null;
        if ((controllerStartMatch || Gdx.input.isKeyPressed(Input.Keys.P)) && !matchPlay && !isShowingResults && allianceSelection == null && rankings == null) {
            matchEnd = 0;
            field.onMatchStart();
            resetField = true;
            matchPlay = true;
            matchStart = getTime();
            matchStartSound.play(.45f);
            if (PLAY_MUSIC) {
                music = Gdx.audio.newMusic(musicChoices[Utils.randomInt(0, musicChoices.length - 1)]);
                music.setVolume(.1f);
                music.play();
            }
            controllerStartMatch = false;
            upHeld = null;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.I) || controllerStartMatch) {
            if (matchPlay) {
                foghornSound.play(.25f);
                matchPlay = false;
                didWhoop = false;
                if (PLAY_MUSIC && music.isPlaying()) music.stop();
            } else if (isShowingResults) {
                Main.removeDrawable(results);
                results = null;
                isShowingResults = false;
                if (schedule.getCurrentMatch() == null) {
                    if (!schedule.elims) {
                        allianceSelection = new AllianceSelection();
                        Main.addDrawable(allianceSelection);
                    } else {
                        schedule.elimsUpdate();
                    }
                }
            } else if (allianceSelection != null) {
                AllianceSelection a = (AllianceSelection) allianceSelection;
                if (a.done) {
                    Main.removeDrawable(a);
                    schedule.startElims(a.alliances);
                    field.updateMatchInfo();
                    allianceSelection = null;
                }
            }
            upHeld = null;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R) || resetField) {
            field.resetField(Main.drawables);
            resetField = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            if (rankings == null) {
                if (MAKE_SCHEDULE) {
                    rankings = new RankingDisplay();
                    Main.addDrawable(rankings);
                }
            } else {
                Main.removeDrawable(rankings);
                rankings = null;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            for (Robot r : new ArrayList<>(robots)) {
                r.onGamepadDisconnect();
            }
            robots.clear();
            if (music != null) music.stop();
            matchPlay = false;
            Main.getInstance().setScreen(new TitleScreen());
        }
        if (Gamepads.getGamepads().size() != robots.size() && Gamepads.getGamepads().size() == 1) {
            Gamepad one = Gamepads.getGamepad(0);
            if (one.getButton(Gamepad.JOY_RIGHT)) {
                if (!wasHeld) {
                    currentRobot++;
                    if (currentRobot == robots.size()) {
                        currentRobot = 0;
                    }
                }
                wasHeld = true;
            } else {
                wasHeld = false;
            }
        }
    }

    @Override
    public void draw(SpriteBatch b) {
        field.draw(b);
    }

    @Override
    public void drawUnscaled(SpriteBatch b) {}

    public static long getTime() {
        return Math.round(time*1000);
    }
}
