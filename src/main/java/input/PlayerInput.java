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
        keyInput.put("SPACE", false);
    }

    @Override
    public DIRECTION handleKeyInput() {
        Set<String> keySet = keyInput.keySet();
        for (String code : keySet) {
            if (keyInput.get(code)) {
                // Set key điều khiển hướng di chuyển của nhân vật
                if (code.equals("W")) return UP;
                else if(code.equals("S")) return DOWN;
                else if(code.equals("D")) return RIGHT;
                else if(code.equals("A")) return LEFT;
                else if(code.equals("SPACE")) return PLACEBOMB;
            }
        }
        return NONE;
    }
}
