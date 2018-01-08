package ryan.game.autonomous.powerup;

import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.bcnlib_pieces.Motor;
import ryan.game.bcnlib_pieces.PIDController;
import ryan.game.entity.Robot;
import ryan.game.games.Game;
import ryan.game.games.power.PowerMetadata;

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
        String s = Main.getInstance().gameField.getGameString(robot.blue ? Game.ALLIANCE.BLUE : Game.ALLIANCE.RED);
        if (s.charAt(0) == 'L') {
            Utils.log("GO LEFT");
        } else {
            Utils.log("GO RIGHT");
        }
        try {
            boolean left = s.charAt(0) == 'L';
            meta.setIntaking(false);

            resetEncs();
            robot.setMotors(0.5f, 0.5f);
            while (encAverage() < inches(12) && Game.isAutonomous()) {
                Thread.sleep(5);
            }
            robot.setMotors(0,0);
            Thread.sleep(220);

            double angle = left ? 360-30 : 30;
            rotatePID.setTarget(angle);
            rotatePID.enable();
            while (!rotatePID.isDone() && Game.isAutonomous()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(5);
            }
            rotatePID.disable();
            robot.setMotors(0,0);
            Thread.sleep(100);

            resetEncs();
            robot.setMotors(1, 1);
            while (encAverage() < inches(184) && Game.isAutonomous()) {
                Thread.sleep(5);
            }

            robot.setMotors(0,0);
            rotatePID.setTarget(0);
            rotatePID.enable();
            while (!rotatePID.isDone() && Game.isAutonomous()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(5);
            }
            rotatePID.disable();

            resetEncs();
            robot.setMotors(0.7f, 0.7f);
            while (encAverage() < inches(24) && Game.isAutonomous()) {
                Thread.sleep(5);
            }
            robot.setMotors(0,0);
            Thread.sleep(200);

            meta.ejectChest(robot, false);
            Thread.sleep(200);

            resetEncs();
            robot.setMotors(-0.7f, -0.7f);
            while (Math.abs(encAverage()) < inches(12) && Game.isAutonomous()) {
                Thread.sleep(5);
            }

            angle = left ? 90 : 270;
            rotatePID.setTarget(angle);
            rotatePID.enable();
            while (!rotatePID.isDone() && Game.isAutonomous()) {
                robot.setMotors(pidOutput.getPower(), -pidOutput.getPower());
                Thread.sleep(5);
            }
            rotatePID.disable();

            meta.setIntaking(true);
            robot.setMotors(0.5f,0.5f);
            Thread.sleep(1000);
            meta.setIntaking(false);

            resetEncs();
            robot.setMotors(-0.7f, -0.7f);
            while (Math.abs(encAverage()) < inches(20) && Game.isAutonomous()) {
                Thread.sleep(5);
            }

            if (left) {
                robot.setMotors(-0.5f, 0.5f);
            } else {
                robot.setMotors(0.5f, -0.5f);
            }
            while (Math.abs((left ? 360-10 : 30)-robot.getGyro().getForPID()) > 5) {
                Thread.sleep(5);
            }
            robot.setMotors(-1,-1);
            Thread.sleep(700);
            robot.setMotors(0,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float inches(float inches) {
        return inches * 0.0254f;
    }

    public void resetEncs() {
        robot.getLeftEncoder().reset();
        robot.getRightEncoder().reset();
    }

    public float encAverage() {
        double avg = (robot.getRightEncoder().getForPID() + robot.getLeftEncoder().getForPID())/2000.0;
        return (float) avg;
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return true;
    }
}
