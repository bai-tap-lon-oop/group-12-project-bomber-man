package entity.animateentity.character;

import entity.animateentity.Bomb;
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Grass;
import entity.staticentity.SpeedItem;
import graphics.Sprite;
import input.KeyInput;
import sound.Sound;
import texture.BombTexture;

import static graphics.Sprite.*;

import static variables.Variables.DIRECTION.*;

public class Bomber extends Character {
    public KeyInput keyInput;
    public boolean canPlace = true;

    private int timeRevival;

    public Bomber(int x, int y, Sprite sprite, KeyInput keyInput) {
        super(x, y, sprite);
        animation.put(LEFT, Sprite.PLAYER_LEFT);
        animation.put(RIGHT, Sprite.PLAYER_RIGHT);
        animation.put(UP, Sprite.PLAYER_UP);
        animation.put(DOWN, Sprite.PLAYER_DOWN);
        animation.put(DESTROYED, Sprite.PLAYER_DESTROYED);
        currentAnimate = animation.get(DOWN);
        this.keyInput = keyInput;
        this.keyInput.initialization();
        this.defaultVel = 1;
        this.speed = 2;
        this.life = 3;
    }

    public void placeBombAt(int x, int y) {
        canPlace = true;
        int bombX = 0;
        int bombY = 0;
        // Xác định vị trí đặt bomb
        // Cách đặt bomb là đặt bomb tại ô tile gần nhất
        int baseX = x / SCALED_SIZE;
        int baseY = y / SCALED_SIZE;

        int remX = x % SCALED_SIZE;
        int remY = y % SCALED_SIZE;

        bombX = (remX >= SCALED_SIZE / 2) ? baseX + 1 : baseX;
        bombY = (remY >= SCALED_SIZE / 2) ? baseY + 1 : baseY;

        // Kiểm tra xem vị trí đặt bomb có trùng với bomb khác hay không
        for (Bomb bomb: map.getBombs()) {
            if (bomb.getTileX() == bombX && bomb.getTileY() == bombY) {
                canPlace = false;
            }
        }

        // Kiểm tra vị trí đặt bomb có trùng với enemy không
        for (Enemy enemy: map.getEnemies()) {
            if(enemy.getTileX() == bombX && enemy.getTileY() == bombY) {
                canPlace = false;
            }
        }
        // Nếu vị trí đã oke thì đặt bomb = cách tạo ra dối tượng bomb trên mao và thêm âm thanh
        if (map.getTile(bombX, bombY) instanceof Grass && map.getBombs().size() < Bomb.limit && canPlace) {
            Bomb bomb = BombTexture.setBomb(bombX, bombY); // Tạo object bomb
            map.getBombs().add(bomb);
            Sound.place_bomb.play(); // Tạo âm thanh
        }
    }

    @Override
    public void checkCollision() {
        super.checkCollision();
        if (immortal > 0) {
            immortal--;
        }
        map.getEnemies().forEach(enemy -> {
            if (this.isCollider(enemy)) {
                if (immortal == 0) {
                    destroy();
                }
            }
        });
        map.getBombs().forEach(bomb -> {
            if (!this.isCollider(bomb)) {
                bomb.setBlock(true);
            }
        });
        map.getItems().forEach(item -> {
            if (this.isCollider(item)) {
                Sound.get_item.play();
                item.setActivated(true);
                item.remove();
                if (item instanceof SpeedItem) {
                    setSpeed(SpeedItem.increasedSpeed);
                }
                item.delete();
            }
        });
        if (isCollision) {
            for (int i = -8 - speed; i <= 8 + speed; i++) {
                switch (direction) {
                    case UP, DOWN -> pixelX += i;
                    case LEFT, RIGHT -> pixelY += i;
                }
                super.checkCollision();
                if (!isCollision) {
                    break;
                }
                switch (direction) {
                    case UP, DOWN -> pixelX -= i;
                    case LEFT, RIGHT -> pixelY -= i;
                }
            }
        }
        tileX = pixelX / SCALED_SIZE;
        tileY = pixelY / SCALED_SIZE;
    }


    @Override
    public void setDirection() {
        direction = keyInput.handleKeyInput();
        this.setVelocity(0, 0);
        switch (direction) {
            case NONE -> this.setVelocity(0, 0);
            case LEFT -> this.setVelocity(-defaultVel, 0);
            case RIGHT -> this.setVelocity(defaultVel, 0);
            case UP -> this.setVelocity(0, -defaultVel);
            case DOWN -> this.setVelocity(0, defaultVel);
            case PLACEBOMB -> placeBombAt(pixelX, pixelY);
        }
        if (direction != NONE && direction != PLACEBOMB) {
            currentAnimate = animation.get(direction);
            updateAnimation();
            Sound.walk.play();
        }
    }

    @Override
    public void delete() {
        this.life--;
        timeRevival = 7;
        immortal = 100;
        map.setRevival(true);
        setPosition(SCALED_SIZE, SCALED_SIZE);
        destroyed = false;
        direction = NONE;
        setSprite(Sprite.PLAYER_DOWN[0]);
        Sound.bomber_die.play();
    }

    public int getTimeRevival() {
        return timeRevival;
    }
}