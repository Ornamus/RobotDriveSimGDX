package ryan.game.competition;

import ryan.game.games.steamworks.SteamTeamData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: Figure out how to standardize tiebreakers and support them here

public abstract class Rankings<T extends RankData> {

    protected Schedule s;
    private List<T> previousRanks = new ArrayList<T>();
    private List<T> currentRanks = new ArrayList<T>();

    public abstract T generate(Team team);
    public abstract void update(T data, Match.MatchAlliance alliance, Match.MatchAlliance other);
    public abstract List<T> rank(List<T> data);

    int[] fakeranks = {1902, 254, 1557, 4013, 987, 27, 33, 180, 233, 1987, 1114, 2056, 1678, 604, 3132, 597, 1241, 5816, 4613, 2767, 4118, 79, 744, 1592, 1523, 125};

    public void addFakeRankings() {
        for (int i : fakeranks) {
            final int num = i;
            //TODO: revert
            currentRanks.add((T)new SteamTeamData(num));
            /*
            currentRanks.add((T) new RankData() {
                @Override
                public int getTeam() {
                    return num;
                }
            });*/
        }
    }

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

    public int getPreviousRank(int team) {
        int rank = 1;
        for (T t : previousRanks) {
            if (t.getTeam() == team) {
                return rank;
            }
            rank++;
        }
        return -1;
    }

    public List<T> getRankings() {
        return new ArrayList<>(currentRanks);
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
                        int team = a.teams[i];
                        if (!m.isSurrogate(team)) {
                            T t = data.get(team);
                            update(t, a, m.getOther(a));
                        }
                    }
                }
            }
        }
        previousRanks = currentRanks;
        currentRanks = rank(new ArrayList<>(data.values()));

        int rank = 1;
        for (T t : currentRanks) {
            t.rank = rank;
            rank++;
        }
    }
}
