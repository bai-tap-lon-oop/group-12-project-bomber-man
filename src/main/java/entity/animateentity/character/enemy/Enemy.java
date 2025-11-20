package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.Character;
import graphics.Sprite;
import map.Map;
import java.util.Random; // [THÊM]

import static variables.Variables.*;

public abstract class Enemy extends Character {
    protected int cntMove;
    protected int changeSpeed;
    protected int defaultCntMove;
    protected int defaultChangeSpeed;

    protected int aiCooldown = 0;
    // [TĂNG LÊN] 1 giây mới suy nghĩ lại 1 lần (60 frames)
    // Quái sẽ ngu hơn vì phản ứng chậm, nhưng game siêu mượt.
    protected final int MAX_AI_COOLDOWN = 60;

    private Random random = new Random();

    public Enemy(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        // [QUAN TRỌNG] Random thời gian chờ ban đầu
        // Để các con quái KHÔNG tính đường cùng một lúc
        aiCooldown = random.nextInt(MAX_AI_COOLDOWN);
    }

    public void setCntMove(int cntMove) {
        this.cntMove = cntMove;
    }

    public int getCntMove() {
        return this.cntMove;
    }

    public int getChangeSpeed() {
        return this.changeSpeed;
    }

    public void setChangeSpeed(int changeSpeed) {
        this.changeSpeed = changeSpeed;
    }

    public abstract DIRECTION path(Map map, Bomber player, Enemy enemy);

    @Override
    public void setDirection() {
        // Logic Cooldown
        if (aiCooldown > 0) {
            aiCooldown--;
        } else {
            // Hết thời gian chờ thì mới tính đường
            direction = path(map, map.getPlayer(), this);
            aiCooldown = MAX_AI_COOLDOWN;
        }

        switch (direction) {
            case UP -> this.setVelocity(0, -defaultVel);
            case DOWN -> this.setVelocity(0, defaultVel);
            case LEFT -> this.setVelocity(-defaultVel, 0);
            case RIGHT -> this.setVelocity(defaultVel, 0);
            default -> this.setVelocity(0,0);
        }
        currentAnimate = animation.get(direction);
    }
}