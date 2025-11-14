package entity.animateentity;

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

    public Bomb(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(NOTEXPLODEDYET, Sprite.BOMB);
        currentAnimate = animation.get(NOTEXPLODEDYET);
        block = false;
    }

    @Override
    public void update() {
        if (timetoExplode != 0) {
            updateAnimation();
            timetoExplode--;
        } // else { TO DO }
            if(cnt == 0) {
                Sound.bomb_explosion.play();
            }

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setTimetoExplode(int timetoExplode) {
        this.timetoExplode = timetoExplode;
    }

    @Override
    public void delete() {
        this.remove();
    }

}
