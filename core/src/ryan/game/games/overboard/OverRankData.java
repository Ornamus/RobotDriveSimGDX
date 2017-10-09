package ryan.game.games.overboard;

import ryan.game.competition.RankData;

public class OverRankData extends RankData {

    private final int team;

    public OverRankData(int i) {
        team = i;
    }

    @Override
    public int getTeam() {
        return team;
    }
}
