package input;

import static variables.Variables.DIRECTION;

public interface KeyInput {

    public abstract void initialization();

    public abstract DIRECTION handleKeyInput();
}
