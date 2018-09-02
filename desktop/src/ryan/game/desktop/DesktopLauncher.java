package ryan.game.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.controls.ControllerMapper;
import ryan.game.screens.GameScreen;

public class DesktopLauncher {

    static String[] random = {"Water Game Confirmed", "Driver Practice Confirmed", "Driver Skill Simulator", "pOrK liFt", "100%* Real Physics",
	"#FuelMatters", "Revenge of the Tank Drive", "The Peg Strikes Back", "Raiders of the Lost Gears", "Destination: Despacito", "Press A to Chute Door",
	"Mission Moon :)", "TSIMFD"};

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720); //720p
		config.setTitle("The FRC Simulator - " + random[Utils.randomInt(0, random.length-1)]);
		//config.width = 1100;
		//config.height = 630;
		//config.title = "RobotDriveSimGDX - " + random[Utils.randomInt(0, random.length-1)];
		boolean mapping = false;
		for (String s : arg) {
			System.out.println("Arg: " + s);
			if (s.equalsIgnoreCase("no_music")) {
				GameScreen.PLAY_MUSIC = false;
			}
			if (s.equalsIgnoreCase("software_gl")) {
				System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
			}
			if (s.equalsIgnoreCase("schedule")) {
				GameScreen.MAKE_SCHEDULE = true;
			}
			if (s.equalsIgnoreCase("custom_teams")) {
				GameScreen.CUSTOM_TEAMS = true;
			}
			if (s.startsWith("rounds=")) {
				GameScreen.SCHEDULE_ROUNDS = Integer.parseInt(s.replace("rounds=", ""));
			}
			if (s.startsWith("teams=")) {
				GameScreen.RANDOM_TEAMS = Integer.parseInt(s.replace("teams=", ""));
			}
			if (s.startsWith("event_name=")) {
                GameScreen.EVENT_NAME = s.replace("event_name=", "");
			}
			if (s.startsWith("extra_robots=")) {
				GameScreen.EXTRA_ROBOTS = Integer.parseInt(s.replace("extra_robots=", ""));
			}
			if (s.startsWith("event_key=")) {
				GameScreen.EVENT_KEY = s.replace("event_key=", "");
			}
			if (s.equalsIgnoreCase("debug")) {
				Main.DEBUG_RENDER = true;
			}
			if (s.equalsIgnoreCase("manipulators")) {
				GameScreen.MANIPULATORS = true;
			}
			if (s.equalsIgnoreCase("controller_mapping")) {
				mapping = true;
			}
		}
		new Lwjgl3Application(mapping ? new ControllerMapper() : new Main(), config);
	}
}
