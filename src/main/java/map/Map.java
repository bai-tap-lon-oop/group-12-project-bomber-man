package map;

import entity.animateentity.Bomb;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.Character;
import entity.animateentity.character.enemy.*;
import entity.animateentity.Flame;
import entity.Entity;
import entity.staticentity.Item;
import entity.staticentity.Score;
import entity.staticentity.StaticEntity;
import entity.staticentity.Wall;
import game.MainGame;
import game.Menu;
import texture.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static variables.Variables.*;
import static graphics.Sprite.*;

public class Map {
    private static Map map;
    private static int levelNumber;
    private int time = 60 * 200;
    private Image topInfoImage;
    private Entity[][] tiles;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bomb> bombs;
    private ArrayList<Flame> flames;
    private ArrayList<Item> items;
    private ArrayList<Score> scores;
    private Bomber player;
    private boolean revival;
    private int renderX;
    private int renderY;

    private int width;
    private int height;

    public static Map getGameMap() {
        if (map == null) {
            map = new Map();
        }
        return map;
    }

    private void resetEntities() {
        tiles = new Entity[height][width];
        enemies = new ArrayList<>();
        bombs = new ArrayList<>();
        flames = new ArrayList<>();
        items = new ArrayList<>();
        scores = new ArrayList<>();
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void resetNumber() {
        Flame.flameLength = 1;
        Bomb.limit = 1;
        MainGame.setNewScore(-MainGame.getScore());
        player.setSpeed(2);
        time = 60 * 200;
    }

    public void createMap(String mapPath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(mapPath));
        topInfoImage = new Image("/top_info.png");

        if (scanner.hasNextInt()) {
            levelNumber = scanner.nextInt();
            this.height = scanner.nextInt();
            this.width = scanner.nextInt();
            scanner.nextLine();
        }

        resetEntities();
        revival = false;

        for (int i = 0; i < height; i++) {
            String string = scanner.hasNextLine() ? scanner.nextLine() : "";

            for (int j = 0; j < width; j++) {
                char c;
                if (j < string.length()) {
                    c = string.charAt(j);
                } else {
                    c = ' ';
                }

                tiles[i][j] = StaticTexture.setStatic(c, i, j);

                if (tiles[i][j] == null) {
                    tiles[i][j] = StaticTexture.setStatic(' ', i, j);
                }

                // [SỬA LỖI 1] Bỏ .getFxImage(), truyền trực tiếp đối tượng 'wall' (là Sprite)
                if (tiles[i][j] == null) {
                    tiles[i][j] = new Wall(j, i, wall);
                }

                if (tiles[i][j] instanceof Item) {
                    items.add((Item) tiles[i][j]);
                }
                if (c == '*') {
                    tiles[i][j] = BrickTexture.setBrick(i, j);
                }
                Character character = CharacterTexture.setCharacter(c, i, j);

                if (character != null) {
                    if (c == 'p') {
                        player = (Bomber) character;
                    } else {
                        enemies.add((Enemy) character);
                    }
                }
            }
        }
        scanner.close();
    }

    private void removeEntities() {
        ArrayList<Enemy> removedEnemies = new ArrayList<>();
        ArrayList<Bomb> removedBombs = new ArrayList<>();
        ArrayList<Flame> removedFlames = new ArrayList<>();
        ArrayList<Item> removedItems = new ArrayList<>();
        ArrayList<Score> removedScores = new ArrayList<>();

        scores.forEach(score -> { if (score.isRemoved()) removedScores.add(score); });
        items.forEach(item -> { if (item.isRemoved()) removedItems.add(item); });
        enemies.forEach(enemy -> { if (enemy.isRemoved()) removedEnemies.add(enemy); });
        bombs.forEach(bomb -> { if (bomb.isRemoved()) removedBombs.add(bomb); });
        flames.forEach(flame -> { if (flame.isRemoved()) removedFlames.add(flame); });

        if (player != null && player.isRemoved()) player = null;

        removedEnemies.forEach(enemy -> {
            if (enemy instanceof Balloom) scores.add(ScoreTexture.setScore('b', enemy.getTileX(), enemy.getTileY()));
            else if (enemy instanceof Oneal) scores.add(ScoreTexture.setScore('o', enemy.getTileX(), enemy.getTileY()));
            else if (enemy instanceof Doll) scores.add(ScoreTexture.setScore('d', enemy.getTileX(), enemy.getTileY()));
            else if (enemy instanceof Minvo) scores.add(ScoreTexture.setScore('m', enemy.getTileX(), enemy.getTileY()));
            else if (enemy instanceof Kondoria) scores.add(ScoreTexture.setScore('k', enemy.getTileX(), enemy.getTileY()));
            enemies.remove(enemy);
        });

        removedBombs.forEach(bomb -> bombs.remove(bomb));
        removedFlames.forEach(flame -> flames.remove(flame));
        removedItems.forEach(item -> items.remove(item));
        removedScores.forEach(score -> scores.remove(score));
    }

    public void updateMap() {
        if (revival) return;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) tiles[i][j].update();
            }
        }
        enemies.forEach(Enemy::update);
        if (player != null) player.update();
        bombs.forEach(Bomb::update);
        flames.forEach(Flame::update);
        items.forEach(Item::update);
        scores.forEach(Score::update);
        removeEntities();
    }

    public void renderTopInfo(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(topInfoImage, 0, 0);
        graphicsContext.fillText("Score: " + MainGame.getScore(), 0.6 * SCALED_SIZE, SCALED_SIZE * 0.8);
        if(MainGame.getScore() > Menu.getHighscore()) {
            try {
                PrintWriter writer = new PrintWriter("src/main/resources/menu/highscore.txt");
                writer.print("");
                writer.print(MainGame.getScore());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (time != 0) {
            graphicsContext.fillText("Time: " + (time--) / 60, 0.6 * SCALED_SIZE, SCALED_SIZE * 1.6);
        } else {
            graphicsContext.fillText("Time: " + time, 0.6 * SCALED_SIZE, SCALED_SIZE * 1.6);
            MainGame.setBackToMenu(true);
        }
        graphicsContext.fillText("Stage: " + levelNumber, 10.6 * SCALED_SIZE, SCALED_SIZE * 0.8);
        if (player != null) {
            graphicsContext.fillText("Life: " + player.getLife(), 10.6 * SCALED_SIZE, SCALED_SIZE * 1.6);
            if(player.getLife() == 0) MainGame.setBackToMenu(true);
        }
    }

    private void renderRevival(GraphicsContext graphicsContext) {
        if (renderX == 0 && renderY == 0) {
            revival = false;
            return;
        } else {
            renderX = Math.max(0, renderX - player.getTimeRevival());
            renderY = Math.max(0, renderY - player.getTimeRevival());
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) tiles[i][j].render(graphicsContext);
            }
        }
        enemies.forEach(enemy -> enemy.render(graphicsContext));
        if (player != null) player.render(graphicsContext);
    }

    private void updateRenderXY() {
        if (player == null) return;
        renderX = player.getPixelX() - (WIDTH_SCREEN / 2) * SCALED_SIZE;
        renderY = player.getPixelY() - (HEIGHT_SCREEN / 2) * SCALED_SIZE;
        if (renderX < 0) renderX = 0;
        if (renderX > this.width * SCALED_SIZE - WIDTH_SCREEN * SCALED_SIZE) {
            renderX = this.width * SCALED_SIZE - WIDTH_SCREEN * SCALED_SIZE;
        }
        if (renderY < 0) renderY = 0;
        if (renderY > this.height * SCALED_SIZE - HEIGHT_SCREEN * SCALED_SIZE) {
            renderY = this.height * SCALED_SIZE - HEIGHT_SCREEN * SCALED_SIZE;
        }
    }

    public void renderMap(GraphicsContext graphicsContext) {
        if (revival) {
            renderRevival(graphicsContext);
            return;
        }
        updateRenderXY();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] != null) tiles[i][j].render(graphicsContext);
            }
        }
        enemies.forEach(enemy -> enemy.render(graphicsContext));
        if (player != null) player.render(graphicsContext);
        bombs.forEach(bomb -> bomb.render(graphicsContext));
        flames.forEach(flame -> flame.render(graphicsContext));
        items.forEach(item -> item.render(graphicsContext));
        scores.forEach(score -> score.render(graphicsContext));
    }

    public void setTile(int x, int y, Entity entity) {
        if (x >= 0 && x < height && y >= 0 && y < width) {
            tiles[x][y] = entity;
        }
    }

    public Entity getTile(int x, int y) {
        // [SỬA LỖI 2] Bỏ .getFxImage(), truyền trực tiếp đối tượng 'wall'
        if (y < 0 || y >= height || x < 0 || x >= width) {
            return new Wall(x, y, wall);
        }

        Entity tile = tiles[y][x];

        // [SỬA LỖI 3] Bỏ .getFxImage(), truyền trực tiếp đối tượng 'wall'
        if (tile == null) {
            return new Wall(x, y, wall);
        }

        return tile;
    }

    public ArrayList<Wall> getWalls() {
        ArrayList<Wall> walls = new ArrayList<>();
        if (getTile(1, 0) instanceof Wall) walls.add((Wall) getTile(1, 0));
        if (getTile(2, 0) instanceof Wall) walls.add((Wall) getTile(2, 0));
        if (getTile(3, 0) instanceof Wall) walls.add((Wall) getTile(3, 0));
        return walls;
    }

    public Bomber getPlayer() { return this.player; }
    public ArrayList<Bomb> getBombs() { return bombs; }
    public ArrayList<Flame> getFlames() { return flames; }
    public ArrayList<Item> getItems() { return items; }
    public int getRenderX() { return renderX; }
    public int getRenderY() { return renderY; }
    public void setRevival(boolean revival) { this.revival = revival; }
    public static int getLevelNumber() { return levelNumber; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}