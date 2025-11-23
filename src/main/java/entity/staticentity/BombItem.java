package entity.staticentity;

import graphics.Sprite;

public class BombItem extends Item{
    public BombItem(int x, int y, Sprite sprite) {
        super(x, y, sprite);
    }

    @Override
    public void update() {
        // Xử lý tăng bomb limit được làm trong Bomber.checkCollision()
        // Không cần xử lý gì ở đây
    }
}
