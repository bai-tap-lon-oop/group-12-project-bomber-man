package game;

import input.KeyInput;
import input.Player2Input;
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
    // Constants
    private final int countDown_Max = 160;
    private final int continue_delay = 70;
    
    public static int currentLevel = 1;
    public static long time; 
    
    private static Map map = Map.getGameMap();
    private static int score = 0;
    private static boolean backToMenu = false;
    private static boolean win = false;
    
    private Menu menu = new Menu();
    private boolean paused = false;
    private boolean gameStarted = false;

    private GraphicsContext graphicsContext;
    private GraphicsContext topInfoContext;
    private GraphicsContext gameMenuContext;

    private final double FPS = 120.0;
    private int countDown;
    private final long timePerFrame = (long) (1000000000 / FPS);
    private long lastFrame;
    private int frames;
    private long startTime;
    private long lastTime;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(GAME_TITLE);
        
        Canvas canvas = new Canvas(WIDTH_SCREEN * SCALED_SIZE, HEIGHT_SCREEN * SCALED_SIZE);
        Canvas topInfo = new Canvas(WIDTH_SCREEN * SCALED_SIZE, UP_BORDER * SCALED_SIZE);
        Canvas gameMenu = new Canvas(WIDTH_SCREEN * SCALED_SIZE, (HEIGHT_SCREEN + UP_BORDER) * SCALED_SIZE);

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

        countDown = countDown_Max; // Countdown cho hiệu ứng chuyển cảnh

        // ===== GAME LOOP - ANIMATION TIMER =====
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                long now = currentTime - startTime;

                if (!gameStarted || backToMenu) {
                    handleMenuState(scene, scene2, stage);
                } else {
                    // ===== GAME LOOP CHÍNH =====
                    if (now - lastFrame >= timePerFrame) {
                        lastFrame = now;

                        if (paused) {
                            handlePauseState(scene, scene2, stage);
                            return;
                        }

                        handleGamePlaying(scene, scene2, stage);
                    }
                }

                // ===== TÍNH FPS THỰC TẾ =====
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

    // Xử lý trạng thái menu
    private void handleMenuState(Scene scene, Scene scene2, Stage stage) {
        menu.setStart(false);

        if (!menu.isModeSelected()) {
            menu.renderModeSelection(gameMenuContext);
        } else {
            menu.renderMenu(gameMenuContext);
        }

        scene2.setOnKeyPressed(keyEvent -> {
            String code = keyEvent.getCode().toString();
            if (menu.keyInput instanceof input.MenuInput) {
                ((input.MenuInput) menu.keyInput).setKeyPressed(code, true);
            }
        });

        scene2.setOnKeyReleased(keyEvent -> {
            String code = keyEvent.getCode().toString();
            if (menu.keyInput instanceof input.MenuInput) {
                ((input.MenuInput) menu.keyInput).setKeyPressed(code, false);
            }
        });

        if (menu.isStart() || countDown != countDown_Max) {
            if (countDown == countDown_Max) {
                Sound.menu_sound.stop();
                Sound.level_start.play();
            }
            countDown--;
            menu.renderMessage('s', gameMenuContext);
        }

        if (countDown == 0) {
            win = false;
            score = 0;
            PlayerInput.lastPressedKey = null;
            KeyInput.keyInput.clear();
            currentLevel = 1;
            countDown = countDown_Max;
            backToMenu = false;
            gameStarted = true;
            Sound.menu_sound.stop();
            Sound.stage_sound.play();
            Sound.stage_sound.loop();
            // Tạo map TRƯỚC khi chuyển scene để tránh NullPointerException
            try {
                map.createMap(MAP_URLS[currentLevel - 1]);
                map.resetNumber();
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }
            stage.setScene(scene);
        }
    }

    // Xử lý trạng thái pause
    private void handlePauseState(Scene scene, Scene scene2, Stage stage) {
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
                gameRestart();
            }
        });
    }

    // Xử lý gameplay chính
    private void handleGamePlaying(Scene scene, Scene scene2, Stage stage) {
        map.updateMap();
        map.renderMap(graphicsContext);
        map.renderTopInfo(topInfoContext);

        scene.setOnKeyPressed(keyEvent -> {
            String code = keyEvent.getCode().toString();
            if (code.equals("ESCAPE")) {
                paused = true;
                return;
            }
            KeyInput.keyInput.put(code, true);
            PlayerInput.lastPressedKey = code;
            if (map.getPlayer2() != null && map.getPlayer2().keyInput instanceof input.Player2Input) {
                Player2Input.lastPressedKey = code;
            }
        });

        scene.setOnKeyReleased(keyEvent -> {
            String code = keyEvent.getCode().toString();
            KeyInput.keyInput.put(code, false);
            PlayerInput.lastPressed();
            if (map.getPlayer2() != null && map.getPlayer2().keyInput instanceof input.Player2Input) {
                Player2Input.lastPressed();
            }
        });

        // Xử lý thua
        if ((backToMenu && !win) || (countDown != countDown_Max && !win)) {
            if (countDown == countDown_Max) {
                Sound.stage_sound.stop();
                Sound.game_over.play();
                stage.setScene(scene2);
            }
            backToMenu = false;
            menu.renderMessage('l', gameMenuContext);
            countDown--;
        }

        // Xử lý thắng
        if ((backToMenu && win) || (countDown != countDown_Max && win)) {
            if (countDown == countDown_Max) {
                Sound.level_complete.play();
                stage.setScene(scene2);
            }
            backToMenu = false;

            if (currentLevel < (MAP_URLS.length)) {
                menu.renderMessage('w', gameMenuContext);
                if (countDown <= continue_delay) {
                    menu.renderMessage('c', gameMenuContext);
                }
            } else {
                menu.renderMessage('v', gameMenuContext);
                win = false;
            }
            countDown--;
        }

        // Chuyển level hoặc về menu
        if (countDown == 0) {
            countDown = countDown_Max;

            if (!win) {
                gameStarted = false;
                Sound.stage_sound.stop();
                Sound.menu_sound.play();
                Sound.menu_sound.loop();
                backToMenu = true;
                win = false;
                return;
            } else {
                PlayerInput.lastPressedKey = null;
                if (map.getPlayer2() != null && map.getPlayer2().keyInput instanceof input.Player2Input) {
                    Player2Input.lastPressedKey = null;
                }
                KeyInput.keyInput.clear();
                try {
                    currentLevel++;
                    map.createMap(MAP_URLS[currentLevel - 1]);
                    map.resetNumber();
                    backToMenu = false;
                    win = false;
                    stage.setScene(scene);
                } catch (Exception e) {
                    gameRestart();
                    win = false;
                    stage.setScene(scene2);
                    currentLevel = 1;
                }
            }
        }
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

    public void gameRestart() {
        paused = false;
        backToMenu = true;
        gameStarted = false;
        menu.resetModeSelection();
        Sound.stage_sound.stop();
        Sound.menu_sound.play();
        Sound.menu_sound.loop();
    }
}
