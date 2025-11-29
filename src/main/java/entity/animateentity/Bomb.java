package entity.animateentity;

import entity.animateentity.character.Bomber;
import entity.staticentity.Grass;
import graphics.Sprite;
import sound.Sound;
import texture.FlameTexture;

import static variables.Variables.STATUS.*;

public class Bomb extends AnimateEntity {
    protected int timetoExplode = 120;
    public static int limit = 1;
    private boolean up = true;
    private boolean left = true;
    private boolean right = true;
    private boolean down = true;
    private int cnt = 0;
    private Bomber owner;

    public Bomb(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(NOTEXPLODEDYET, Sprite.BOMB);
        currentAnimate = animation.get(NOTEXPLODEDYET);
        block = false;
    }

    public void setOwner(Bomber owner) {this.owner = owner;}
    public Bomber getOwner() {return this.owner;}

    @Override
    public void update() {
        //Đếm ngược thơi gian bom nổ
        if (timetoExplode != 0) {
            updateAnimation();
            timetoExplode--;
        }
        // Khi bom nổ
        else {
            delete();
            Flame flm = FlameTexture.setFlame("be", this.tileX, this.tileY);
            map.getFlames().add(flm);
            // Duyệt vòng for để bomb nổ chạy từ vị trí 1 đến vị trí xa nhất bomb có thể nổ tới
            for(int i = 1; i <= flm.flameLength; i++) {
                // Lấy tọa độ trung tâm khi bom nổ
                int x = flm.getTileX();
                int y = flm.getTileY();
                int ii = i;
                // Xử lý nổ theo hướng down
                if(down) {
                    //Kiểm tra xem tại vị trí (x, y + ii) có bomb khác k
                    //Nếu có để bomb đó nổ ngay và không lan xuống nữa
                    map.getBombs().forEach(bomb -> {
                        if(bomb.getTileX() == x && bomb.getTileY() == (y + ii)) {
                            bomb.setTimetoExplode(0);
                            down = false;
                            if(bomb.up) cnt++;
                        }
                    });
                    //Kiểm tra xem tại vị trí (x, y + ii) nếu đang có flame của vụ nổ khác thì không tạo thêm bomb nữa
                    map.getFlames().forEach(flame -> {
                        if(flame.getTileX() == x && flame.getTileY() == (y + ii)) {
                            down = false;
                        }
                    });
                    //Nếu tile không phải grass xử lý interactWith với tile tương ứng khi nổ bomb
                    if(map.getTile(x, y + ii) instanceof Grass == false) {
                        flm.interactWith(map.getTile(x, y + i));
                        down = false;
                    }
                }
                //Xử lý tương tự với up, left, right
                if (up) {
                    map.getBombs().forEach(bomb -> {
                        if (bomb.getTileX() == x && bomb.getTileY() == (y - ii)) {
                            bomb.setTimetoExplode(0);
                            up = false;
                            if(bomb.down) {
                                cnt++;
                            }
                        }
                    });
                    map.getFlames().forEach(flame -> {
                        if (flame.getTileX() == x && flame.getTileY() == (y - ii)) {
                            up = false;
                        }
                    });
                    if (map.getTile(x, y - i) instanceof Grass == false) {
                        flm.interactWith(map.getTile(x, y - i));
                        up = false;
                    }
                }

                if (right) {
                    map.getBombs().forEach(bomb -> {
                        if (bomb.getTileX() == (x + ii) && bomb.getTileY() == (y)) {
                            bomb.setTimetoExplode(0);
                            right = false;
                            if(bomb.left) {
                                cnt++;
                            }
                        }
                    });
                    map.getFlames().forEach(flame -> {
                        if (flame.getTileX() == (x+ii) && flame.getTileY() == (y)) {
                            right = false;
                        }
                    });
                    if (map.getTile(x + i, y) instanceof Grass == false) {
                        flm.interactWith(map.getTile(x + i, y));
                        right = false;

                    }
                }
                if (left) {
                    map.getBombs().forEach(bomb -> {
                        if (bomb.getTileX() == (x - ii) && bomb.getTileY() == (y)) {
                            bomb.setTimetoExplode(0);
                            left = false;
                            if(bomb.right) {
                                cnt++;
                            }
                        }
                    });
                    map.getFlames().forEach(flame -> {
                        if (flame.getTileX() == (x-ii) && flame.getTileY() == (y)) {
                            left = false;
                        }
                    });
                    if (map.getTile(x - i, y) instanceof Grass == false) {
                        flm.interactWith(map.getTile(x - i, y));
                        left = false;
                    }
                }

                // Nếu là ngọn lửa cuối
                if (i == flm.flameLength) {
                    if (down) {
                        Flame vdl = FlameTexture.setFlame("vdl", x, y + i); //Flema hướng down cuối cùng
                        map.getFlames().add(vdl); // Thêm vào map
                    }
                    // Tương tự với các hướng up, left, right
                    if (up) {
                        Flame vtl = FlameTexture.setFlame("vtl", x, y - i);
                        map.getFlames().add(vtl);
                    }
                    if (left) {
                        Flame hll = FlameTexture.setFlame("hll", x - i, y);
                        map.getFlames().add(hll);
                    }
                    if (right) {
                        Flame hrl = FlameTexture.setFlame("hrl", x + i, y);
                        map.getFlames().add(hrl);
                    }
                }
                // Nếu không phải
                else {
                    if (down) {
                        Flame vd = FlameTexture.setFlame("v", x, y + i);
                        map.getFlames().add(vd);
                    }
                    if (up) {
                        Flame vt = FlameTexture.setFlame("v", x, y - i);
                        map.getFlames().add(vt);
                    }
                    if (left) {
                        Flame hl = FlameTexture.setFlame("h", x - i, y);
                        map.getFlames().add(hl);
                    }
                    if (right) {
                        Flame hr = FlameTexture.setFlame("h", x + i, y);
                        map.getFlames().add(hr);
                    }
                }
            }
            //Nếu bomb hiện tại nổ không kick hoạt bomb khác nổ thì phát âm thanh
            if(cnt == 0) {
                Sound.bomb_explosion.play();
            }
        }
    }

    public int getLimit() {return limit;}
    public void setLimit(int limit) {this.limit = limit;}

    public void setTimetoExplode(int timetoExplode) {
        this.timetoExplode = timetoExplode;
    }

    @Override
    public void delete() {
        this.remove();
    }

}
