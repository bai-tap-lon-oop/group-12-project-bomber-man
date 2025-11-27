package entity.animateentity.character;

import entity.animateentity.Bomb;
import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Grass;
import entity.staticentity.SpeedItem;
import entity.staticentity.CoinItem;
import entity.staticentity.SwitchItem;
import game.MainGame;
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
    private int bombLimit = 1; // Số lượng bomb mỗi player có thể đặt

    // biến riêng cho Swamp
    private boolean isSlowed = false;
    private Object slowSource; // Track nguồn gây slow (swamp nào)
    private int moveFrameCounter = 0; // Đếm frame để skip movement

    public Bomber(int x, int y, Sprite sprite, KeyInput keyInput) {
        super(x, y, sprite);
        // Gán sprite cho các hướng
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
        if (x % SCALED_SIZE < y % SCALED_SIZE) {
            bombX = x / SCALED_SIZE;
            if (y % SCALED_SIZE > SCALED_SIZE / 2) {
                bombY = y / SCALED_SIZE + 1;
            } else {
                bombY = y / SCALED_SIZE;
            }
        } else {
            bombY = y / SCALED_SIZE;
            if (x % SCALED_SIZE > SCALED_SIZE / 2) {
                bombX = x / SCALED_SIZE + 1;
            } else {
                bombX = x / SCALED_SIZE;
            }
        }
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

        // Đếm số bomb của player này đang có trên map
        int myBombCount = 0;
        for (Bomb bomb: map.getBombs()) {
            if (bomb.getOwner() == this) {
                myBombCount++;
            }
        }

        // Nếu vị trí đã oke thì đặt bomb = cách tạo ra đối tượng bomb trên map và thêm âm thanh
        if (map.getTile(bombX, bombY) instanceof Grass && myBombCount < bombLimit && canPlace) {
            Bomb bomb = BombTexture.setBomb(bombX, bombY); // Tạo object bomb
            bomb.setOwner(this); // Đặt owner cho bomb
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
        //Va chạm với Quái (Enemy)
        map.getEnemies().forEach(enemy -> {
            if (this.isCollider(enemy)) {
                if (immortal == 0) {
                    destroy();
                }
            }
        });
        // Chỉ set block cho bomb mà player này đã đặt và đã ra khỏi bomb đó
        map.getBombs().forEach(bomb -> {
            if (bomb.getOwner() == this && !this.isCollider(bomb)) {
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
                else if (item instanceof SwitchItem) {
                    map.getWalls().forEach(wall -> {
                        wall.setBlock(false);
                        wall.setSprite(Sprite.grass);
                        map.getEnemies().forEach(enemy -> {
                            enemy.destroy();
                        });
                    });
                }
                item.delete();// Xóa vật phẩm khỏi map
            }
        });
        //Khi Bomber đi vào góc tường, sẽ tự động "trượt" thay vì bị kẹt cứng.
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
        direction = keyInput.handleKeyInput(); // Lấy hướng di chuyển nhận vào từ bàn phím

        // Xử lý đặt bomb riêng, không ảnh hưởng đến di chuyển
        if (direction == PLACEBOMB) {
            placeBombAt(pixelX, pixelY);
            direction = NONE; // Reset về NONE sau khi đặt bomb
        }

        this.setVelocity(0, 0);

        switch (direction) {
            case NONE -> this.setVelocity(0, 0);
            // Set tốc độ di chuyển treo trục tọa độ Oxy
            case LEFT -> this.setVelocity(-defaultVel, 0);
            case RIGHT -> this.setVelocity(defaultVel, 0);
            case UP -> this.setVelocity(0, -defaultVel);
            case DOWN -> this.setVelocity(0, defaultVel);
            default -> this.setVelocity(0, 0);
        }

        // Áp dụng slow effect: chỉ di chuyển mỗi 4 frame thay vì mỗi frame
        if (isSlowed && direction != NONE && direction != PLACEBOMB) {
            moveFrameCounter++;
            if (moveFrameCounter % 4 != 0) {
                this.setVelocity(0, 0); // Skip frame này, không di chuyển
            }
        } else {
            moveFrameCounter = 0;
        }
        // Xử lý nếu di chuyển và phát âm thanh
        if (direction != NONE && direction != PLACEBOMB) {
            // Lấy frame tương ứng với direction sau đó tạo hiệu ứng di chuyển
            currentAnimate = animation.get(direction);
            updateAnimation();
            Sound.walk.play(); // Phát âm thanh di chuyển
        }
    }

    @Override
    public void delete() {
        // Trừ life của player1 (dùng chung life pool)
        map.getPlayer().life--;

        timeRevival = 7;
        immortal = 100;
        map.setRevival(true);
        setPosition(SCALED_SIZE, SCALED_SIZE);//Reset về vị trí (1,1)
        destroyed = false;
        direction = NONE;//Dừng di chuyển
        setSprite(Sprite.PLAYER_DOWN[0]);//Đặt lại ảnh
        Sound.bomber_die.play();
    }

    public int getTimeRevival() {return timeRevival;}

    // Swamp slow effect methods
    public void applySlowEffect(double multiplier, Object source) {
        if (!isSlowed) {
            isSlowed = true;
            slowSource = source;
        }
    }

    public void removeSlowEffect() {
        if (isSlowed) {
            isSlowed = false;
            slowSource = null;
        }
    }

    public boolean isSlowed() {return isSlowed;}
    public Object getSlowSource() {return slowSource;}
}