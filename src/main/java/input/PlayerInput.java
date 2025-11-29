package input;

import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class PlayerInput implements KeyInput {
    public static String lastPressedKey;

    @Override
    public void initialization() {
        keyInput.put("A", false);
        keyInput.put("D", false);
        keyInput.put("W", false);
        keyInput.put("S", false);
        keyInput.put("P", false);
        keyInput.put("SPACE", false);
        keyInput.put("ESCAPE", false);
    }

    @Override
    public DIRECTION handleKeyInput() {

        if (lastPressedKey == null) return NONE;

        switch (lastPressedKey) {
            case "W": return UP;
            case "S": return DOWN;
            case "A": return LEFT;
            case "D": return RIGHT;
            case "SPACE": return PLACEBOMB;
        }
        return NONE;
    }

    public static void lastPressed() {
        if (keyInput.getOrDefault("W", false)) lastPressedKey = "W";
        else if (keyInput.getOrDefault("A", false)) lastPressedKey = "A";
        else if (keyInput.getOrDefault("S", false)) lastPressedKey = "S";
        else if (keyInput.getOrDefault("D", false)) lastPressedKey = "D";
        else if (keyInput.getOrDefault("SPACE", false)) lastPressedKey = "SPACE";
        else lastPressedKey = null;
    }
}
