package input;

import java.util.HashMap;
import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class MenuInput implements KeyInput {
    private HashMap<String, Boolean> keyInput = new HashMap<>();

    @Override
    public void initialization() {
        keyInput.put("W", false);
        keyInput.put("S", false);
        keyInput.put("ENTER", false);
    }

    @Override
    public DIRECTION handleKeyInput() {
        Set<String> keySet = keyInput.keySet();
        for (String code : keySet) {
            if (keyInput.get(code)) {
                switch (code) {
                    case ("W"):
                        return UP;
                    case ("S"):
                        return DOWN;
                    case ("ENTER"):
                        return DESTROYED;
                }
            }
        }
        return NONE;
    }

    public void setKeyPressed(String key, boolean pressed) {
        if (keyInput.containsKey(key)) {
            keyInput.put(key, pressed);
        }
    }
}
