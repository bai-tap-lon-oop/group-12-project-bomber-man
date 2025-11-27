package game;

import input.KeyInput;
import input.PlayerInput;
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
    private static boolean paused = false;  // Trạng thái pause game

    private GraphicsContext graphicsContext;
    private GraphicsContext topInfoContext;
    private GraphicsContext gameMenuContext;
    private Canvas canvas;
    private Canvas topInfo;
    private Canvas gameMenu;

    private final double FPS = 120.0;
    private int countdown;
    public static int currentLevel = 1;
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
        canvas = new Canvas(WIDTH_SCREEN * SCALED_SIZE, HEIGHT_SCREEN * SCALED_SIZE);
        topInfo = new Canvas(WIDTH_SCREEN * SCALED_SIZE, UP_BORDER * SCALED_SIZE);
        gameMenu = new Canvas(WIDTH_SCREEN * SCALED_SIZE, (HEIGHT_SCREEN + UP_BORDER) * SCALED_SIZE);

        graphicsContext = canvas.getGraphicsContext2D();
        topInfoContext = topInfo.getGraphicsContext2D();
        gameMenuContext = gameMenu.getGraphicsContext2D();

        Font font = Font.loadFont(FONT_URLS[0], 30);
        Font menu_font = Font.loadFont(FONT_URLS[0], 35);

        topInfoContext.setFont(font);
        topInfoContext.setFill(Color.WHITE);

        gameMenuContext.setFont(menu_font);
        gameMenuContext.setFill(Color.WHITE);

        // ===== THIẾT LẬP SCENE =====
        // Scene 1: Game chính (topInfo + canvas)
        VBox root = new VBox(topInfo, canvas);
        Scene scene = new Scene(root);

        // Scene 2: Menu (chỉ có gameMenu)
        VBox root2 = new VBox(gameMenu);
        Scene scene2 = new Scene(root2);

        // Bắt đầu với menu
        stage.setScene(scene2);
        stage.setResizable(false);  // Không cho resize cửa sổ
        stage.getIcons().add(new Image("/icon.png"));
        stage.show();

        // ===== KHỞI TẠO TIMING =====
        startTime = System.nanoTime();
        lastFrame = 0;
        lastTime = 0;

        // ===== KHỞI TẠO MENU VÀ ÂM THANH =====
        menu.createMenu();
        Sound.menu_sound.play();
        Sound.menu_sound.loop();

        countdown = 160; // Countdown cho hiệu ứng chuyển cảnh

        // ===== GAME LOOP - ANIMATION TIMER =====
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                long now = currentTime - startTime;

                if (!choseStart || backToMenu) {
                    menu.setStart(false);
                    menu.renderMenu(gameMenuContext);

                    scene2.setOnKeyPressed(keyEvent -> {
                        String code = keyEvent.getCode().toString();
                        KeyInput.keyInput.put(code, true);
                    });

                    scene2.setOnKeyReleased(keyEvent -> {
                        String code = keyEvent.getCode().toString();
                        KeyInput.keyInput.put(code, false);
                    });

                    if(menu.isStart() || countdown != 160) {
                        if(countdown == 160) {
                            Sound.level_start.play();
                        }
                        countdown--;
                        menu.renderMessage('s', gameMenuContext);
                    }
                    if (countdown == 0) {
                        countdown = 160;
                        backToMenu = false;
                        choseStart = true;
                        Sound.stage_sound.play();
                        Sound.stage_sound.loop();
                        stage.setScene(scene);
                        try {
                            map.createMap(MAP_URLS[currentLevel - 1]);
                            map.resetNumber();
                        } catch (FileNotFoundException e) {
                            System.out.println(e);
                        }
                    }
                } else {
                    // ===== GAME LOOP CHÍNH =====
                    if (now - lastFrame >= timePerFrame) {
                        lastFrame = now;

                        // ===== XỬ LÝ PAUSE MENU =====
                        if (paused) {
                            menu.renderPauseMenu(gameMenuContext);

                            if (stage.getScene() != scene2) {
                                stage.setScene(scene2);
                            }

                            scene2.setOnKeyPressed(keyEvent -> {
                                String code = keyEvent.getCode().toString();
                                if (code.equals("ESCAPE")) {
                                    paused = false;
                                    stage.setScene(scene);
                                } else if (code.equals("Q")) {
                                    paused = false;
                                    backToMenu = true;
                                    choseStart = false;
                                    Sound.stage_sound.stop();
                                    Sound.menu_sound.play();
                                    Sound.menu_sound.loop();
                                }
                            });
                            return;  // Skip game update khi pause
                        }

                        map.updateMap();
                        map.renderMap(graphicsContext);
                        map.renderTopInfo(topInfoContext);

                        scene.setOnKeyPressed(keyEvent -> {
                            String code = keyEvent.getCode().toString();

                            // ESC để pause
                            if (code.equals("ESCAPE")) {
                                paused = true;
                                return;
                            }

                            KeyInput.keyInput.put(code, true);
                            PlayerInput.lastPressedKey = code;
                        });
                        scene.setOnKeyReleased(keyEvent -> {
                            String code = keyEvent.getCode().toString();
                            KeyInput.keyInput.put(code, false);
                            if (KeyInput.keyInput.getOrDefault("W", false)) PlayerInput.lastPressedKey = "W";
                            else if (KeyInput.keyInput.getOrDefault("A", false)) PlayerInput.lastPressedKey = "A";
                            else if (KeyInput.keyInput.getOrDefault("S", false)) PlayerInput.lastPressedKey = "S";
                            else if (KeyInput.keyInput.getOrDefault("D", false)) PlayerInput.lastPressedKey = "D";
                            else if (KeyInput.keyInput.getOrDefault("SPACE", false)) PlayerInput.lastPressedKey = "SPACE";
                            else PlayerInput.lastPressedKey = null;

                        });

                        // ===== XỬ LÝ LOSE =====
                        if((backToMenu == true && win == false) || (countdown != 160 && win == false)) {
                            if(countdown == 160) {
                                Sound.stage_sound.stop();
                                Sound.game_over.play();

                                stage.setScene(scene2);
                            }
                            backToMenu = false;
                            menu.renderMessage('l', gameMenuContext);  // Hiển "Game Over"
                            countdown--;
                        }

                        // ===== XỬ LÝ WIN =====
                        if ((backToMenu && win) || (countdown != 160 && win)) {

                            if (countdown == 160) {
                                Sound.stage_sound.stop();
                                Sound.level_complete.play();   // Âm thắng level
                                stage.setScene(scene2);// Chuyển về màn menu
                            }


                            backToMenu = false;

                            // ----- HIỂN THỊ TỪNG PHẦN -----
                            if(currentLevel < (MAP_URLS.length)) {
                                Sound.stage_sound.play();
                                menu.renderMessage('w', gameMenuContext);
                                if (countdown <= 70) {     // 160 -> 100 = 60 frame = ~1s
                                    menu.renderMessage('c', gameMenuContext);
                                }
                            }
                            else {
                                menu.renderMessage('v', gameMenuContext);
                            }

                            countdown--;
                        }


                        if (countdown == 0) {
                            countdown = 160;

                            if (!win) {
                                // Trở về menu khi thua
                                choseStart = false;
                                Sound.stage_sound.stop();
                                Sound.menu_sound.play();
                                Sound.menu_sound.loop();
                                backToMenu = true;
                                win = false;
                                return;
                            } else {
                                // WIN → TẢI LEVEL TIẾP THEO NHƯ MAIN GAME 2
                                try {
                                    currentLevel++;  // ⚠ Quan trọng
                                    map.createMap(MAP_URLS[currentLevel - 1]);
                                    map.resetNumber();

                                    // Reset phím bấm khi sang level mới
                                    PlayerInput.lastPressedKey = null;
                                    KeyInput.keyInput.clear();   // nếu muốn xoá hết trạng thái phím đang giữ

                                    backToMenu = false;
                                    win = false;
                                    stage.setScene(scene);
                                } catch (Exception e) {
                                    // Hết level → quay lại menu
                                    choseStart = false;
                                    Sound.stage_sound.stop();
                                    Sound.menu_sound.play();
                                    Sound.menu_sound.loop();
                                    backToMenu = true;
                                    win = false;
                                    stage.setScene(scene2);
                                    currentLevel = 1;
                                }
                            }
                        }


                    }
                }

                // ===== TÍNH FPS THỰC TẾ =====
                frames++;
                if (now - lastTime >= 1000000000) {
                    // Hiển thị FPS trên title bar
                    stage.setTitle(GAME_TITLE + " | " + frames + " FPS");
                    frames = 0;
                    lastTime = now;
                }

                // Cập nhật biến time cho animation (chia 60 triệu để chuyển từ nano sang đơn vị phù hợp)
                time = (long) ((currentTime - startTime)) / 60000000 + 1;
            }
        };

        // Bắt đầu timer (bắt đầu game loop)
        timer.start();
    }

    public static void main(String[] args) {
        launch();  // Khởi động JavaFX Application
    }

    public static int getScore() {return score;}
    public static void setNewScore(int enemy_score) {MainGame.score = score + enemy_score;}

    public static void setBackToMenu(boolean backToMenu) {
        MainGame.backToMenu = backToMenu;

        // Xóa bom đầu tiên nếu còn (tránh lỗi khi reset)
        if (map.getBombs().size() > 0) {
            map.getBombs().remove(0);
        }
    }

    public static void setWin(boolean win) {
        MainGame.win = win;
    }
}
