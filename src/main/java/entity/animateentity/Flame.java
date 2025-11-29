package entity.animateentity;

import entity.Entity;
import entity.staticentity.*;
import graphics.Sprite;
import variables.Variables;

import static variables.Variables.FLAME_SHAPE.*;

public class Flame extends AnimateEntity {
    public static int flameLength = 1;
    protected int flameShape = 0;

    public Flame(int x, int y, Sprite sprite, Variables.FLAME_SHAPE fs) {
        super(x, y, sprite);
        animation.put(BOMB_EXPLODED, Sprite.BOMB_EXPLODED);
        animation.put(VERTICAL, Sprite.EXPLOSION_VERTICAL);
        animation.put(HORIZONTAL, Sprite.EXPLOSION_HORIZONTAL);
        animation.put(HORIZONTAL_LEFT_LAST, Sprite.EXPLOSION_HORIZONTAL_LEFT_LAST);
        animation.put(HORIZONTAL_RIGHT_LAST, Sprite.EXPLOSION_HORIZONTAL_RIGHT_LAST);
        animation.put(VERTICAL_TOP_LAST, Sprite.EXPLOSION_VERTICAL_TOP_LAST);
        animation.put(VERTICAL_DOWN_LAST, Sprite.EXPLOSION_VERTICAL_DOWN_LAST);
        currentAnimate = animation.get(fs);
    }

    @Override
    public void update() {
        checkCollison();
        updateAnimation();
        updateDestroyAnimation();
    }

    @Override
    public void updateDestroyAnimation() {
        checkCollison();
        if (timeDestroy == 0) {
            delete();
        } else {
            timeDestroy--;
            updateAnimation();
        }
    }

    // Xử lý va chạm với các thực thể khác
    public void interactWith(Entity entity) {
        if (entity instanceof Brick) {
            ((Brick) entity).destroyed = true;
        } else if (entity instanceof Item) {
            entity.setBlock(false);
            if (entity instanceof SpeedItem) {
                entity.setSprite(Sprite.powerup_speed);
            }
            else if (entity instanceof BombItem) {
                entity.setSprite(Sprite.powerup_bombs);
            }
            else if (entity instanceof FlameItem) {
                entity.setSprite(Sprite.powerup_flames);
            }
        }
    }

    public void checkCollison() {
        map.getEnemies().forEach(enemy -> {
            if (this.isCollider(enemy)) {
                enemy.destroy();
            }
        });

        // Kiểm tra Player 1
        if (this.isCollider(map.getPlayer()) && map.getPlayer().getImmortal() == 0 && !map.getPlayer().isDestroyed()) {
            map.getPlayer().destroy();
        }

        // Kiểm tra Player 2
        if (map.getPlayer2() != null && this.isCollider(map.getPlayer2()) && map.getPlayer2().getImmortal() == 0 && !map.getPlayer2().isDestroyed()) {
            map.getPlayer2().destroy();
        }
    }

    @Override
    public void delete() {
        this.remove();
    }
}
