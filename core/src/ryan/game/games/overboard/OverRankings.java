package ryan.game.games.overboard;

import ryan.game.competition.Match;
import ryan.game.competition.RankData;
import ryan.game.competition.Rankings;
import ryan.game.competition.Team;
import java.util.List;

public class OverRankings extends Rankings {

    //TODO: implement

    @Override
    public RankData generate(Team team) {
        return new OverRankData(team.number);
    }

    @Override
    public void update(RankData data, Match.MatchAlliance alliance, Match.MatchAlliance other) {

    }

    @Override
    public List rank(List data) {
        return data;
    }
}
