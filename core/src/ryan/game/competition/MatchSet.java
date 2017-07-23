package ryan.game.competition;

import ryan.game.Main;
import ryan.game.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatchSet {

    List<Match> matches = new ArrayList<>();

    public MatchSet(Match... m) {
        addMatches(m);
    }

    public void addMatches(Match...m) {
        for (Match match : m) {
            if (!matches.contains(m)) matches.add(match);
        }
    }

    public List<Match> getMatches() {
        return new ArrayList<>(matches);
    }

    public Match.MatchAlliance getWinner() {
        Utils.log("Matches: " + matches.size());
        int blueWins = 0, redWins = 0;
        for (Match m : matches) {
            if (m.blue.winner) blueWins++;
            else if (m.red.winner) redWins++;
        }
        Utils.log("b: " + blueWins + ", red: " + redWins);
        if (blueWins == 2) return matches.get(0).blue;
        else if (redWins == 2) return matches.get(0).red;
        else return null;
    }

    public static List<MatchSet> getSets(List<Match> matches) {
        HashMap<String, MatchSet> sets = new HashMap<>();
        for (Match m : matches) {
            for (Match m2 : matches) {
                if (!m.equals(m2)) {
                    if (m.getLevel().equalsIgnoreCase(m2.getLevel()) && Main.schedule.arraysEqual(m.blue.teams, m2.blue.teams) && Main.schedule.arraysEqual(m.red.teams, m2.red.teams)) {
                        MatchSet set = sets.get(m.blue.teams[0] + "" + m.blue.teams[1] + "" + m.blue.teams[2]);
                        if (set == null) {
                            set = new MatchSet();
                            sets.put(m.blue.teams[0] + "" + m.blue.teams[1] + "" + m.blue.teams[2], set);
                        }
                        set.addMatches(m);
                    }
                }
            }
        }
        return new ArrayList<>(sets.values());
    }
}
