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

        stage.setScene(scene2);
        stage.setResizable(false);
        try {
            stage.getIcons().add(new Image("/icon.png")); // Cần đảm bảo file icon tồn tại để ko lỗi
        } catch (Exception e) {
            System.out.println("Icon not found");
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
                    menu.setStart(false);
                    menu.renderMenu(gameMenuContext);

                    scene2.setOnKeyPressed(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), true));
                    scene2.setOnKeyReleased(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), false));

                    if(menu.isStart() || countdown != 160) {
                        if(countdown == 160) {
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
                        win = false; // Reset trạng thái thắng khi chơi lại

                        Sound.stage_sound.play();
                        Sound.stage_sound.loop();
                        stage.setScene(scene);

                        // [ĐÃ SỬA] Code gọn gàng hơn, gọi loadLevel(0)
                        try {
                            Map.getGameMap().loadLevel(0);
                            map.resetNumber();
                            score = 0; // Reset điểm khi chơi mới
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // --- LOGIC TRONG GAME ---
                else {
                    if (now - lastFrame >= timePerFrame) {
                        lastFrame = now;
                        map.updateMap();
                        map.renderMap(graphicsContext);
                        map.renderTopInfo(topInfoContext);

                        scene.setOnKeyPressed(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), true));
                        scene.setOnKeyReleased(keyEvent -> KeyInput.keyInput.put(keyEvent.getCode().toString(), false));

                        // Xử lý khi THUA (Game Over)
                        if((backToMenu && !win) || (countdown != 160 && !win)) {
                            if(countdown == 160) {
                                Sound.game_over.play();
                                stage.setScene(scene2);
                            }
                            backToMenu = false;
                            menu.renderMessage('o', gameMenuContext); // 'o' = Game Over
                            countdown--;
                        }

                        // Xử lý khi THẮNG (Victory / Level Complete)
                        if((backToMenu && win) || (countdown != 160 && win)) {
                            if(countdown == 160) {
                                Sound.level_complete.play();
                                stage.setScene(scene2);
                            }
                            backToMenu = false;
                            menu.renderMessage('c', gameMenuContext); // 'c' = Complete
                            countdown--;
                        }

                        // Quay về Menu chính sau khi hiện thông báo xong
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
        // Xóa bom thừa nếu có để tránh lỗi khi vào lại
        if (map.getBombs().size() > 0) {
            map.getBombs().clear();
        }
    }

    public static void setWin(boolean win) {
        MainGame.win = win;
    }
}