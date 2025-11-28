package input;

import java.util.HashMap;
import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class PlayerInput implements KeyInput {
    private HashMap<String, Boolean> keyInput = new HashMap<>();
    public static String lastPressedKey;

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

    public void setKeyPressed(String key, boolean pressed) {
        if (keyInput.containsKey(key)) {
            keyInput.put(key, pressed);
        }
    }

    public void updateLastPressedKeyFromHeldKeys() {
        if (KeyInput.keyInput.getOrDefault("W", false)) lastPressedKey = "W";
        else if (KeyInput.keyInput.getOrDefault("A", false)) lastPressedKey = "A";
        else if (KeyInput.keyInput.getOrDefault("S", false)) lastPressedKey = "S";
        else if (KeyInput.keyInput.getOrDefault("D", false)) lastPressedKey = "D";
        else if (KeyInput.keyInput.getOrDefault("SPACE", false)) lastPressedKey = "SPACE";
        else lastPressedKey = null;
    }

}
