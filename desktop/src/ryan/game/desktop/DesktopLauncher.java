package ryan.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ryan.game.Main;
import ryan.game.Utils;

public class DesktopLauncher {

    static String[] random = {"Water Game Confirmed", "Driver Practice Confirmed", "Driver Skill Simulator", "pOrK liFt", "100%* real physics"};

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1100;
		config.height = 630;
		config.title = "Robot Drive Sim GDX - " + random[Utils.randomInt(0, random.length-1)];
		new LwjglApplication(new Main(), config);
	}
}
