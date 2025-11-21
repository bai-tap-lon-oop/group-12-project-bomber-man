package entity.animateentity;

import entity.animateentity.character.Bomber;
import graphics.Sprite;

public class Swamp extends AnimateEntity {
    private static final double SPEED_REDUCTION = 0.5; // Giảm tốc độ xuống 50%

    public Swamp(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        this.block = false;
    }

    @Override
    public void update() {
        checkPlayerOnSwamp();
    }

    private void checkPlayerOnSwamp() {
        Bomber bomber = map.getPlayer();
        if (bomber != null) {
            // Kiểm tra nếu bomber đang đứng trên swamp (va chạm bounding box)
            if (this.getBorder().intersects(bomber.getBorder())) {
                // Chỉ apply slow nếu chưa bị slow bởi swamp này
                if (bomber.getSlowSource() != this) {
                    bomber.applySlowEffect(SPEED_REDUCTION, this);
                }
            } else {
                // Nếu bomber rời khỏi swamp này, khôi phục tốc độ
                if (bomber.getSlowSource() == this) {
                    bomber.removeSlowEffect();
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
