package input;

import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class Player2Input implements KeyInput {
    public static String lastPressedKey;

    public void initialization() {
        keyInput.put("LEFT", false);
        keyInput.put("RIGHT", false);
        keyInput.put("UP", false);
        keyInput.put("DOWN", false);
        keyInput.put("NUMPAD0", false);
    }

    @Override
    public DIRECTION handleKeyInput() {

        if (lastPressedKey == null) return NONE;

        switch (lastPressedKey) {
            case "UP": return UP;
            case "DOWN": return DOWN;
            case "LEFT": return LEFT;
            case "RIGHT": return RIGHT;
            case "NUMPAD0": return PLACEBOMB;
        }
        return NONE;
    }

    public static void lastPressed() {
        if (keyInput.getOrDefault("UP", false)) lastPressedKey = "UP";
        else if (keyInput.getOrDefault("LEFT", false)) lastPressedKey = "LEFT";
        else if (keyInput.getOrDefault("DOWN", false)) lastPressedKey = "DOWN";
        else if (keyInput.getOrDefault("RIGHT", false)) lastPressedKey = "RIGHT";
        else if (keyInput.getOrDefault("NUMPAD0", false)) lastPressedKey = "NUMPAD0";
        else lastPressedKey = null;
    }
}
