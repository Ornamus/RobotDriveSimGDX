package ryan.game.games;

import ryan.game.competition.RobotStats;

import java.util.List;

public abstract class RobotStatBuilder {

    private List<RobotStatSlider> sliders;

    public RobotStatBuilder() {
        sliders = createSliders();
    }

    public abstract List<RobotStatSlider> createSliders();

    public abstract int getMaxPoints();

    public List<RobotStatSlider> getSliders() {
        return sliders;
    }

    public void applyStats(RobotStats stats) {
        for (RobotStatSlider s : sliders) {
            s.affectStats(stats);
        }
    }
}
