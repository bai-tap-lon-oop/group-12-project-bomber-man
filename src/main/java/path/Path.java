package path;

import entity.animateentity.Bomb;
import entity.Entity;
import entity.staticentity.Wall;
import map.Map;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Enemy;

import static variables.Variables.DIRECTION;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.dx;
import static variables.Variables.dy;
import static variables.Variables.INF;

public abstract class Path {
    protected Map map;
    protected Bomber player;
    protected Enemy enemy;

    // [SIÊU TỐI ƯU] Dùng mảng 1 chiều làm hàng đợi (Queue) thay vì Object
    // Giúp loại bỏ hoàn toàn việc dọn rác bộ nhớ (Garbage Collection)
    private int[] qX;
    private int[] qY;
    private int[][] dist;
    private boolean[][] visited;

    public Path(Map map, Bomber player, Enemy enemy) {
        this.map = map;
        this.player = player;
        this.enemy = enemy;
        initArrays();
    }

    private void initArrays() {
        int h = Math.max(1, map.getHeight());
        int w = Math.max(1, map.getWidth());
        // Queue tối đa bằng kích thước map
        qX = new int[h * w + 1];
        qY = new int[h * w + 1];
        dist = new int[h][w];
        visited = new boolean[h][w];
    }

    private boolean isValid(int x, int y) {
        return (x >= 0 && x < map.getHeight() && y >= 0 && y < map.getWidth());
    }

    public int Distance(int x1, int y1, int x2, int y2, boolean dodge) {
        // 1. Kiểm tra nhanh khoảng cách (Manhattan)
        // Nếu xa quá 6 ô thì nghỉ khỏe, khỏi tính -> Tăng FPS cực mạnh
        if (Math.abs(x1 - x2) + Math.abs(y1 - y2) > 6) return INF;

        int h = map.getHeight();
        int w = map.getWidth();

        // Resize nếu cần
        if (dist.length != h || dist[0].length != w) initArrays();

        if (!isValid(x2, y2) || !isValid(x1, y1)) return INF;
        if (map.getTile(y2, x2).isBlock()) return INF;

        // 2. Reset mảng (Nhanh hơn tạo mới)
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                dist[i][j] = INF;
                visited[i][j] = false;
            }
        }

        // Đánh dấu vật cản
        // (Lưu ý: Ta tích hợp kiểm tra vật cản ngay trong lúc duyệt BFS để đỡ phải loop 2 lần)

        // 3. BFS dùng mảng (Array Queue)
        int head = 0;
        int tail = 0;

        // Push điểm bắt đầu
        qX[tail] = x1;
        qY[tail] = y1;
        tail++;
        dist[x1][y1] = 0;
        visited[x1][y1] = true;

        while (head < tail) {
            int ux = qX[head];
            int uy = qY[head];
            head++;

            // Tìm thấy đích
            if (ux == x2 && uy == y2) return dist[ux][uy];

            // Nếu đi quá sâu (quá 10 bước) thì dừng để tiết kiệm CPU
            if (dist[ux][uy] > 10) continue;

            for (int k = 0; k < 4; k++) {
                int vx = ux + dx[k];
                int vy = uy + dy[k];

                if (isValid(vx, vy) && !visited[vx][vy]) {
                    // Kiểm tra vật cản trực tiếp ở đây
                    boolean isBlocked = false;
                    Entity tile = map.getTile(vy, vx); // Lưu ý map.getTile(col, row)

                    if (tile != null && tile.isBlock()) {
                        isBlocked = true;
                        if (dodge && !(tile instanceof Wall)) isBlocked = false;
                    }

                    // Kiểm tra bom
                    if (!isBlocked) {
                        for (Bomb b : map.getBombs()) {
                            if (b.getTileX() == vy && b.getTileY() == vx) {
                                isBlocked = true;
                                break;
                            }
                        }
                    }

                    if (!isBlocked) {
                        dist[vx][vy] = dist[ux][uy] + 1;
                        visited[vx][vy] = true;
                        qX[tail] = vx;
                        qY[tail] = vy;
                        tail++;
                    }
                }
            }
        }

        return INF;
    }

    public DIRECTION intToDirection(int x) {
        switch (x) { case 0: return UP; case 1: return DOWN; case 2: return LEFT; case 3: return RIGHT; default: return NONE; }
    }

    public abstract DIRECTION path();
}