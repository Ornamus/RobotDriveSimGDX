package ryan.game.competition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.Main;
import ryan.game.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Match implements Serializable {

    public final int number;
    public final MatchAlliance red, blue;
    public List<Integer> blueSurrogates = new ArrayList<>();
    public List<Integer> redSurrogates = new ArrayList<>();
    public boolean qualifier = true;
    public String level = "qm"; //qm, qf, sf, f
    public boolean complete = false;

    public Match(int number, int[] b, int[] r) {
        this.number = number;
        blue = new MatchAlliance(b);
        red = new MatchAlliance(r);
    }

    public MatchAlliance getAlliance(int team) {
        for (int i=0; i<blue.teams.length; i++) {
            if (blue.teams[i] == team) {
                return blue;
            }
        }
        for (int i=0; i<red.teams.length; i++) {
            if (red.teams[i] == team) {
                return red;
            }
        }
        return null;
    }

    public MatchAlliance getOther(MatchAlliance a) {
        if (a == blue) return red;
        if (a == red) return blue;
        return null;
    }

    public boolean isSurrogate(int team) {
        return blueSurrogates.contains(team) || redSurrogates.contains(team);
    }

    public void setBlueSurrogates(List<Integer> indexes) {
        blueSurrogates = indexes;
    }

    public void setRedSurrogates(List<Integer> indexes) {
        redSurrogates = indexes;
    }

    @Override
    public String toString() {
        String blueString = "", redString = "";
        for (int i=0; i<blue.teams.length; i++) {
            int t = blue.teams[i];
            boolean surrogate = blueSurrogates.contains(i);
            blueString += t + (surrogate ? "*" : "") + (i+1 != blue.teams.length ? ", " : "");
        }

        for (int i=0; i<red.teams.length; i++) {
            int t = red.teams[i];
            boolean surrogate = redSurrogates.contains(i);
            redString += t + (surrogate ? "*" : "") + (i+1 != red.teams.length ? ", " : "");
        }

        return "Match " + number + ": " + blueString + "  vs  " + redString;
    }

    public void save() {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Utils.writeFile("matches/" + Main.eventKey + "_" + level + "_" + number, g.toJson(this));
    }

    public static Match loadMatch(String level, int number) {
        return loadMatch(Main.eventKey, level, number);
    }

    public static Match loadMatch(String event, String level, int number) {
        //TODO
        return null;
    }

    public class MatchAlliance implements Serializable {
        public int[] teams;
        public int score = 0;
        public boolean winner = false;
        public Object breakdown = null;

        public MatchAlliance(int[] teams) {
            this.teams = teams;
        }
    }
}


