package ryan.game.games.destination;

import ryan.game.games.RobotStatBuilder;
import ryan.game.games.RobotStatSlider;
import ryan.game.games.SliderTextChecker;
import ryan.game.games.steamworks.robots.SteamRobotStats;

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

        newSliders.add(new RobotStatSlider("Intake Length", 6, (slider, stats) ->  {
            DestinationRobotStats steam = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            steam.intakeWidth = steam.robotWidth * (.4f + (.6f * val));
        }));

        newSliders.add(new RobotStatSlider("Panel Floor", 5, (slider, stats) ->  {
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

        newSliders.add(new RobotStatSlider("Panel HP", 1, (slider, stats) -> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.panelIntake = val != 0;
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "No HP panels.";
            }
            return null;
        }));

        cargoSlider = new RobotStatSlider("Cargo Intake", 5, (slider, stats) ->  {
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

        newSliders.add(new RobotStatSlider("Cargo Intake on Back", 1, (slider, stats)-> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.differentiateBetweenIntakes = val != 0;
        }).setTextChecker(s -> {
            if (s.getProgress() > 0 && cargoSlider.getProgress() == 0) {
                return "CARGO CONFUSION";
            }
            return null;
        }));

        newSliders.add(new RobotStatSlider("Elevator (all or nothin)", 3, (slider, stats)-> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
            dest.elevator = val == 1;
        }).setTextChecker(s -> {
            if (s.getProgress() > 0 && s.getProgress() < 1) {
                return "SHORT ELEVATOR";
            }
            return null;
        }));

        newSliders.add(new RobotStatSlider("Climber", 10, (slider, stats)-> {
            DestinationRobotStats dest = (DestinationRobotStats) stats;
            float val = slider.getProgress();
        }).setTextChecker(s->{
            if (s.getProgress() == 0) {
                return "Hab LVL 1";
            }
            if (s.getProgress() < .34) {
                return "Hab LVL 2";
            }
            return "Hab LVL 3";
        }));


        return newSliders;
    }
}
