package entity.staticentity;

import graphics.Sprite;

public class CoinItem extends Item {

    public CoinItem(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        setBlock(false);
    }

    @Override
    public void update() {

    }
}
