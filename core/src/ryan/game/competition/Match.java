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
    private String level = "qm"; //qm, qf, sf, f
    public boolean complete = false;
    public boolean tiebreaker = false;

    public Match(int number, int[] b, int[] r) {
        this.number = number;
        blue = new MatchAlliance(b);
        red = new MatchAlliance(r);
    }

    public String getLevel() {
        return level;
    }

    public Match setQuarterfinal() {
        return setLevel("qf");
    }

    public Match setSemifinal() {
        return setLevel("sf");
    }

    public Match setFinal() {
        return setLevel("f");
    }

    public Match setLevel(String s) {
        level = s;
        if (!s.equalsIgnoreCase("qm")) qualifier = false;
        return this;
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

    public boolean isInMatch(int team) {
        for (int i : blue.teams) {
            if (i == team) return true;
        }
        for (int i : red.teams) {
            if (i == team) return true;
        }
        return false;
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

    public String getName() {
        if (qualifier) {
            return "Qualification " + number + " of " + Main.schedule.getQualifiers().size();
        } else {
            String type = "Elimination";
            int total = -1;
            if (level.equalsIgnoreCase("qf")) {
                type = "Quarterfinal";
                total = 8;
            } else if (level.equalsIgnoreCase("sf")) {
                type = "Semifinal";
                total = 4;
            } else if (level.equalsIgnoreCase("f")) {
                type = "Final";
                total = -1;
            }
            if (total == -1 || tiebreaker) {
                return type + (tiebreaker && !type.equals("Final") ? (" Tiebreaker ") : " ") + number;
            } else {
                return type + " " + number + " of " + total;
            }
        }
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
        Utils.writeFile(Main.eventKey + "/matches/" + level + (tiebreaker ?  "_tb" : "") + "_" + number, g.toJson(this));
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


