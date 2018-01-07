package ryan.game.games.steamworks;

import ryan.game.competition.RankData;

public class SteamTeamData extends RankData {
    int team;
    public float rankingPoints = 0;
    public int scores = 0;
    public int rotorPoints = 0;
    public int climbPoints = 0;
    public int kPa = 0;
    public float matchesPlayed = 0;


    public SteamTeamData(int t) {
        team = t;
    }

    @Override
    public int getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return team + " - " + (rankingPoints/matchesPlayed) + " RP";
    }
}