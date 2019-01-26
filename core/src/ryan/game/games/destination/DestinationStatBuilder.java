package ryan.game.games.destination;

import ryan.game.games.RobotStatBuilder;
import ryan.game.games.RobotStatSlider;
import java.util.ArrayList;
import java.util.List;

public class DestinationStatBuilder extends RobotStatBuilder {

    RobotStatSlider cargoSlider;

    @Override
    public List<RobotStatSlider> createSliders() {
        List<RobotStatSlider> newSliders = new ArrayList<>();

        newSliders.add(new RobotStatSlider("Speed", 10, (slider, stats) ->  {
            stats.maxMPS = (8 + (19 * slider.getProgress())) / 3.28084f;
        }));

        newSliders.add(new RobotStatSlider("Intake Length", 6, (slider, stats) -> {
            DestinationRobotStats steam = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            steam.intakeWidth = steam.robotWidth * (.4f + (.6f * val));
        }));

        newSliders.add(new RobotStatSlider("Panel Floor", 5, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.panelFloor = val != 0;
            dest.panelIntakeRate = 1000 - (700 * val);
            dest.panelIntakeStrength = 9f + (3 * val);
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "No floor panels.";
            }
            return null;
        }));

        /*newSliders.add(new RobotStatSlider("Panel HP", 2, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.panelIntake = val != 0;
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "No HP panels.";
            }
            return null;
        }).setAllOrNothing(true));*/

        cargoSlider = new RobotStatSlider("Cargo Intake", 5, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.cargoIntake = val != 0;
            dest.cargoIntakeRate = 1000 - (700 * val);
            dest.cargoIntakeStrength = 9f + (3 * val);
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "No cargo.";
            }
            return null;
        });
        newSliders.add(cargoSlider);

        newSliders.add(new RobotStatSlider("Put Cargo Intake on Back", 1, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.differentiateBetweenIntakes = val != 0;
        }).setTextChecker(s -> {
            if (s.getProgress() > 0 && cargoSlider.getProgress() == 0) {
                return "CARGO CONFUSION";
            }
            return null;
        }));

        newSliders.add(new RobotStatSlider("Elevator", 5, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.elevator = val == 1;
        }).setAllOrNothing(true));

        newSliders.add(new RobotStatSlider("Climber", 10, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getVal();
            if (val == 0) {
                dest.hab_level = 1;
                dest.habLevel1Speed = 0.5f;
            } else {
                dest.habLevel1Speed = 0.5f;
                float cappedVal = val - 1 > 4 ? 4 : val - 1;
                dest.hab_level = 2;
                dest.habLevel2Speed  = 20 - (19.5f * ((cappedVal)/4f));

                if (val >= 6) {
                    dest.hab_level = 3;
                    dest.habLevel3Speed  = 30 - (28.5f * ((val-6)/4f));
                }
            }
        }).setTextChecker(s->{
            if (s.getVal() == 0) return "Hab LVL 1";
            if (s.getVal() < 6) return "Hab LVL 2";
            return "Hab LVL 3";
        }));

        return newSliders;
    }

    @Override
    public int getMaxPoints() {
        return 30;
    }
}
