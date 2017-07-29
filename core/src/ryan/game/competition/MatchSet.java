package ryan.game.competition;

import ryan.game.Main;
import ryan.game.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MatchSet {

    List<Match> matches = new ArrayList<>();

    public MatchSet(Match... m) {
        addMatches(m);
    }

    public boolean belongsInSet(Match m) {
        if (matches.size() > 0) {
            return Arrays.equals(matches.get(0).blue.teams, m.blue.teams);
        }
        return true;
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
        List<MatchSet> sets = new ArrayList<>();
        for (int i=0; i<Main.schedule.remainingAlliances.length/2; i++) {
            sets.add(new MatchSet());
        }
        List<Match> taken = new ArrayList<>();
        for (MatchSet set : sets) {
            for (Match m : matches) {
                if (set.belongsInSet(m) && !taken.contains(m)) {
                    set.addMatches(m);
                    taken.add(m);
                }
            }
        }
        return sets;
        /*
        HashMap<String, MatchSet> sets = new HashMap<>();
        for (Match m : matches) {
            for (Match m2 : matches) {
                if (!m.equals(m2)) {
                    if (m.getLevel().equalsIgnoreCase(m2.getLevel()) && Arrays.equals(m.blue.teams, m2.blue.teams) && Arrays.equals(m.red.teams, m2.red.teams)) {
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
        return new ArrayList<>(sets.values());*/
    }
}
