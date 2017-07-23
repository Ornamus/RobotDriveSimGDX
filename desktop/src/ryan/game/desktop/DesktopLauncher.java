package ryan.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ryan.game.Main;
import ryan.game.Utils;

public class DesktopLauncher {

    static String[] random = {"Water Game Confirmed", "Driver Practice Confirmed", "Driver Skill Simulator", "pOrK liFt", "100%* Real Physics",
	"Phillip Approved", "Fuel Matters", "Revenge of the Tank Drive", "The Peg Strikes Back", "Raiders of the Lost Gears"};

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1100;
		config.height = 630;
		config.title = "RobotDriveSimGDX - " + random[Utils.randomInt(0, random.length-1)];
		for (String s : arg) {
			System.out.println("Arg: " + s);
			if (s.equalsIgnoreCase("no_music")) {
				Main.playMusic = false;
			}
			if (s.equalsIgnoreCase("software_gl")) {
				System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
			}
			if (s.equalsIgnoreCase("schedule")) {
				Main.makeSchedule = true;
			}
			if (s.equalsIgnoreCase("custom_teams")) {
				Main.customTeams = true;
			}
			if (s.startsWith("rounds=")) {
				Main.scheduleRounds = Integer.parseInt(s.replace("rounds=", ""));
			}
			if (s.startsWith("event_name=")) {
                Main.eventName = s.replace("event_name=", "");
			}
			if (s.startsWith("event_key=")) {
				Main.eventKey = s.replace("event_key=", "");
			}
			if (s.equalsIgnoreCase("debug_view")) {
				Main.DEBUG_RENDER = true;
			}
		}
		new LwjglApplication(new Main(), config);
	}
}
