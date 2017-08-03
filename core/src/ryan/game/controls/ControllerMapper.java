package ryan.game.controls;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import ryan.game.Utils;

public class ControllerMapper extends ApplicationAdapter {

    Gamepad currentMap = null;

    @Override
    public void create () {
        super.create();
        Gamepads.init();
        Utils.log("Welcome to the Gamepad Mapper!\n[INFO] Follow the instructions to map your gamepad(s).");
    }

    public void tick() {
        if (currentMap == null) {
            for (Gamepad g : Gamepads.getGamepads()) {
                if (g.getConfig() == null) {
                    currentMap = g;
                    break;
                }
            }
            currentMap.mapping = true;
            currentMap.config = new GamepadConfig();
            currentMap.currentKey = currentMap.mapOrder[0];
            Utils.log("Starting to create mapping for: " + currentMap.getName());
        }
    }

    @Override
    public void render () {
        tick();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //TODO: eventually show a picture of the button/axis that needs to be pressed
    }
}
