package path;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;
import map.Map;
import variables.Variables.DIRECTION;

import java.util.ArrayList;
import java.util.Random;

public class RandomPath extends Path {

    public RandomPath(Map map, Bomber player, Enemy enemy) {
        super(map, player, enemy);
    }
    
    // Tạo đường đi cho enemy random nhưng hợp lệ
    public DIRECTION path() {
        // Check khi có va chạm hoặc ở trung tâm 1 ô thì đổi hướng
        if (enemy.isCollider() || enemy.isInATile()) {
            ArrayList<DIRECTION> canDirections = new ArrayList<>(); // Chứa các hướng thể đi
            for (int k = 0; k < 4; k++) {
                // Hướng có thể đi thì thêm vào list
                if (!enemy.checkTileCollider(intToDirection(k), false)) {
                    canDirections.add(intToDirection(k));
                }
            }
            // K có hướng nào đi được thì đi như cữ
            if (canDirections.size() == 0) {
                return enemy.getDirection();
            }
            // Chọn 1 hướng đi ngẫu nhiên có thể đi trong list
            int random = new Random().nextInt(canDirections.size());
            return canDirections.get(random);
        } else {
            return enemy.getDirection();
        }
    }
}
