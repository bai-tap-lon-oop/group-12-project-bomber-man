package input;

import java.util.HashMap;
import java.util.Set;
import variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;

public class Player2Input implements KeyInput {
    private HashMap<String, Boolean> keyInput = new HashMap<>();

    public void initialization() {
        keyInput.put("LEFT", false);
        keyInput.put("RIGHT", false);
        keyInput.put("UP", false);
        keyInput.put("DOWN", false);
        keyInput.put("SPACE", false);
    }

    @Override
    public DIRECTION handleKeyInput() {
        Set<String> keySet = keyInput.keySet();
        for (String code : keySet) {
            if (keyInput.get(code)) {
                // Set key điều khiển hướng di chuyển của player 2
                if (code.equals("UP")) return UP;
                else if(code.equals("DOWN")) return DOWN;
                else if(code.equals("RIGHT")) return RIGHT;
                else if(code.equals("LEFT")) return LEFT;
                else if(code.equals("SPACE")) return PLACEBOMB;
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
