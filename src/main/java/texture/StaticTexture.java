package texture;

import entity.staticentity.*;
import graphics.Sprite;

public class StaticTexture {
    public static StaticEntity setStatic(char c, int i, int j) {
        switch (c) {
            case 's':
                return new SpeedItem(j, i, Sprite.BRICK[0]);
            case 'f':
                return new FlameItem(j, i, Sprite.BRICK[0]);
            case 'b':
                return new BombItem(j ,i, Sprite.BRICK[0]);
            case 'x':
                return new Portal(j, i, Sprite.BRICK[0]);
            case 'y':
                return new CoinItem(j, i, Sprite.coin);
            case '#':
                return new Wall(j, i, Sprite.wall);
            case 'z':
                return new SwitchItem(j, i, Sprite.key);
            default:
                return new Grass(j, i, Sprite.grass);
        }
    }
}
