package ryan.game.competition;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.games.steamworks.AllianceScoreData;
import ryan.game.games.steamworks.Steamworks;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;

public class RobotMaker extends ImageDrawer {

    String robot1 = "core/assets/robot_custom.png";
    Sprite current;

    public RobotMaker() {
        super(0, 0, "core/assets/alliance_selection_alliances.png");
        sprite.setSize(1188, 631);
        sprite.setPosition(0 - sprite.getWidth() / 2, 0 - sprite.getHeight() / 2);
        setDrawScaled(false);

        current = new Sprite(Utils.colorImage(robot1, Color.GREEN, Color.BLUE, Color.GOLD));

        current.setSize(100, 100);
        current.setPosition(0-50, 0);
    }

    @Override
    public void draw(SpriteBatch b) {
        sprite.setSize(Main.screenWidth, Main.screenHeight);
        sprite.setPosition(0 - sprite.getWidth() / 2, 0 - sprite.getHeight() / 2);

        super.draw(b);
        current.draw(b);
        /*
        float adjust = 15;

        Fonts.drawCentered(Fonts.fmsBlack, match.getName(), getCenterX(), getCenterY(), 0, 305-adjust, b);
        Fonts.drawCentered(Fonts.fmsBlackSmall, Steamworks.display.getEventName(), getCenterX(), getCenterY(), 0, 280-adjust, b);
        Fonts.drawCentered(Fonts.fmsBlackSmall, "Scoring System powered by Bacon", getCenterX(), getCenterY(), 0, 252-adjust, b);

        drawAlliance(getCenterX() - (Main.widthScale*405), getCenterY(), match.red, b);
        drawAlliance(getCenterX() + (Main.widthScale*70), getCenterY(), match.blue, b);

        blueWin.setBounds(getCenterX() + (206*Main.widthScale), getCenterY() - (295*Main.heightScale), 294*Main.widthScale, 77*Main.heightScale);
        if (match.blue.score > match.red.score) blueWin.draw(b);

        redWin.setBounds(getCenterX() + ((-206 - 294)*Main.widthScale), getCenterY() - (295*Main.heightScale), 294*Main.widthScale, 77*Main.heightScale);
        if (match.red.score > match.blue.score) redWin.draw(b);*/
    }
}