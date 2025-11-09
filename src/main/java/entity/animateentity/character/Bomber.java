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

//        Gán animation để khi ấn a, w, s, d thì player di chuyển
        animation.put(LEFT, Sprite.PLAYER_LEFT);
        animation.put(RIGHT, Sprite.PLAYER_RIGHT);
        animation.put(UP, Sprite.PLAYER_UP);
        animation.put(DOWN, Sprite.PLAYER_DOWN);
        animation.put(DESTROYED, Sprite.PLAYER_DESTROYED);
        currentAnimate = animation.get(DOWN);

//        Khởi tạo input điều khiển
        this.keyInput = keyInput;
        this.keyInput.initialization();

//        Khởi tạo tốc độ player, mạng player
        this.defaultVel = 1;
        this.speed = 2;
        this.life = 3;
    }


//    Đặt bom và tạo sprite bom
    public void placeBombAt(int x, int y) {
        canPlace = true;
        int bombX = 0;
        int bombY = 0;

//        Xác định vị trí dt bomb
        int baseX = x / SCALED_SIZE;
        int baseY = y / SCALED_SIZE;
        int remX = x % SCALED_SIZE;
        int remY = y % SCALED_SIZE;
        if (remX > SCALED_SIZE/2) bombX = baseX + 1; else bombX = baseX;
        if (remY > SCALED_SIZE/2) bombY = baseY + 1; else bombY = baseY;

//        Kiểm tra xem vị trí đặt bomb có trùng với bomb khác hay không
        for (Bomb bomb: map.getBombs()) {
            if (bomb.getTileX() == bombX && bomb.getTileY() == bombY) {
                canPlace = false;
            }
        }

//        Kiểm tra vị trí đặt bomb có trùng với enemy không
        for (Enemy enemy: map.getEnemies()) {
            if(enemy.getTileX() == bombX && enemy.getTileY() == bombY) {
                canPlace = false;
            }
        }

//        Nếu vị trí đã oke thì đặt bomb = cách tạo ra dối tượng bomb trên mao và thêm âm thanh
        if (map.getTile(bombX, bombY) instanceof Grass && map.getBombs().size() < Bomb.limit && canPlace) {
            Bomb bomb = BombTexture.setBomb(bombX, bombY); // Tạo object bomb
            map.getBombs().add(bomb);
            Sound.place_bomb.play(); // Tạo âm thanh
        }
    }

    @Override
    public void checkCollision() {
        super.checkCollision();
//        Giảm dần thời gian bất tử sau khi được hồi sinh
        if (immortal > 0) {
            immortal--;
        }

//        Kiểm tra nếu va chạm với địch và hét thời gian bất tử thì die
        map.getEnemies().forEach(enemy -> {
            if (this.isCollider(enemy)) {
                if (immortal == 0) {
                    destroy();
                }
            }
        });

//        Khi đặt bom lúc đặt có thẻ đi xuyên qua, sau khi đã rời khỏi thì sẽ k đi xuyên qua bom được nữa
        map.getBombs().forEach(bomb -> {
            if (!this.isCollider(bomb)) {
                bomb.setBlock(true);
            }
        });

//        Khi nhặt item
        map.getItems().forEach(item -> {
            if (this.isCollider(item)) {
                Sound.get_item.play(); // Tạo tiếng khi nhận được item
                item.setActivated(true); // Đánh dấu item đã kích hoạt
                item.remove(); // Sau khi nhận thì xóa item

//                Nếu item là SpeedItem thì set lại speed
                if (item instanceof SpeedItem) {
                    setSpeed(SpeedItem.increasedSpeed);
                }

                item.delete();
            }
        });

//        Khi va chạm
        if (isCollision) {
//            Dò vị trí k bị kẹt
            for (int i = -8 - speed; i <= 8 + speed; i++) {
                switch (direction) {
                    case UP, DOWN -> pixelX += i;
                    case LEFT, RIGHT -> pixelY += i;
                }
                super.checkCollision(); // Check lại xem còn va chạm không
                if (!isCollision) break; // Nếu k còn thì break

                switch (direction) {
                    case UP, DOWN -> pixelX -= i;
                    case LEFT, RIGHT -> pixelY -= i;
                }
            }
        }
//        Cập nhật lại vị trí
        tileX = pixelX / SCALED_SIZE;
        tileY = pixelY / SCALED_SIZE;
    }

    @Override
    public void setDirection() {
        direction = keyInput.handleKeyInput();
        this.setVelocity(0, 0);
//        Di chuyển và đặt bomb, muốn di chuyển nhanh hơn đổi defaultVel thành speed là khi di chuyển sẽ nhanh hơn
        switch (direction) {
            case NONE -> this.setVelocity(0, 0);
            case LEFT -> this.setVelocity(-defaultVel, 0);
            case RIGHT -> this.setVelocity(defaultVel, 0);
            case UP -> this.setVelocity(0, -defaultVel);
            case DOWN -> this.setVelocity(0, defaultVel);
            case PLACEBOMB -> placeBombAt(pixelX, pixelY);
        }
//        Nếu direction khac NONE va PLACEBOMB thì updateAnimate và tạo tiếng đi bộ
        if (direction != NONE && direction != PLACEBOMB) {
            currentAnimate = animation.get(direction);
            updateAnimation();
            Sound.walk.play();
        }
    }

//    Khi bị die
    @Override
    public void delete() {
        this.life--; // Trừ 1 mạng
        timeRevival = 7; // Thời gian hồi sinh
//        Set lai map
        immortal = 100; // Muốn sau khi hồi sinh bất tử lâu hơn thì tăng cái này
        map.setRevival(true);
        setPosition(SCALED_SIZE, SCALED_SIZE);
        destroyed = false;
        direction = NONE;
        setSprite(Sprite.PLAYER_DOWN[0]);
        Sound.bomber_die.play(); // Phát âm thanh die
    }

    public int getTimeRevival() {
        return timeRevival;
    }
}