package ryan.game.controls;

import net.java.games.input.Component;

public class Button {

    int id;
    Component button;

    private static int buttons = 0;

    protected Button(Component c) {
        button = c;
        id = buttons++;
    }

    public boolean get() {
        return button.getPollData() == 1;
    }
}
