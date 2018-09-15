package ryan.game.controls;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import ryan.game.Utils;
import ryan.game.entity.Robot;

public class Gamepad {

    public static final int ONE = 0, TWO = 1, THREE = 2, FOUR = 3;
    public static final int BUMPER_LEFT = 4, BUMPER_RIGHT = 5;
    public static final int START = 6, SELECT = 7;
    public static final int JOY_LEFT = 8, JOY_RIGHT = 9;
    public static final int TRIGGER_LEFT = 10, TRIGGER_RIGHT = 11;
    public static final int DPAD_UP = 12, DPAD_LEFT = 13, DPAD_DOWN = 14, DPAD_RIGHT = 15;

    protected static final GamepadConfigPart[] mapOrder = {
            new GamepadConfigPart(ONE, "QuadButton Top"), new GamepadConfigPart(TWO, "QuadButton Left"), new GamepadConfigPart(THREE, "QuadButton Bottom"),
            new GamepadConfigPart(FOUR, "QuadButton Right"), new GamepadConfigPart(BUMPER_LEFT, "Left Bumper"), new GamepadConfigPart(BUMPER_RIGHT, "Right Bumper"), new GamepadConfigPart(START, "Start (left)"),
            new GamepadConfigPart(SELECT, "Select (right)"), new GamepadConfigPart(JOY_LEFT, "Left Joystick Button"), new GamepadConfigPart(JOY_RIGHT, "Right Joystick Button"),
            new GamepadConfigPart(TRIGGER_LEFT, "Left Trigger Button", true), new GamepadConfigPart(TRIGGER_RIGHT, "Right Trigger Button", true),
            new GamepadConfigPart(DPAD_UP, "DPad Up"), new GamepadConfigPart(DPAD_LEFT, "DPad Left"), new GamepadConfigPart(DPAD_DOWN, "DPad Down"), new GamepadConfigPart(DPAD_RIGHT, "DPad Right"),
            new GamepadConfigPart(94, "Joystick X Axis"), new GamepadConfigPart(95, "Joystick Y Axis"), new GamepadConfigPart(96, "Joystick X Axis 2", true),
            new GamepadConfigPart(97, "Joystick Y Axis 2", true), new GamepadConfigPart(98, "Left Trigger Axis", true), new GamepadConfigPart(99, "Right Trigger Axis", true)
    };


    public final int id;
    public final String name;
    protected Controller c;

    public Robot r = null;

    protected GamepadConfig config = null;

    private boolean reverseSticks = false;

    protected boolean noConfig = false;
    protected boolean mapping = false;
    protected int mapIndex = 0;

    private static int gamepads = 0;

    protected Gamepad(Controller c) {
        this.c = c;
        name = c.getName();
        config = Utils.fromJSON("core/assets/controller_configs/" + getSimpleName() + ".json", GamepadConfig.class);
        if (config == null) { //TODO: check if in mapping mode or not
            Utils.log("NULL GAMEPAD");
            noConfig = true;
            config = new GamepadConfig();
        }

        id = gamepads++;
    }

    public void doMapInit() {
        Utils.log("Please Press: " + mapOrder[mapIndex].display);
        if (mapOrder[mapIndex].optional) {
            Utils.log("(This field is optional. If your controller does not have it, press 'X' on the keyboard.)");
        }
    }

    boolean mapEmpty = true;

    public boolean isMapEmpty() {
        return mapEmpty;
    }

    public void updateMap(int index) {
        mapEmpty = false;
        int key = mapOrder[mapIndex].key;
        Utils.log("[DEBUG] Mapping " + key + " to " + index);
        config.setMapping(key, index);
        mapIndex++;
        if (mapIndex < mapOrder.length) {
            doMapInit();
        } else {
            mapping = false;
            config.save(getSimpleName());
            Utils.log("Mapping complete!");
        }
    }

    public float getX() {
        if (c == null) return 0;
        if (hasSecondJoystick() && reverseSticks) return c.getAxis(config.xAxis2);
        return c.getAxis(config.xAxis);
    }

    public float getY() {
        float reverse = config.reverseY ? -1 : 1;
        if (c == null) return 0;
        if (hasSecondJoystick() && reverseSticks) return c.getAxis(config.yAxis2) * reverse;
        return c.getAxis(config.yAxis) * reverse;
    }

    public float getX2() {
        if (c == null) return 0;
        if (config.xAxis2 != -1) {
            if (reverseSticks) return c.getAxis(config.xAxis);
            return c.getAxis(config.xAxis2);
        }
        return 0;
    }

    public float getY2() {
        if (c == null) return 0;
        if (config.yAxis2 != -1) {
            if (reverseSticks) return c.getAxis(config.yAxis);
            return c.getAxis(config.yAxis2);
        }
        return 0;
    }

    public float getLeftTrigger() {
        if (c == null) return 0;
        if (config.zAxis1 != -1) return c.getAxis(config.zAxis1);
        return getButton(TRIGGER_LEFT) ? 1 : 0;
    }

    public float getRightTrigger() {
        if (c == null) return 0;
        if (config.zAxis2 != -1) return c.getAxis(config.zAxis2);
        return getButton(TRIGGER_RIGHT) ? 1 : 0;
    }

    public PovDirection getDPad() {
        if (c == null) return PovDirection.center;
        return c.getPov(0);
        /*
        if (getButton(DPAD_UP)) {
            if (getButton(DPAD_LEFT)) return PovDirection.northWest;
            else if (getButton(DPAD_RIGHT)) return PovDirection.northEast;
            else return PovDirection.north;
        } else if (getButton(DPAD_DOWN)) {
            if (getButton(DPAD_LEFT)) return PovDirection.southWest;
            else if (getButton(DPAD_RIGHT)) return PovDirection.southEast;
            else return PovDirection.south;
        } else if (getButton(DPAD_LEFT)) return PovDirection.west;
        else if (getButton(DPAD_RIGHT)) return PovDirection.east;
        else return PovDirection.center;*/
    }

    public boolean isLeftTriggerPressed() {
        if (c == null) return false;
        return getLeftTrigger() > 0.1;
    }

    public boolean isRightTriggerPressed() {
        if (c == null) return false;
        return getRightTrigger() > 0.1;
    }

    public boolean hasZAxis() {
        return config.zAxis1 != -1 || config.zAxis2 != -1;
    }

    public boolean hasSecondJoystick() {
        return config.xAxis2 != -1 && config.yAxis2 != -1;
    }

    public boolean isSticksReversed() {
        return reverseSticks;
    }

    public GamepadConfig getConfig() {
        return config;
    }

    public void setReverseSticks(boolean rev) {
        if (hasSecondJoystick()) reverseSticks = rev;
    }

    public boolean getButton(int id) {
        if (c != null && id > -1) return c.getButton(config.getMapping(id));
        return false;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        String s = getName();
        s = s.replaceAll("[^A-Za-z0-9]", "");
        return s;
    }
}
