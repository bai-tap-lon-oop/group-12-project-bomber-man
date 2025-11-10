package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import java.util.Random;

public class SpeedDistancePath extends Path {
    public SpeedDistancePath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }

    public DIRECTION path() {
        if (Distance(enemy.getTileY(), enemy.getTileX(), player.getTileY(), player.getTileX(), false) <= 5) {
            enemy.setCntMove(5);
            return new DistancePath(map, player, enemy).path();
        } else {
            // TO DO
            return new RandomPath(map, player, enemy).path();
        }
    }
}
