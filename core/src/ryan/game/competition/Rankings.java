package ryan.game.competition;

import ryan.game.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: Figure out how to support tiebreakers

public abstract class Rankings<T extends Rankings.RankData> {

    protected Schedule s;
    private List<T> currentRanks = new ArrayList<T>();

    public abstract T generate(Team team);
    public abstract void update(T data, Match.MatchAlliance alliance, Match.MatchAlliance other);
    public abstract List<T> rank(List<T> data);

    public int getRank(int team) {
        int rank = 1;
        for (T t : currentRanks) {
            if (t.getTeam() == team) {
                return rank;
            }
            rank++;
        }
        return -1;
    }

    public void calculate() {
        HashMap<Integer, T> data = new HashMap<>();
        for (Team t : s.getTeams()) {
            data.put(t.number, generate(t));
        }
        for (Match m : s.getQualifiers()) {
            if (m.complete) {
                Match.MatchAlliance[] alliances = new Match.MatchAlliance[]{m.blue, m.red};
                for (Match.MatchAlliance a : alliances) {
                    for (int i=0; i<a.teams.length; i++) {
                        T t = data.get(a.teams[i]);
                        update(t, a, m.getOther(a));
                    }
                }
            }
        }
        currentRanks = rank(new ArrayList<>(data.values()));

        Utils.log("=========");
        int rank = 1;
        for (T t : currentRanks) {
            Utils.log(rank + ": " + t.toString());
            rank++;
        }
    }

    public interface RankData {

        int getTeam();
    }
}
