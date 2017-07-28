package ryan.game.games.steamworks;

import ryan.game.Utils;
import ryan.game.competition.*;
import java.util.ArrayList;
import java.util.List;

public class SteamRankings extends Rankings<SteamTeamData> {

    @Override
    public SteamTeamData generate(Team team) {
        return new SteamTeamData(team.number);
    }

    @Override
    public void update(SteamTeamData data, Match.MatchAlliance alliance, Match.MatchAlliance other) {;
        AllianceScoreData scoreInfo = (AllianceScoreData) alliance.breakdown;
        if (scoreInfo == null) Utils.log("Score info is null");
        if (data == null) Utils.log("Data is null");
        if (alliance == null) Utils.log("alliance is null (wat)");
        data.rankingPoints += (alliance.winner ? 2 : (other.winner ? 0 : 1));
        data.rankingPoints += scoreInfo.rankingPoints;
        data.scores += alliance.score;
        data.rotorPoints += scoreInfo.rotorPoints;
        data.climbPoints += (scoreInfo.climbs * 50);
        data.kPa += scoreInfo.kPA;
        data.matchesPlayed++;
    }

    @Override
    public List<SteamTeamData> rank(List<SteamTeamData> original) {
        List<SteamTeamData> d = new ArrayList<>(original);
        for (SteamTeamData t : original) {
            if (t.matchesPlayed == 0) d.remove(t);
        }
        d.sort((o1, o2) -> {
            int o1RP = Math.round(Utils.roundToPlace(o1.rankingPoints / o1.matchesPlayed, 2) * 100);
            int o2RP = Math.round(Utils.roundToPlace(o2.rankingPoints / o2.matchesPlayed, 2) * 100);
            int result = o2RP-o1RP;
            if (result == 0) result = o2.scores - o1.scores;
            //TODO: auto points tiebreaker
            if (result == 0) result = o2.rotorPoints - o1.rotorPoints;
            if (result == 0) result = o2.climbPoints - o1.climbPoints;
            if (result == 0) result = o2.kPa - o1.kPa;
            if (result == 0) result = Utils.randomInt(0, 1);
            return result;
        });
        return d;
    }
}
