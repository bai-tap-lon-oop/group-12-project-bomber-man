package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.Entity;
import graphics.Sprite;
import map.Map;
import path.RandomPath;
import variables.Variables.DIRECTION;

import static graphics.Sprite.*;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.DIRECTION.RIGHT;
import static variables.Variables.HEIGHT;
import static variables.Variables.WIDTH;

public class Minvo extends Enemy {
    
    public Minvo(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(LEFT, MINVO_LEFT);
        animation.put(UP, MINVO_LEFT);
        animation.put(RIGHT, MINVO_RIGHT);
        animation.put(DOWN, MINVO_RIGHT);
        animation.put(DESTROYED, MINVO_DESTROYED);
        currentAnimate = animation.get(UP);
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5; // Di chuyển khoảng 5 frame trước khi đổi hướng
        this.life = 2;
    }

    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        if (!enemy.isCollider() && cntMove > 0) {
            cntMove--;
            return enemy.getDirection();
        }
        cntMove = defaultCntMove;
        RandomPath randomPath = new RandomPath(map, map.getPlayer(),this);
        return randomPath.path();
    }

    public void checkCollision() {
        isCollision = false;
        // Tạm dịch quái đến vị trí mới
        pixelX += this.velocityX;
        pixelY += this.velocityY;
        // Duyệt map
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                Entity entity = map.getTile(j,i); //Lấy vật tại vị trí (i,j)
                // Nếu vị trí (i,j) có block = true hoặc vật tại vị trí (x,y) va chạm với minvo
                if (entity.isBlock() && this.isCollider(entity)) {
                    isCollision = true; // Cập nhật có va chạm
                }
            }
        }
        // Stand là trạng thái của minvo là đứng yên hoặc có va chạm thì stand = true
        stand = (velocityX == 0 && velocityY == 0) || isCollision;
        // Trả minvo về vị trí cũ
        pixelX -= this.velocityX;
        pixelY -= this.velocityY;
    }

    @Override
    public void delete() {
        this.remove();
    }
}
