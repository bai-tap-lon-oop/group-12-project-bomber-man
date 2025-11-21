package entity.animateentity;

import entity.animateentity.character.Bomber;
import game.MainGame;
import graphics.Sprite;

public class SpikeTrap extends AnimateEntity {
    private int damageTimer = 0;
    private final int DAMAGE_TIMER = 180;
    private boolean isActive = false;

    public SpikeTrap(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        this.block = false;
        sprite = Sprite.spike_trap;
    }

    @Override
    public void update() {
        damageTimer++;
        if (damageTimer >= DAMAGE_TIMER) {
            isActive = true;
            damageTimer = 0;
        } else if (damageTimer > 60) isActive = false;
        if (isActive) checkAndDamageBomber();
        updateAnimation();
    }

    @Override
    public void updateAnimation() {
        long time = MainGame.time;
        if (isActive) {
            sprite = Sprite.movingSprite(Sprite.spike_trap_active, 2, time);
        } else {
            sprite = Sprite.spike_trap;
        }
        image = sprite.getFxImage();
    }

    private void checkAndDamageBomber() {
        Bomber bomber = map.getPlayer();
        if (bomber != null && bomber.getLife() > 0) {
            if (this.getBorder().intersects(bomber.getBorder())) {
                bomber.destroy();
            }
        }
    }

    @Override
    public void updateDestroyAnimation() {}

    @Override
    public void delete() {}

    public boolean isActive() {return isActive;}
}
