package ryan.game.games;

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
import ryan.game.controls.ControllerManager;
import ryan.game.controls.Gamepad;
import ryan.game.render.Fonts;
import ryan.game.render.ImageDrawer;
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

        rankings = Main.schedule.getRankings().getRankings();

        /*
        allianceBar = new Sprite(new Texture("core/assets/alliance_selection_bar.png"));
        resize(allianceBar, .25f);

        teamButton = new Sprite(new Texture("core/assets/alliance_selection_teambutton.png"));
        resize(teamButton, .25f);

        teamButtonSelected = new Sprite(new Texture("core/assets/alliance_selection_teambutton_selected.png"));
        resize(teamButtonSelected, .25f);*/

        if (Main.schedule.getTeams().size() >= 24) allianceAmount = 8;
        else if (Main.schedule.getTeams().size() >= 12) allianceAmount = 4;
        else if (Main.schedule.getTeams().size() >= 6) allianceAmount = 2;

        alliances = new int[allianceAmount][allianceSize];

        Rankings r = Main.schedule.getRankings();
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
    PovDirection prevDPadValue = 0;

    @Override
    public void tick() {
        super.tick();
        Gamepad g = Gamepads.getGamepad(0);
        boolean threeVal = g.getButton(3).get();
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
                        Utils.writeFile(Main.eventKey + "/alliance_selection.json", gson.toJson(alliances));
                        done = true;
                    }
                }
                //TODO: when adding backup drafts add support for it here

                setSelecting(selectingForAlliance);
                showingAlliances = true;
            } else if (g.getButton(1).get()) {
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


        Fonts.drawCentered(Fonts.fmsBlack, Main.eventName + " Alliance" + (done ? "s" : " Selection"), getCenterX(), getCenterY(), 0, 305-15, batch);
        //Fonts.drawCentered(Main.eventName + " Alliance" + (done ? "s" : " Selection"), getCenterX(), getCenterY() + 305-15, Fonts.fmsBlack, batch);
        if (showingAlliances) {
            if (!done) Fonts.drawCentered(Fonts.fmsBlackSmall, "Alliance " + (selectingForAlliance+1) + " Is Picking", getCenterX(), getCenterY(), 0, 280-15, batch);
            //TODO: fix minor text alignment bug that happens when resized at certain sizes
            for (int i=0; i<allianceAmount; i++) {
                float x = -270*Main.widthScale;
                float y = 135*Main.heightScale;
                y -= ((i*56)*Main.heightScale);

                allianceBar.setPosition(x,y);
                allianceBar.draw(batch);

                Fonts.draw(Fonts.fmsBlack, (i+1)+"", x, y, 48.5f, 33.5f, batch);
                //Fonts.drawCentered((i+1)+"", x+48.5f, y+33.5f, Fonts.fmsBlack, batch);

                int[] teams = alliances[i];
                String teamString = "";
                for (int t : teams) {
                    if (t > 0) teamString += t + " - ";
                }
                teamString = teamString.substring(0, teamString.length()-3);

                Fonts.draw(Fonts.fmsBlack, teamString, x, y, 90, 33.5f, batch);
                //Fonts.fmsBlack.draw(batch, teamString, x+90f, y+33.5f);
            }
        } else {
            Fonts.drawCentered(Fonts.fmsBlackSmall, "Available Teams", getCenterX(), getCenterY(), 0, 280-15, batch);
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

                    float x = -500*Main.widthScale;
                    float y = -250*Main.heightScale;

                    float xOffset = (gridX*120);
                    float yOffset = (gridY*56);

                    if (available.size() > index) {
                        RankData d = available.get(index);
                        Sprite s = teamButton;
                        if (gridX == selectedGridX && gridY == selectedGridY) {
                            s = teamButtonSelected;
                            selected = d;
                        }
                        s.setPosition(x + (xOffset*Main.widthScale), y + (yOffset*Main.heightScale));
                        s.draw(batch);

                        Fonts.fmsWhiteSmall.setColor(Color.RED);
                        Fonts.drawCentered(Fonts.fmsWhiteSmall, d.rank + "", x, y, 19+xOffset, 33.5f+yOffset, batch);
                        Fonts.fmsWhiteSmall.setColor(Color.WHITE);

                        Fonts.draw(Fonts.fmsBlack, d.getTeam() + "", x, y, 50+xOffset, 33.5f+yOffset, batch);
                    }
                    index++;
                }
            }
        }
    }
}