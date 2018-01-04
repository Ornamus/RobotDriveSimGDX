package ryan.game.games.power;

import ryan.game.competition.RankData;

public class PowerRankData extends RankData {

    private final int team;

    public PowerRankData(int i) {
        team = i;
    }

    @Override
    public int getTeam() {
        return team;
    }
}
