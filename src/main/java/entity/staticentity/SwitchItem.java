package entity.staticentity;

import graphics.Sprite;

public class SwitchItem extends Item {

    public SwitchItem(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        setBlock(false);
    }

    @Override
    public void update() {

    }
}
