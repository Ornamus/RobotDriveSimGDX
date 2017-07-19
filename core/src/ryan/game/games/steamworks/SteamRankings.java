package ryan.game.games.steamworks;

import ryan.game.competition.*;
import java.util.ArrayList;
import java.util.List;

public class SteamRankings extends Rankings<SteamRankings.TeamData> {

    @Override
    public TeamData generate(Team team) {
        return new TeamData(team.number);
    }

    @Override
    public void update(TeamData data, Match.MatchAlliance alliance, Match.MatchAlliance other) {
        AllianceScoreData scoreInfo = (AllianceScoreData) alliance.breakdown;
        data.rankingPoints += (alliance.winner ? 2 : (other.winner ? 0 : 1));
        data.rankingPoints += (scoreInfo.rotors == 4 ? 1 : 0) + (scoreInfo.kPA >= 40 ? 1 : 0);
        data.matchesPlayed++;
    }

    @Override
    public List<TeamData> rank(List<TeamData> original) {
        List<TeamData> d = new ArrayList<>(original);
        for (TeamData t : original) {
            if (t.matchesPlayed == 0) d.remove(t);
        }
        d.sort((o1, o2) -> (o2.rankingPoints / o2.matchesPlayed) - (o1.rankingPoints / o1.matchesPlayed));
        return d;
    }

    class TeamData {
        int team;
        int rankingPoints = 0;
        int matchesPlayed = 0;

        TeamData(int t) {
            team = t;
        }

        @Override
        public String toString() {
            return team + " - " + (rankingPoints/matchesPlayed) + " RP";
        }
    }
}
