package entity.animateentity;

import entity.animateentity.character.Bomber;
import graphics.Sprite;

public class Swamp extends AnimateEntity {
    private static final double SPEED_REDUCTION = 0.5;

    public Swamp(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        this.block = false;
    }

    @Override
    public void update() {
        checkPlayerOnSwamp();
    }

    private void checkPlayerOnSwamp() {
        // Kiểm tra Player 1
        Bomber bomber = map.getPlayer();
        if (bomber != null) {
            // Kiểm tra nếu bomber đang đứng trên swamp (va chạm bounding box)
            if (this.getBorder().intersects(bomber.getBorder())) {
                if (bomber.getSlowSource() != this) {
                    bomber.applySlowEffect(SPEED_REDUCTION, this);
                }
            } else {
                if (bomber.getSlowSource() == this) {
                    bomber.removeSlowEffect();
                }
            }
        }
        
        // Kiểm tra Player 2
        Bomber bomber2 = map.getPlayer2();
        if (bomber2 != null) {
            if (this.getBorder().intersects(bomber2.getBorder())) {
                if (bomber2.getSlowSource() != this) {
                    bomber2.applySlowEffect(SPEED_REDUCTION, this);
                }
            } else {
                if (bomber2.getSlowSource() == this) {
                    bomber2.removeSlowEffect();
                }
            }
        }
    }

    @Override
    public void updateAnimation() {
        // Swamp không có animation
        image = sprite.getFxImage();
    }

    @Override
    public void updateDestroyAnimation() {}

    @Override
    public void delete() {}
}
