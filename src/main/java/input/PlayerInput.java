package input;

import java.util.HashMap;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class PlayerInput implements KeyInput {
    public String lastPressedKey;
    private HashMap<String, Boolean> keyInput = new HashMap<>();

    public void setKeyState(String key, boolean pressed) {
        if (key.equals("W") || key.equals("A") || key.equals("S") || 
            key.equals("D") || key.equals("SPACE")) {
            keyInput.put(key, pressed);
            if (pressed) {
                lastPressedKey = key;
            } else {
                lastPressed();
            }
        }
    }

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

    private void lastPressed() {
        if (keyInput.getOrDefault("W", false)) lastPressedKey = "W";
        else if (keyInput.getOrDefault("A", false)) lastPressedKey = "A";
        else if (keyInput.getOrDefault("S", false)) lastPressedKey = "S";
        else if (keyInput.getOrDefault("D", false)) lastPressedKey = "D";
        else if (keyInput.getOrDefault("SPACE", false)) lastPressedKey = "SPACE";
        else lastPressedKey = null;
    }
}
