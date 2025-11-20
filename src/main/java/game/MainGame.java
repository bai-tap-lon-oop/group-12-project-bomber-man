package game;

import input.KeyInput;
import sound.Sound;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import map.Map;

import java.io.FileNotFoundException;

import static graphics.Sprite.SCALED_SIZE;
import static variables.Variables.*;

public class MainGame extends Application {
    private static Map map = Map.getGameMap();
    private static Menu menu = new Menu();
    private static int score = 0;
    private static boolean backToMenu = false;
    private static boolean win = false;

    private GraphicsContext graphicsContext;
    private GraphicsContext topInfoContext;
    private GraphicsContext gameMenuContext;
    private Canvas canvas;
    private Canvas topInfo;
    private Canvas gameMenu;

    // [OPTIMIZE] Có thể giảm xuống 60.0 nếu máy yếu, nhưng 120.0 vẫn ổn nếu thuật toán AI đã sửa
    private final double FPS = 120.0;
    private int countdown;
    private final long timePerFrame = (long) (1000000000 / FPS);
    private long lastFrame;
    private int frames;
    public static long time;
    private long startTime;
    private long lastTime;
    private boolean choseStart = false;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(GAME_TITLE);

        // Khởi tạo Canvas
        canvas = new Canvas(WIDTH_SCREEN * SCALED_SIZE, HEIGHT_SCREEN * SCALED_SIZE);
        topInfo = new Canvas(WIDTH_SCREEN * SCALED_SIZE, UP_BORDER * SCALED_SIZE);
        gameMenu = new Canvas(WIDTH_SCREEN * SCALED_SIZE, (HEIGHT_SCREEN + UP_BORDER) * SCALED_SIZE);

        graphicsContext = canvas.getGraphicsContext2D();
        topInfoContext = topInfo.getGraphicsContext2D();
        gameMenuContext = gameMenu.getGraphicsContext2D();

        // Font chữ
        Font font = Font.loadFont(FONT_URLS[0], 30);
        Font menu_font = Font.loadFont(FONT_URLS[0], 35);

        topInfoContext.setFont(font);
        topInfoContext.setFill(Color.WHITE);
        gameMenuContext.setFont(menu_font);
        gameMenuContext.setFill(Color.WHITE);

        // Scene Setup
        VBox root = new VBox(topInfo, canvas);
        Scene scene = new Scene(root);
        VBox root2 = new VBox(gameMenu);
        Scene scene2 = new Scene(root2);

        // [QUAN TRỌNG] Cài đặt Input 1 lần duy nhất ở đây (Không đặt trong vòng lặp)
        setupInput(scene, scene2);

        stage.setScene(scene2);
        stage.setResizable(false);
        try {
            stage.getIcons().add(new Image("/icon.png"));
        } catch (Exception e) {
            // Ignore if icon missing
        }
        stage.show();

        // Timing & Sound
        startTime = System.nanoTime();
        lastFrame = 0;
        lastTime = 0;
        menu.createMenu();
        Sound.menu_sound.play();
        Sound.menu_sound.loop();
        countdown = 160;

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                long now = currentTime - startTime;

                // --- LOGIC MENU HOẶC CHUYỂN CẢNH ---
                if (!choseStart || backToMenu) {
                    handleMenuLogic(stage, scene);
                }
                // --- LOGIC TRONG GAME ---
                else {
                    if (now - lastFrame >= timePerFrame) {
                        lastFrame = now;
                        handleGameLogic(stage, scene2);
                    }
                }

                // FPS Counter
                frames++;
                if (now - lastTime >= 1000000000) {
                    stage.setTitle(GAME_TITLE + " | " + frames + " FPS");
                    frames = 0;
                    lastTime = now;
                }
                time = (long) ((currentTime - startTime)) / 60000000 + 1;
            }
        };
        timer.start();
    }

    // [HÀM MỚI] Tách phần xử lý Input ra ngoài cho gọn và tối ưu
    private void setupInput(Scene gameScene, Scene menuScene) {
        // Input cho Menu
        menuScene.setOnKeyPressed(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), true));
        menuScene.setOnKeyReleased(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), false));

        // Input cho Game
        gameScene.setOnKeyPressed(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), true));
        gameScene.setOnKeyReleased(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), false));
    }

    // [HÀM MỚI] Tách logic Menu
    private void handleMenuLogic(Stage stage, Scene gameScene) {
        menu.setStart(false);
        menu.renderMenu(gameMenuContext);

        if (menu.isStart() || countdown != 160) {
            if (countdown == 160) {
                Sound.level_start.play();
            }
            countdown--;
            menu.renderMessage('s', gameMenuContext);
        }

        // BẮT ĐẦU GAME
        if (countdown == 0) {
            countdown = 160;
            backToMenu = false;
            choseStart = true;
            win = false;

            Sound.stage_sound.play();
            Sound.stage_sound.loop();
            stage.setScene(gameScene);

            try {
                // Load Level 1 (index 0)
                Map.getGameMap().loadLevel(0);
                map.resetNumber();
                score = 0;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // [HÀM MỚI] Tách logic Game Loop
    private void handleGameLogic(Stage stage, Scene menuScene) {
        map.updateMap();
        map.renderMap(graphicsContext);
        map.renderTopInfo(topInfoContext);

        // Xử lý khi THUA (Game Over)
        if ((backToMenu && !win) || (countdown != 160 && !win)) {
            if (countdown == 160) {
                Sound.game_over.play();
                stage.setScene(menuScene);
            }
            backToMenu = false;
            menu.renderMessage('o', gameMenuContext);
            countdown--;
        }

        // Xử lý khi THẮNG TOÀN BỘ GAME (Victory)
        if ((backToMenu && win) || (countdown != 160 && win)) {
            if (countdown == 160) {
                Sound.level_complete.play();
                stage.setScene(menuScene);
            }
            backToMenu = false;
            menu.renderMessage('c', gameMenuContext);
            countdown--;
        }

        // Quay về Menu chính sau khi đếm ngược xong
        if (countdown == 0) {
            countdown = 160;
            choseStart = false;
            Sound.stage_sound.stop();
            Sound.menu_sound.play();
            Sound.menu_sound.loop();
            backToMenu = true;
            win = false;
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setNewScore(int enemy_score) {
        MainGame.score = score + enemy_score;
    }

    public static int getScore() {
        return score;
    }

    public static void setBackToMenu(boolean backToMenu) {
        MainGame.backToMenu = backToMenu;
        // Xóa sạch bom khi về menu để tránh lỗi
        if (map.getBombs() != null && map.getBombs().size() > 0) {
            map.getBombs().clear();
        }
    }

    public static void setWin(boolean win) {
        MainGame.win = win;
    }
}