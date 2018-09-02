package ryan.game.autonomous.powerup;

import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.power.PowerMetadata;
import ryan.game.screens.GameScreen;

public class Bacon_Basic extends Command {

    Motor pidOutput;
    PIDController rotatePID;

    public Bacon_Basic(Robot r) {
        super(r);
        pidOutput = new Motor();
        rotatePID = new PIDController(pidOutput, r.getGyro(), 0.01, 0.0008 * 3/*2*/, 0.09 * 1.5, 0.15, 1).setRotational(true);
        rotatePID.setFinishedTolerance(1);
    }

    @Override
    public void onInit() {
        robot.getGyro().reset();
        PowerMetadata meta = (PowerMetadata) robot.metadata;
        String s = GameScreen.self.field.getGameString(robot.blue ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED);
        if (s.charAt(0) == 'L') {
            Utils.log("GO LEFT");
        } else {
            Utils.log("GO RIGHT");
        }
        try {
            boolean left = s.charAt(0) == 'L';
            meta.setIntaking(false);

            driveDistance(8, 0.5);
            sleep(200);


            double angle = left ? 360-55 : 30;
            driveDistanceAtAngle(left ? 54 : 96, 1, angle);


            driveDistanceAtAngle(left ? 110 : 120-60, 1, 0); //1 at 0 angle

            //eject cube 1
            meta.ejectChest(robot, false);

            //scored cube, going for #2

            final double backFromSwitch = 17.5 + 2+8     + 6  - 6; //TODO: CHANGE THE 2 IF YOU CHANGE THE ABOVE

            driveDistanceAtAngle(backFromSwitch, -0.6, 0);

            angle = left ? 75 : 360-75;
            turnToAngle(angle);


            meta.setIntaking(true);

            double forward = 48+3-6;
            driveDistanceAtAngle(forward, 0.5, angle);;

            driveDistanceAtAngle(forward-13, -0.6, angle);

            meta.setIntaking(false);


            angle = 0;//left ? 8 : 360-8;
            turnToAngle(angle);


            driveDistanceAtAngle(backFromSwitch+15, 1, angle);

            //Eject cube 2
            meta.ejectChest(robot, false);

            //backup for cube 3
            final double backFromSwitch2 = 10 + 3;

            driveDistanceAtAngle(backFromSwitch2, -0.45, 0);

            //turn for cube 3
            angle = left ? 75 : 360-75;

            turnToAngle(angle);

            //get cube 3
            meta.setIntaking(true);

            forward = 32 - 3;
            driveDistanceAtAngle(forward, 0.6, angle); //.5

            driveDistanceAtAngle(forward-2, -0.65, angle);

            meta.setIntaking(false);


            angle = 0;//left ? 5 : 360-5;
            turnToAngle(angle);

            driveDistanceAtAngle(backFromSwitch2+5   +2, 1, angle); //TODO: too much?

            //Eject cube 3
            meta.ejectChest(robot, false);


            driveDistanceAtAngle(24, -1, 0);

            robot.setMotors(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float inches(float inches) {
        return (inches * 0.0254f)* 2f;
    }

    public void resetEncs() {
        robot.getLeftEncoder().reset();
        robot.getRightEncoder().reset();
    }

    public float encAverage() {
        double avg = (robot.getRightEncoder().getForPID() + robot.getLeftEncoder().getForPID())/2000.0;
        return (float) avg;
    }

    public void turnToAngle(double angle) {
        turnToAngle(angle, 5); //4
    }

    public void turnToAngle(double angle, double deadzone) {
        if (true) {
            rotatePID.setFinishedTolerance(deadzone);
            rotatePID.setTarget(angle);
            rotatePID.enable();
            while (!rotatePID.isDone()) {
                robot.setMotors((float)rotatePID.getMotorPower(), (float)-rotatePID.getMotorPower());
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                }
            }
            rotatePID.disable();
        }
    }

    public void sleep(long i) {
        try {
            Thread.sleep(i);
        } catch (Exception e) {}
    }

    public void driveDistance(double inches, double speed) {
        driveDistanceAtAngle(inches, speed, robot.getGyro().getForPID());
    }

    public void driveDistanceAtAngle(double inches, double speed, double angle) {
        driveAtAngle(inches, 0, speed, angle);
    }

    public void driveTimeAtAngle(long milliseconds, double speed, double angle) {
        driveAtAngle(0, milliseconds, speed, angle);
    }

    public void driveAtAngle(double inches, long milliseconds, double speed, double angle) {
        if (true) {
            resetEncs();
            rotatePID.setTarget(angle);
            rotatePID.enable();
            long startTime = System.currentTimeMillis();
            boolean keepGoing = true;
            while (keepGoing) {
                double pidOut = rotatePID.getMotorPower() * 1;
                double left = speed + pidOut;
                double right = speed - pidOut;

                double max = maxDouble(Math.abs(left), Math.abs(right));
                left /= max;
                right /= max;

                left *= Math.abs(speed);
                right *= Math.abs(speed);

                robot.setMotors((float)left, (float)right);

                if (inches == 0) {
                    keepGoing = System.currentTimeMillis() - startTime <= milliseconds;
                } else {
                    keepGoing = Math.abs(encAverage()) < inches((float)inches);
                }

                if (keepGoing) {
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                    }
                }
            }
            rotatePID.disable();
            robot.setMotors(0, 0);
        }
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return true;
    }

    /**
     * Get the absolute value of the largest number passed of an arbitrary number of doubles
     *
     * @param nums Doubles to search through
     * @return The absolute value of the largest number in nums
     */
    public static double maxDouble(double... nums) {
        double currMax = Math.abs(nums[0]);

        for (double i : nums) {
            currMax = Math.abs(i) > currMax ? Math.abs(i) : currMax;
        }

        return currMax;
    }
}
