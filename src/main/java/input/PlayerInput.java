package input;

import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class PlayerInput implements KeyInput {

    public static String lastPressedKey;

    public void initialization() {
        keyInput.put("A", false);
        keyInput.put("D", false);
        keyInput.put("W", false);
        keyInput.put("S", false);
        keyInput.put("SPACE", false);
    }

    @Override
    public DIRECTION handleKeyInput() {

        boolean up = keyInput.getOrDefault("W", false);
        boolean down = keyInput.getOrDefault("S", false);
        boolean left = keyInput.getOrDefault("A", false);
        boolean right = keyInput.getOrDefault("D", false);
        boolean bomb = keyInput.getOrDefault("SPACE", false);

        if (bomb) return PLACEBOMB;

        else if (up && left) {
            if ("A".equals(lastPressedKey)) {
                return LEFT;     // A được nhấn sau --> đi trái
            } else {
                return NONE;     // W được nhấn sau --> đứng yên
            }
        }

        else if (down && right) {
            if ("D".equals(lastPressedKey)) {
                return RIGHT;     // D được nhấn sau --> đi phải
            } else {
                return NONE;     // S được nhấn sau --> đứng yên
            }
        }

        else if (left)  return LEFT;
        else if (right) return RIGHT;
        else if (up)    return UP;
        else if (down)  return DOWN;

        return NONE;
    }

}
