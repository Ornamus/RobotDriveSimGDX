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
import java.util.List;

public class RankingDisplay extends ImageDrawer {

    private static final String tex = "core/assets/alliance_selection_alliances.png";

    List<RankData> rankings;

    Sprite allianceBar;

    int currentPage = 0;
    int pages;

    public RankingDisplay() {
        super(0, 0, tex);
        sprite.setSize(1100, 631);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        setDrawScaled(false);

        rankings = Main.schedule.getRankings().getRankings();

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
        super.draw(batch);
        Fonts.drawCentered(Main.eventName + " Rankings", getCenterX(), getCenterY() + 305 - 15, Fonts.fmsBlack, batch);
        int index = 0;
        for (int gridX = 0; gridX<2; gridX++) {
            for (int gridY = 0; gridY < 8; gridY++) {
                if (rankings.size() > (currentPage*16)+index) {
                    RankData data = rankings.get((currentPage*16)+index);

                    float x = -485 + (500 * gridX);
                    float y = 135;
                    y -= (gridY * 56);

                    allianceBar.setPosition(x, y);
                    allianceBar.draw(batch);

                    Fonts.drawCentered(data.rank + "", x + 44.5f, y + 32.5f, Fonts.fmsBlack, batch);

                    SteamTeamData d = (SteamTeamData) data; //TODO: this will crash for any game other than Steamworks

                    String teamString = d.getTeam() + "  |  " + Utils.roundToPlace(d.rankingPoints/d.matchesPlayed, 2) + " RP  |  " + d.scores + " points";

                    //teamString = teamString.substring(0, teamString.length() - 3);

                    Fonts.fmsBlack.draw(batch, teamString, x + 90f, y + 31.5f);
                }
                index++;
            }
        }
    }
}