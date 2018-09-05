package ryan.game.games.steamworks;

import ryan.game.games.RobotStatBuilder;
import ryan.game.games.RobotStatSlider;
import ryan.game.games.SliderTextChecker;
import ryan.game.games.steamworks.robots.SteamRobotStats;

import java.util.ArrayList;
import java.util.List;

public class SteamStatBuilder extends RobotStatBuilder {

    @Override
    public List<RobotStatSlider> createSliders() {
        List<RobotStatSlider> newSliders = new ArrayList<>();

        newSliders.add(new RobotStatSlider("Speed", 11, (slider, stats) ->  {
            stats.maxMPS = (8 + (14 * slider.getProgress())) / 3.28084f;
        }));

        newSliders.add(new RobotStatSlider("Intake Length", 6, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.intakeWidth = steam.robotWidth * (.4f + (.6f * val));
        }));

        newSliders.add(new RobotStatSlider("Gear Intake", 5, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.gearIntake = val != 0;
            steam.gearIntakeRate = 1000 - (700 * val);
            steam.gearIntakeStrength = 9f + (3 * val);
        }).setTextChecker(new SliderTextChecker() {
            @Override
            public String check(RobotStatSlider s) {
                if (s.getProgress() == 0) {
                    return "Cannot intake gears.";
                }
                return null;
            }
        }));

        newSliders.add(new RobotStatSlider("Fuel Intake", 5, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.fuelIntake = val != 0;
            steam.fuelIntakeRate = 1000 - (750 * val);
            steam.fuelIntakeStrength = 1f;//9f + (3 * val);
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "Cannot intake fuel.";
            }
            return null;
        }));

        newSliders.add(new RobotStatSlider("Fuel Capacity", 8, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.maxFuel = Math.round(0 + (50 * val));
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "Cannot hold fuel.";
            }
            return null;
        }));

        newSliders.add(new RobotStatSlider("Shooter Speed", 4, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.shooter = val != 0;
            steam.timePerShoot = 1000 - (850 * val);
        }));

        newSliders.add(new RobotStatSlider("Shooter Accuracy", 4, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.shootPowerVariance = 2.5f - (1.5f * val);
            steam.shootAngleVariance = 5 - (4 * val);
        }));

        newSliders.add(new RobotStatSlider("Climber", 9, (slider, stats) ->  {
            SteamRobotStats steam = (SteamRobotStats) stats;
            float val = slider.getProgress();
            steam.climber = val != 0;
            steam.climbSpeed = 8 - (6.5f * val);
        }).setTextChecker(s -> {
            if (s.getProgress() == 0) {
                return "Cannot climb.";
            }
            return null;
        }));

        return newSliders;
    }
}
