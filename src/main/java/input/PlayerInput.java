package input;

import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class PlayerInput implements KeyInput {

    public void initialization() {
        keyInput.put("A", false);
        keyInput.put("D", false);
        keyInput.put("W", false);
        keyInput.put("S", false);
        // TO DO
    }

    @Override
    public DIRECTION handleKeyInput() {
        Set<String> keySet = keyInput.keySet();
        for (String code : keySet) {
            if (keyInput.get(code)) {
                switch (code) {
                    case ("W"):
                        return UP;
                    case ("D"):
                        return RIGHT;
                    case ("S"):
                        return DOWN;
                    case ("A"):
                        return LEFT;
                    // TO DO
                }
            }
        }
        return NONE;
    }
}
