package input;

import java.util.HashMap;
import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class Player2Input implements KeyInput {
    private HashMap<String, Boolean> keyInput = new HashMap<>();
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

//    public static void setKeyPressed(String key, boolean pressed) {
//        if (keyInput.containsKey(key)) {
//            keyInput.put(key, pressed);
//        }
//    }

    public static void updateLastPressedKeyFromHeldKeys() {
        if (KeyInput.keyInput.getOrDefault("UP", false)) lastPressedKey = "UP";
        else if (KeyInput.keyInput.getOrDefault("LEFT", false)) lastPressedKey = "LEFT";
        else if (KeyInput.keyInput.getOrDefault("DOWN", false)) lastPressedKey = "DOWN";
        else if (KeyInput.keyInput.getOrDefault("RIGHT", false)) lastPressedKey = "RIGHT";
        else if (KeyInput.keyInput.getOrDefault("NUMPAD0", false)) lastPressedKey = "NUMPAD0";
        else lastPressedKey = null;
    }

}
