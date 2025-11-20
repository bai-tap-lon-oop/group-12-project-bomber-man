package entity.animateentity.character.enemy;

import entity.animateentity.character.Bomber;
import entity.Entity;
import entity.staticentity.Wall;
import graphics.Sprite;
import map.Map;
import path.DodgePath;
import variables.Variables.DIRECTION;

import static graphics.Sprite.*;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.DIRECTION.RIGHT;
import static variables.Variables.HEIGHT;
import static variables.Variables.WIDTH;

public class Kondoria extends Enemy {
    public Kondoria(int x, int y, Sprite sprite) {
        super(x, y, sprite);
        animation.put(LEFT, KONDORIA_LEFT);
        animation.put(UP, KONDORIA_LEFT);
        animation.put(RIGHT, KONDORIA_RIGHT);
        animation.put(DOWN, KONDORIA_RIGHT);
        animation.put(DESTROYED, KONDORIA_DESTROYED);
        currentAnimate = animation.get(UP);
        this.direction = UP;
        this.defaultVel = 1;
        this.speed = 1;
        this.defaultCntMove = 5;
        this.life = 1;
    }
    @Override
    public DIRECTION path(Map map, Bomber player, Enemy enemy) {
        DodgePath dodgePath = new DodgePath(map, map.getPlayer(),this);
        return dodgePath.path();
    }

    @Override
    public void checkCollision() {
        isCollision = false;
        // TO DO
    }

    @Override
    public void delete() {
        this.remove();
    }
}
