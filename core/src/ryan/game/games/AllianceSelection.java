package ryan.game.games;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.RankData;
import ryan.game.competition.Rankings;
import ryan.game.controls.Gamepad;
import ryan.game.controls.Gamepads;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;
import ryan.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class AllianceSelection extends ImageDrawer {

    private static final String showAlliancesTex = "core/assets/alliance_selection_alliances.png";

    List<RankData> rankings;

    public boolean done = false;

    boolean showingAlliances = false;
    int selectingForAlliance = -1;
    int selectionRound = 1;

    int selectedGridX = 0, selectedGridY = 0;
    RankData selected = null;

    Sprite allianceBar;
    Sprite teamButton, teamButtonSelected;

    int allianceAmount = 8;
    int allianceSize = 3;

    public int[][] alliances = null;

    public AllianceSelection() {
        super(0, 0, showAlliancesTex);
        sprite.setSize(1100, 631);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        setDrawScaled(false);

        rankings = GameScreen.schedule.getRankings().getRankings();

        /*
        allianceBar = new Sprite(new Texture("core/assets/alliance_selection_bar.png"));
        resize(allianceBar, .25f);

        teamButton = new Sprite(new Texture("core/assets/alliance_selection_teambutton.png"));
        resize(teamButton, .25f);

        teamButtonSelected = new Sprite(new Texture("core/assets/alliance_selection_teambutton_selected.png"));
        resize(teamButtonSelected, .25f);*/

        if (GameScreen.schedule.getTeams().size() >= 24) allianceAmount = 8;
        else if (GameScreen.schedule.getTeams().size() >= 12) allianceAmount = 4;
        else if (GameScreen.schedule.getTeams().size() >= 6) allianceAmount = 2;

        alliances = new int[allianceAmount][allianceSize];

        Rankings r = GameScreen.schedule.getRankings();
        List rankings = r.getRankings();
        for (int i=0; i<allianceAmount; i++) {
            RankData d = (RankData) rankings.get(i);
            int team = d.getTeam();
            alliances[i][0] = team;
        }
        showingAlliances = true;
        setSelecting(0);
    }

    public void resize(Sprite s, float resize) {
        s.setSize(s.getWidth()*resize, s.getHeight()*resize);
    }

    public void setSelecting(int i) {
        selectingForAlliance = i;
        selectedGridX = 0;
        selectedGridY = 0;
    }

    boolean threeWasPressed = false;
    PovDirection prevDPadValue = PovDirection.center;

    @Override
    public void tick() {
        super.tick();
        Gamepad g = Gamepads.getGamepad(0);
        boolean threeVal = g.getButton(3);
        boolean pressed = threeVal && !threeWasPressed;
        if (!showingAlliances) {
            if (pressed) {
                int[] alliance = alliances[selectingForAlliance];
                alliance[selectionRound] = selected.getTeam();

                for (int i=0; i<alliances.length; i++) {
                    if (alliances[i][0] == selected.getTeam()) {
                        //An alliance captain has left their alliance to join another
                        for (int a=i; a<alliances.length; a++) {
                            if (alliances.length > a+1) {
                                alliances[a][0] = alliances[a + 1][0];
                            } else {
                                for (RankData d : rankings) {
                                    boolean onAlliance = false;
                                    for (int[] alli : alliances) {
                                        if (alli[0] == d.getTeam() || alli[2] == d.getTeam() || alli[1] == d.getTeam()) {
                                            onAlliance = true;
                                            break;
                                        }
                                    }
                                    if (!onAlliance) {
                                        alliances[a][0] = d.getTeam();
                                        break;
                                    }
                                }
                            }
                        }

                        break;
                    }
                }

                if (selectionRound == 1) {
                    selectingForAlliance++;
                    if (selectingForAlliance == allianceAmount) {
                        selectingForAlliance--;
                        selectionRound = 2;
                    }
                } else if (selectionRound == 2) {
                    selectingForAlliance--;
                    if (selectingForAlliance < 0) {
                        Utils.log("ALLIANCE SELECTION COMPLETE");
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        Utils.writeFile(GameScreen.EVENT_KEY + "/alliance_selection.json", gson.toJson(alliances));
                        done = true;
                    }
                }
                //TODO: when adding backup drafts add support for it here

                setSelecting(selectingForAlliance);
                showingAlliances = true;
            } else if (g.getButton(1)) {
                showingAlliances = true;
            }
            PovDirection dPad = g.getDPad();
            if (dPad != prevDPadValue) {
                if (dPad == PovDirection.south) {
                    selectedGridY--;
                } else if (dPad == PovDirection.east) {
                    selectedGridX++;
                } else if (dPad == PovDirection.north) {
                    selectedGridY++;
                } else if (dPad == PovDirection.west) {
                    selectedGridX--;
                }
                //TODO: limit to buttons that are visible
                if (selectedGridY > 7) selectedGridY = 0;
                if (selectedGridY < 0) selectedGridY = 7;

                if (selectedGridX > 7) selectedGridX = 0;
                if (selectedGridX < 0) selectedGridX = 7;
            }

            prevDPadValue = dPad;
        } else {
            if (pressed && !done) {
                showingAlliances = false;
            }
        }
        threeWasPressed = threeVal;
    }

    @Override
    public void draw(SpriteBatch batch) {
        sprite.setSize(Main.screenWidth, Main.screenHeight);
        sprite.setPosition(0-sprite.getWidth() /2, 0-sprite.getHeight()/2);
        super.draw(batch);


        allianceBar = new Sprite(new Texture("core/assets/alliance_selection_bar.png"));
        resize(allianceBar, .25f*Main.fontScale);

        teamButton = new Sprite(new Texture("core/assets/alliance_selection_teambutton.png"));
        resize(teamButton, .25f*Main.fontScale);

        teamButtonSelected = new Sprite(new Texture("core/assets/alliance_selection_teambutton_selected.png"));
        resize(teamButtonSelected, .25f*Main.fontScale);


        Fonts.drawCentered(Fonts.fmsBlack, GameScreen.EVENT_NAME + " Alliance" + (done ? "s" : " Selection"), getCenterX(), getCenterY() + 290*1.7f, batch);
        if (showingAlliances) {
            if (!done) Fonts.drawCentered(Fonts.fmsBlackSmall, "Alliance " + (selectingForAlliance+1) + " Is Picking", getCenterX(), getCenterY() + 265*1.7f, batch);
            for (int i=0; i<allianceAmount; i++) {
                float x = -allianceBar.getWidth()/2;
                float y = 135*1.7f;
                y -= ((i*56)*1.7f);

                allianceBar.setPosition(x,y);
                allianceBar.draw(batch);

                Fonts.draw(Fonts.fmsBlack, (i+1)+"", x + 67.5f, y + 52, batch);

                int[] teams = alliances[i];
                String teamString = "";
                for (int t : teams) {
                    if (t > 0) teamString += t + " - ";
                }
                teamString = teamString.substring(0, teamString.length()-3);

                Fonts.draw(Fonts.fmsBlack, teamString, x + 153, y + 52, batch);
            }
        } else {
            Fonts.drawCentered(Fonts.fmsBlackSmall, "Available Teams", getCenterX(), getCenterY() + 265*1.7f, batch);
            //TODO: probably want to calculate available teams once per screen appearance instead of EVERY RENDER FRAME
            List<RankData> available = new ArrayList<>(rankings);
            if (selectionRound == 1) { //Remove all teams on an alliance equal to or higher seeded than this one (i.e. 6 can only pick from 7 or 8)
                int currAlliance = selectingForAlliance;
                while (currAlliance >= 0) {
                    for (int t : alliances[currAlliance]) {
                        if (t > 0) {
                            for (RankData d : rankings) {
                                if (d.getTeam() == t) {
                                    available.remove(d);
                                    break;
                                }
                            }
                        }
                    }
                    currAlliance--;
                }
            } else if (selectionRound == 2 || selectionRound == 3) { //Remove all teams already on alliances
                for (int i=0; i<allianceAmount; i++) {
                    for (int team : alliances[i]) {
                        for (RankData d : rankings) {
                            if (d.getTeam() == team) available.remove(d);
                        }
                    }
                }
            }

            int index = 0;
            for (int gridX=0; gridX<8; gridX++) {
                for (int gridY=0; gridY<8; gridY++) {

                    float x = -500*1.7f;
                    float y = -250*1.7f;

                    float xOffset = (gridX* (120*1.7f));
                    float yOffset = (gridY*(56*1.7f));

                    if (available.size() > index) {
                        RankData d = available.get(index);
                        Sprite s = teamButton;
                        if (gridX == selectedGridX && gridY == selectedGridY) {
                            s = teamButtonSelected;
                            selected = d;
                        }
                        s.setPosition(x + (xOffset*Main.widthScale), y + (yOffset*Main.heightScale));
                        s.draw(batch);

                        Fonts.fmsWhiteSmall.setColor(Main.RED);
                        Fonts.drawCentered(Fonts.fmsWhiteSmall, d.rank + ".", x + 32.3f + xOffset, y + 57-7 + yOffset, batch);
                        Fonts.fmsWhiteSmall.setColor(Color.WHITE);

                        Fonts.draw(Fonts.fmsBlack, d.getTeam() + "", x + 85-10 + xOffset, y + 57-7 + yOffset, batch);
                    }
                    index++;
                }
            }
        }
    }
}