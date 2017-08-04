package ryan.game.games.overboard.robots;

import com.badlogic.gdx.physics.box2d.Body;
import ryan.game.Utils;
import ryan.game.autonomous.overboard.Auto254Over;
import ryan.game.bcnlib_pieces.Command;
import ryan.game.competition.RobotStats;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Robot;
import ryan.game.entity.parts.Intake;
import ryan.game.entity.parts.Part;
import ryan.game.games.Game;

public class OverRobotStats extends RobotStats {

    public boolean chestIntake = true;
    public int maxChestIntakeAtOnce = 1;
    public int maxChests = 1;
    public float chestIntakeStrength = 5.75f;
    public float chestIntakeTime = 500;

    public boolean cannonballIntake = false;

    public boolean detectWeightOnIntake = false;
    public float detectWeightIntakeTime = 750;

    public boolean detectWeightWithMechanism = false;
    public float  detectWeightMechanismTime = 750;

    public OverRobotStats() {
        super(Game.OVERBOARD);
        robotWidth = 0.8128f;
        intakeWidth = robotWidth*.75f;
        needsStateGenerator = true; //TODO: remove this once this class is properly treated as a stats default
    }

    @Override
    public Command getAutonomous(Robot r) {
        return new Auto254Over(r);
    }

    @Override
    public void addParts(float x, float y, Robot r) {
        float width = intakeWidth, height = robotHeight / 4;
        Body in = BodyFactory.getRectangleDynamic(x - (robotWidth/2), y + robotHeight * 1.25f, width, height, width*height);
        r.addPart(new Intake(width*2, height*2, in));

        //TODO: why do wheel rectangles turn turning robots into vortexes of death 70% of the time? why do they not the other 30%?
        for (int s=0;s<2;s++) {
            for (int i = 0; i < 3; i++) {

                float spawnX = x + (s == 0 ? robotWidth/4 : -(robotWidth));
                float spawnY = y + 0.1f + (robotHeight / 2) - (i * (robotHeight / 1.5f));
                width = .15f;
                height = robotHeight / 4 - 0.1f;

                Body b = new BodyFactory(spawnX, spawnY).setDensity(0.05f).setSensor(true).setShapeRectangle(width, height).setTypeDynamic().create();
                Part p = new Part("wheel", b);
                //p.collideWithSelf = true;
                r.addPart(p);
                //Utils.log("made a wheel rectangle");
            }
        }
    }
}
