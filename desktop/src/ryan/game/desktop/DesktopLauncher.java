package ryan.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ryan.game.Main;
import ryan.game.Utils;

public class DesktopLauncher {

    static String[] random = {"Water Game Confirmed", "Driver Practice Confirmed", "Driver Skill Simulator", "pOrK liFt", "100%* Real Physics",
	"Phillip Approved", "Fuel Is Still Useless", "Revenge of the Tank Drive", "The Peg Strikes Back", "Raiders of the Lost Gears"};

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1100;
		config.height = 630;
		config.title = "Robot Drive Sim GDX - " + random[Utils.randomInt(0, random.length-1)];
		for (String s : arg) {
			if (s.equalsIgnoreCase("no_music")) {
				Main.playMusic = false;
			}
		}
		new LwjglApplication(new Main(), config);
	}
}
