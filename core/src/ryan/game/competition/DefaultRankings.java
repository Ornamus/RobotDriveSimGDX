package ryan.game.competition;

import java.util.ArrayList;
import java.util.List;

public class DefaultRankings extends Rankings<TeamData> {

    @Override
    public TeamData generate(Team team) {
        return new TeamData(team.number);
    }

    @Override
    public void update(TeamData data, Match.MatchAlliance alliance, Match.MatchAlliance other) {
        data.rankingPoints += (alliance.winner ? 2 : (other.winner ? 0 : 1));
        data.matchesPlayed++;
    }

    @Override
    public List<TeamData> rank(List<TeamData> original) {
        List<TeamData> d = new ArrayList<>(original);
        for (TeamData t : original) {
            if (t.matchesPlayed == 0) d.remove(t);
        }
        d.sort((o1, o2) -> Math.round(((o2.rankingPoints / o2.matchesPlayed) - (o1.rankingPoints / o1.matchesPlayed)))*1000);
        return d;
    }
}

class TeamData extends RankData {
    int team;
    float rankingPoints = 0;
    float matchesPlayed = 0;

    TeamData(int t) {
        team = t;
    }

    @Override
    public int getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return team + " - " + (rankingPoints / matchesPlayed) + " RP";
    }
}
