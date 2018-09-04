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
import ryan.game.entity.steamworks.Fuel;
import ryan.game.entity.steamworks.Gear;
import ryan.game.games.RobotStatBuilder;
import ryan.game.games.RobotStatSlider;
import ryan.game.games.steamworks.SteamStatBuilder;
import ryan.game.games.steamworks.robots.SteamRobotStats;
import ryan.game.render.Button;
import ryan.game.render.Fonts;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WinnerScreen extends Screen {

    float ui_visibility = 0;

    Music music = Gdx.audio.newMusic(Gdx.files.internal("core/assets/music/win.wav"));
    Texture background = new Texture(Gdx.files.internal("core/assets/background.png"));

    List<GravityT> things = new ArrayList<>();
    Sprite robot1, robot2, robot3;

    long start;

    @Override
    public void init() {
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        robot1 = new Sprite(Utils.colorImage("core/assets/robot_custom2.png", Color.GRAY, Main.RED, Color.PURPLE));
        robot1.setBounds(-500 - (125/2), 60 - (125/2), 125, 125);

        robot2 = new Sprite(Utils.colorImage("core/assets/robot_custom3.png", Color.GRAY, Main.RED, Color.GREEN));
        robot2.setBounds(0 - (125/2), 60 - (125/2), 125, 125);

        robot3 = new Sprite(Utils.colorImage("core/assets/robot_custom5.png", Color.WHITE, Main.RED, Main.BLUE));
        robot3.setBounds(500 - (125/2), 60 - (125/2), 125, 125);

        //music.setLooping(true);
        music.setVolume(.65f);
        music.play();
        start = System.currentTimeMillis();
    }

    public void gearSplode(float sX, float sY) {
        float gearsToSpawn = 9;
        final float r = 1;
        float angle = 0;
        while (angle < 360) {
            float x = sX + (float)Math.cos(angle)*r;//a + r * sin(angle);
            float y = sY + (float)Math.sin(angle)*r;//b + r * cos(angle);

            things.add(new GravityT(x - 30, y - 30, 60, 60, Gear.TEXTURE).xVel(350f*(float)Math.cos(angle)).yVel(350f*(float)Math.sin(angle)).spinRate(angle > 180 ? -150 : 150));

            angle += (360 / gearsToSpawn);
        }

        /*for (int i=0; i<8; i++) {

        }
        things.add(new GravityT(x, y, 60, 60, Gear.TEXTURE).xVel(300).yVel(0).spinRate(90));
        things.add(new GravityT(x, y, 60, 60, Gear.TEXTURE).xVel(200).yVel(200).spinRate(90));
        things.add(new GravityT(x, y, 60, 60, Gear.TEXTURE).xVel(-200).yVel(200).spinRate(90));
        things.add(new GravityT(x, y, 60, 60, Gear.TEXTURE).xVel(-300).yVel(0).spinRate(90));*/
    }

    int part = 0;

    int tickWait = 9;

    @Override
    public void tick() {
        if (music.getPosition() >= .410 && part == 0) {
            gearSplode(-500,60);
            part = 1;
        }
        if (music.getPosition() >= 1.958 && part == 1) {
            gearSplode(0,60);
            part = 2;
        }
        if (music.getPosition() >= 3.521 && part == 2) {
            gearSplode(500,60);
            part = 3;
        }
        if (music.getPosition() >= 6.017 && part == 3) {
            part = 4;
        }
        if (music.getPosition() >= 6.417 && part == 4) {
            part = 5;
        }
        if (music.getPosition() >= 8.156 && part == 5) {
            part = 6;
            ui_visibility = 0;
        }


        if (part >= 4) {
            if (tickWait >= 8) {
                things.add(new GravityT(Main.screenWidth/2 - 60, Main.screenHeight/2 + 30, 60, 60, Gear.TEXTURE).spinRate(150).yVel(50));
                things.add(new GravityT(-Main.screenWidth/2 + 0, Main.screenHeight/2 + 30, 60, 60, Gear.TEXTURE).spinRate(-150).yVel(50));

                if (part >= 5) {
                    things.add(new GravityT(Main.screenWidth / 2, -200, 24, 24, Fuel.TEXTURE).xVel(-520).yVel(540));
                    things.add(new GravityT(-24 - Main.screenWidth / 2, -200, 24, 24, Fuel.TEXTURE).xVel(520).yVel(540));
                }

                tickWait = 0;
            } else {
                tickWait++;
            }
        }

        if (part >= 6) {
            ui_visibility += 0.75f * Gdx.graphics.getDeltaTime();
            if (ui_visibility > 1) ui_visibility = 1;
        }
        //music.setVolume(ui_visibility * 0.25f);
        for (GravityT thing : new ArrayList<>(things)) {
            if (thing.y < -Main.screenWidth*0.75) {
                things.remove(thing);
            } else {
                thing.tick();
            }
        }
    }

    @Override
    public void draw(SpriteBatch b) { }

    float scrollPoint = -Main.screenWidth/2;
    float backX1 = scrollPoint;
    float backX2 = 9999;

    int blockLength = 387*5; //5 complete loops of the image are just over the width of the screen

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


        if (part >= 1) {
            robot1.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, "2061", -500, -10, b);
            if (part >= 4) Fonts.drawCentered(Fonts.monoWhiteLarge, "First Pick", -500, -50, b);
        }
        if (part >= 2) {
            robot2.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, "472", 0, -10, b);
            if (part >= 4) Fonts.drawCentered(Fonts.monoWhiteLarge, "Alliance Captain", 0, -50, b);
        }
        if (part >= 3) {
            robot3.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, "7892", 500, -10, b);
            if (part >= 4) Fonts.drawCentered(Fonts.monoWhiteLarge, "Second Pick", 500, -50, b);
        }

        if (part >= 6) {
            Color oldColor = Fonts.fmsScore.getColor();
            Fonts.fmsScore.setColor(oldColor.r, oldColor.g, oldColor.b, ui_visibility);
            Fonts.drawCentered(Fonts.fmsScore, "STEAMWORKS WINNERS", 0, -300, b);
            Fonts.fmsScore.setColor(oldColor);
        }

        for (GravityT thing : things) {
            thing.draw(delta, b);
        }

    }

    @Override
    public boolean click(Vector3 pos, int button) {
        return false;
    }

    class GravityT {
        Sprite p;
        float x, y;
        float xVel = 0, yVel = 0;
        float angle = 0;
        float spinRate = 0;
        boolean gravity = true;
        boolean loseXVel = true;

        GravityT(float x, float y, float width, float height, Texture t) {
            this.x = x;
            this.y = y;
            p = new Sprite(t);
            p.setBounds(x, y, width, height);
        }

        public GravityT xVel(float f) {
            xVel = f;
            return this;
        }

        public GravityT yVel(float f) {
            yVel = f;
            return this;
        }

        public GravityT spinRate(float f) {
            spinRate = f;
            return this;
        }

        public void tick() {
            float delta = 0.03f;//Gdx.graphics.getDeltaTime();

            x += xVel * delta;
            y += yVel * delta;

            if (gravity && yVel > -850) {
                yVel -= 6.5;
            } else {
                //Utils.log("max");
            }
            if (!hitZero && loseXVel && xVel != 0) {
                if (xVel > 0) {
                    xVel -= 1;
                    if (xVel < 0) {
                        xVel = 0;
                        hitZero = true;
                    }
                } else {
                    xVel += 1;
                    if (xVel > 0) {
                        xVel = 0;
                        hitZero = true;
                    }
                }
            }

            angle += spinRate * delta;
        }

        boolean hitZero = false;
        public void draw(float delta, SpriteBatch b) {

            p.setPosition(x, y);
            p.setOrigin(30,30);
            p.setRotation(angle);
            p.draw(b);
        }
    }
}