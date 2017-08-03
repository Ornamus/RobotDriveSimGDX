package ryan.game.controls;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    boolean loggedDone = false;

    public void tick() {
        if (currentMap == null) {
            for (Gamepad g : Gamepads.getGamepads()) {
                if (g.noConfig) {
                    currentMap = g;
                    break;
                }
            }
            if (currentMap == null) {
                if (!loggedDone) {
                    Utils.log("All controllers mapped! You can close this now.");
                    loggedDone = true;
                }
            } else {
                Utils.log("Now Mapping: " + currentMap.getName());
                currentMap.mapping = true;
                currentMap.config = new GamepadConfig();
                currentMap.doMapInit();
            }
        } else {
            if (currentMap.mapping) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.X) && Gamepad.mapOrder[currentMap.mapIndex].optional) {
                    Utils.log("Skipping...");
                    currentMap.updateMap(-1);
                }
            } else {
                currentMap.noConfig = false;
                currentMap = null;
            }
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
