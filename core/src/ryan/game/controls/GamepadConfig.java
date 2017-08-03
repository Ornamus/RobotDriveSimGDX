package ryan.game.controls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.Utils;

public class GamepadConfig {

    protected int xAxis = 94, yAxis = 95,
            xAxis2 = 96, yAxis2 = 97,
            zAxis = 98;

    protected int dPad = 99;

    private int[] mappings;

    public GamepadConfig() {
        mappings = new int[100];
    }

    public void setMapping(int buttonID, int controllerButton) {
        if (buttonID == 94) xAxis = controllerButton;
        else if (buttonID == 95) yAxis = controllerButton;
        else if (buttonID == 96) xAxis2 = controllerButton;
        else if (buttonID == 97) yAxis2 = controllerButton;
        else if (buttonID == 98) zAxis = controllerButton;
        else if (buttonID == 99) dPad = controllerButton;
        else mappings[buttonID] = controllerButton;
    }

    public int getMapping(int buttonID) {
        return mappings[buttonID];
    }

    public void save(String name) {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Utils.writeFile("core/assets/controller_configs/"+name+".json", g.toJson(this));
    }
}
