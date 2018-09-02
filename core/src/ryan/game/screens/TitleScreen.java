package ryan.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;

public class TitleScreen extends Screen {

    float ui_visible = 0;

    Music music = Gdx.audio.newMusic(Gdx.files.internal("core/assets/music/title_2.wav"));
    Sprite frc_logo = new Sprite(new Texture(Gdx.files.internal("core/assets/frc_logo2.png")));
    Sprite tournamentButton = new Sprite(new Texture(Gdx.files.internal("core/assets/ui/tournament.png")));
    Sprite builderButton = new Sprite(new Texture(Gdx.files.internal("core/assets/ui/builder.png")));
    Sprite settingsButton = new Sprite(new Texture(Gdx.files.internal("core/assets/ui/settings.png")));
    Texture background = new Texture(Gdx.files.internal("core/assets/background.png"));

    @Override
    public void init() {
        music.setLooping(true);
        music.setVolume(0.25f);
        frc_logo.setScale(.65f);
        frc_logo.setPosition((-Main.screenWidth/2) - 90, (Main.screenHeight/2) - 200);

        tournamentButton.setScale(1.25f);
        tournamentButton.setPosition(-tournamentButton.getWidth()/2, 0);

        builderButton.setScale(1.25f);
        builderButton.setPosition(-builderButton.getWidth()/2, -120);

        settingsButton.setScale(1.25f);
        settingsButton.setPosition(-settingsButton.getWidth()/2, -240);

        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    }

    @Override
    public void tick() {
        if (ui_visible >= 0.25 && !music.isPlaying()) {
            music.play();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            music.stop();
            //TODO: pretty transition
            Main.getInstance().setScreen(new GameScreen());
        }
    }

    float scrollPoint = -Main.screenWidth/2;
    float backX1 = scrollPoint;
    float backX2 = 9999;

    int blockLength = 387*5; //5 complete loops of the image are just over the width of the screen

    @Override
    public void draw(SpriteBatch b) {
        float delta = Gdx.graphics.getDeltaTime();
        ui_visible += 0.75f * delta;
        if (ui_visible > 1) ui_visible =1;

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

        frc_logo.setAlpha(ui_visible);
        frc_logo.draw(b);

        tournamentButton.setAlpha(ui_visible);
        tournamentButton.draw(b);

        builderButton.setAlpha(ui_visible);
        builderButton.draw(b);

        settingsButton.setAlpha(ui_visible);
        settingsButton.draw(b);
    }
}
