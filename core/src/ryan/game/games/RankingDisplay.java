package ryan.game.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.RankData;
import ryan.game.games.steamworks.SteamTeamData;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;
import ryan.game.screens.GameScreen;

import java.util.List;

public class RankingDisplay extends ImageDrawer {

    private static final String tex = "core/assets/alliance_selection_alliances.png";

    List<RankData> rankings;

    Sprite allianceBar;

    int currentPage = 0;
    int pages;

    public RankingDisplay() {
        super(0, 0, tex);
        sprite.setSize(Main.screenWidth, Main.screenHeight);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        setDrawScaled(false);

        rankings = GameScreen.schedule.getRankings().getRankings();

        allianceBar = new Sprite(new Texture("core/assets/alliance_selection_bar.png"));
        resize(allianceBar, .225f);

        pages = (int) Math.ceil(rankings.size() / 16f);

        Utils.log("Pages needed for " + rankings.size() + " teams: " + pages);
    }

    public void resize(Sprite s, float resize) {
        s.setSize(s.getWidth()*resize, s.getHeight()*resize);
    }

    @Override
    public void tick() {
        super.tick();
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            currentPage++;
            if (currentPage == pages) {
                currentPage = 0;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            currentPage--;
            if (currentPage < 0) {
                currentPage = pages-1;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setSize(Main.screenWidth, Main.screenHeight);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        super.draw(batch);

        allianceBar = new Sprite(new Texture("core/assets/alliance_selection_bar.png"));
        resize(allianceBar, .225f * Main.fontScale);

        Fonts.drawCentered(Fonts.fmsBlack, GameScreen.EVENT_NAME + " Rankings", getCenterX(), getCenterY() + (305-15)*1.7f, batch);
        int index = 0;
        for (int gridX = 0; gridX<2; gridX++) {
            for (int gridY = 0; gridY < 8; gridY++) {
                if (rankings.size() > (currentPage*16)+index) {
                    RankData data = rankings.get((currentPage*16)+index);

                    float x = -485*1.7f + (550 * gridX)*1.7f;
                    float y = 135 * 1.7f;
                    y -= (gridY * 56 * 1.7f);

                    allianceBar.setPosition(x, y);
                    allianceBar.draw(batch);

                    Fonts.drawCentered(Fonts.fmsBlack, data.rank + "", x + 67, y + 47, batch);

                    SteamTeamData d = (SteamTeamData) data; //TODO: make this not game-specific

                    String teamString = d.getTeam() + "  |  " + Utils.roundToPlace(d.rankingPoints/d.matchesPlayed, 2) + " RP  |  " + d.scores + " points";

                    //teamString = teamString.substring(0, teamString.length() - 3);

                    Fonts.draw(Fonts.fmsBlack, teamString, x + 90*1.7f, y + 50, batch);
                }
                index++;
            }
        }
    }
}
