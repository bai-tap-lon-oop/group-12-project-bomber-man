package path;

import entity.animateentity.Bomb;
import entity.Entity;
import entity.staticentity.Wall;
import map.Map;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;

import java.util.LinkedList;
import java.util.Queue;

import static variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.dx;
import static variables.Variables.dy;
import static variables.Variables.INF;

public abstract class Path {
    protected Map map;
    protected Bomber player;
    protected Enemy enemy;

    private class Vertex {
        int x;
        int y;
        int value;

        Vertex(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }

    public Path(Map map, Bomber player, Enemy enemy) {
        this.map = map;
        this.player = player;
        this.enemy = enemy;
    }

    // [SỬA] Sử dụng kích thước động từ Map thay vì biến tĩnh HEIGHT/WIDTH
    private boolean isValid(int x, int y) {
        return (x >= 0 && x < map.getHeight() && y >= 0 && y < map.getWidth());
    }

    public int Distance(int x1, int y1, int x2, int y2, boolean dodge) {
        // Lấy kích thước thực tế của map hiện tại
        int currentHeight = map.getHeight();
        int currentWidth = map.getWidth();

        // Kiểm tra tọa độ đích có hợp lệ không trước khi gọi getTile để tránh lỗi
        if (!isValid(x2, y2) || !isValid(x1, y1)) {
            return INF;
        }

        // Lưu ý: Hàm getTile(x, y) trong Map nhận (col, row).
        // Trong file Path này, x đang được dùng làm row, y làm col (theo logic mảng 2D [x][y]).
        // Cần chú ý thứ tự tham số truyền vào map.getTile().

        if (map.getTile(y2, x2).isBlock()) {
            return INF;
        }
        if (map.getTile(y1, x1).isBlock()) {
            if (dodge) {
                if (map.getTile(y1, x1) instanceof Wall) {
                    return INF;
                }
            } else {
                return INF;
            }
        }

        // [SỬA] Khởi tạo mảng theo kích thước động
        int[][] statusTiles = new int[currentHeight][currentWidth];
        int[][] distanceTiles = new int[currentHeight][currentWidth];

        // [SỬA] Vòng lặp theo kích thước động
        for (int i = 0; i < currentHeight; i++) {
            for (int j = 0; j < currentWidth; j++) {
                distanceTiles[i][j] = INF;
                // map.getTile(col, row) -> map.getTile(j, i)
                Entity tile = map.getTile(j, i);

                // Bảo vệ thêm 1 lớp nữa nếu tile null (dù Map đã fix nhưng cẩn thận không thừa)
                if (tile == null) {
                    statusTiles[i][j] = 0; // Coi như đi được nếu lỗi
                    continue;
                }

                if (tile.isBlock()) {
                    statusTiles[i][j] = 1;
                    if (dodge && !(tile instanceof Wall)) {
                        statusTiles[i][j] = 0;
                    }
                } else {
                    statusTiles[i][j] = 0;
                }
            }
        }

        for (Bomb bomb : map.getBombs()) {
            // Đảm bảo bomb nằm trong map mới đánh dấu
            if (isValid(bomb.getTileY(), bomb.getTileX())) {
                statusTiles[bomb.getTileY()][bomb.getTileX()] = 1;
            }
        }

        Queue<Vertex> pq = new LinkedList<>();
        pq.add(new Vertex(x1, y1, 0));
        distanceTiles[x1][y1] = 0;

        while (!pq.isEmpty()) {
            Vertex cur = pq.poll();
            for (int k = 0; k < 4; k++) {
                int _x = cur.x + dx[k];
                int _y = cur.y + dy[k];

                // isValid đã được sửa ở trên để dùng map.getHeight/Width
                if (isValid(_x, _y) && statusTiles[_x][_y] == 0 && distanceTiles[_x][_y] == INF) {
                    distanceTiles[_x][_y] = cur.value + 1;
                    pq.add(new Vertex(_x, _y, cur.value + 1));
                }
            }
        }
        return distanceTiles[x2][y2];
    }

    public DIRECTION intToDirection(int x) {
        switch (x) {
            case 0:
                return UP;
            case 1:
                return DOWN;
            case 2:
                return LEFT;
            case 3:
                return RIGHT;
            default:
                return NONE;
        }
    }

    public abstract DIRECTION path();
}