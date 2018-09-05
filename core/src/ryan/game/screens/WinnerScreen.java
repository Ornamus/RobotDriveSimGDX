package ryan.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Team;
import ryan.game.entity.steamworks.Fuel;
import ryan.game.entity.steamworks.Gear;
import ryan.game.render.Fonts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WinnerScreen extends Screen {

    float ui_visibility = 0;

    Music music = Gdx.audio.newMusic(Gdx.files.internal("core/assets/music/win_alt.wav"));
    Texture background = new Texture(Gdx.files.internal("core/assets/background.png"));

    List<GravityT> things = new ArrayList<>();
    Team captain, firstPick, secondPick;
    Sprite captainSprite, firstPickSprite, secondPickSprite;

    long start;


    public WinnerScreen() {}

    public WinnerScreen(Team captain, Team firstPick, Team secondPick) {
        this.captain = captain;
        this.firstPick = firstPick;
        this.secondPick = secondPick;
    }

    @Override
    public void init() {
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        if (captain == null || firstPick == null || secondPick == null) {
            List<Team> teams = Utils.teamListFromJSON(new File(GameScreen.EVENT_KEY + "/teams.json"));
            captain = teams.get(0);
            firstPick = teams.get(1);
            secondPick = teams.get(2);
        }

        captainSprite = new Sprite(Utils.colorImage(captain.robotStats.texture, captain.primary, Main.RED, captain.secondary));
        captainSprite.setBounds(0 - (125/2), 60 - (125/2), 125, 125);

        firstPickSprite = new Sprite(Utils.colorImage(firstPick.robotStats.texture, firstPick.primary, Main.RED, firstPick.secondary));
        firstPickSprite.setBounds(-500 - (125/2), 60 - (125/2), 125, 125);

        secondPickSprite = new Sprite(Utils.colorImage(secondPick.robotStats.texture, secondPick.primary, Main.RED, secondPick.secondary));
        secondPickSprite.setBounds(500 - (125/2), 60 - (125/2), 125, 125);

        GravityT.loadTexture(Gear.TEXTURE, 60, 60);
        GravityT.loadTexture(Fuel.TEXTURE, 24, 24);

        music.setVolume(.45f);
        music.play();
        //music.setPosition(7);
        start = System.currentTimeMillis();
    }

    public void gearSplode(float sX, float sY) {
        float gearsToSpawn = 9;
        final float r = 1;
        float angle = 0;
        while (angle < 360) {
            float x = sX + (float)Math.cos(angle)*r;
            float y = sY + (float)Math.sin(angle)*r;

            things.add(new GravityT(x - 30, y - 30, 60, 60, Gear.TEXTURE).xVel(350f*(float)Math.cos(angle)).yVel(350f*(float)Math.sin(angle)).spinRate(angle > 180 ? -150 : 150).angle(Utils.randomInt(0,359)));

            angle += (360 / gearsToSpawn);
        }
    }

    int part = 0;

    int tickWait = 9;

    @Override
    public void tick() {
        if (music.getPosition() >= .326 && part == 0) {
            gearSplode(-500,60);
            part = 1;
        }
        if (music.getPosition() >= 2.4 && part == 1) {
            gearSplode(500,60);
            part = 2;
        }
        if (music.getPosition() >= 4.5 && part == 2) {
            gearSplode(0,60);
            part = 3;
        }
        if (music.getPosition() >= 6 && part == 3) {
            part = 4;
        }
        if (music.getPosition() >= 8.3/*6.75*/ && part == 4) {
            part = 5;
        }
        if (music.getPosition() >= 10.7/*8.9*/ && part == 5) {
            part = 6;
        }
        if (music.getPosition() >= 10.7/*8.9*/ && part == 6) {
            part = 7;
            ui_visibility = 0;
            if (Utils.randomInt(1,100) == 100) things.add(new GravityT( Main.screenWidth/2, 300, 80, 80, new Texture("core/assets/moon.png")).xVel(-300f).yVel(20).spinRate(30).disableDecay());
        }
        if (music.getPosition() >= 17.46 && part == 7) {
            part = 8;
        }


        if (part >= 5 && part < 8) {
            if (tickWait >= 8) {
                things.add(new GravityT(Main.screenWidth/2 - 60, Main.screenHeight/2 + 30, 60, 60, Gear.TEXTURE).spinRate(150).yVel(50));
                things.add(new GravityT(-Main.screenWidth/2 + 0, Main.screenHeight/2 + 30, 60, 60, Gear.TEXTURE).spinRate(-150).yVel(50));

                if (part >= 6) {
                    things.add(new GravityT(Main.screenWidth / 2, -200, 24, 24, Fuel.TEXTURE).xVel(-520).yVel(540));
                    things.add(new GravityT(-24 - Main.screenWidth / 2, -200, 24, 24, Fuel.TEXTURE).xVel(520).yVel(540));
                }

                tickWait = 0;
            } else {
                tickWait++;
            }
        }

        if (part >= 7) {
            ui_visibility += 0.75f * Gdx.graphics.getDeltaTime();
            if (ui_visibility > 1) ui_visibility = 1;
        }
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
            firstPickSprite.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, firstPick.number + "", -500, -10, b);
            if (part >= 4) Fonts.drawCentered(Fonts.monoWhiteLarge, "First Pick", -500, -50, b);
        }
        if (part >= 2) {
            secondPickSprite.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, secondPick.number + "", 500, -10, b);
            if (part >= 4) Fonts.drawCentered(Fonts.monoWhiteLarge, "Second Pick", 500, -50, b);
        }

        if (part >= 3) {
            captainSprite.draw(b);
            Fonts.drawCentered(Fonts.monoWhiteLarge, captain.number + "", 0, -10, b);
            if (part >= 4) Fonts.drawCentered(Fonts.monoWhiteLarge, "Alliance Captain", 0, -50, b);
        }
        if (part >= 7) {
            Color oldColor = Fonts.fmsScore.getColor();
            Fonts.fmsScore.setColor(oldColor.r, oldColor.g, oldColor.b, ui_visibility);
            Fonts.drawCentered(Fonts.fmsScore, "WINNERS", 0, -300, b);
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

}

class GravityT {

    public static HashMap<Texture, Sprite> sprites = new HashMap<>();

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
        p = sprites.get(t);
        if (p == null) {
           loadTexture(t, width, height);
           p = sprites.get(t);
        }
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

    public GravityT angle(float f) {
        angle = f;
        return this;
    }

    public GravityT disableDecay() {
        gravity = false;
        loseXVel = false;
        return this;
    }

    public void tick() {
        float delta = 0.03f;

        x += xVel * delta;
        y += yVel * delta;

        if (gravity && yVel > -850) {
            yVel -= 6.5;
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
        p.setRotation(angle);
        p.draw(b);
    }

    public static void loadTexture(Texture t, float width, float height) {
        Sprite s = new Sprite(t);
        s.setSize(width, height);
        s.setOrigin(width/2,height/2);
        sprites.put(t, s);
    }
}