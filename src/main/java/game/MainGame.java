package game;

import input.KeyInput;
import sound.Sound;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
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
import static variables.Variables.DIRECTION.*;

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
    private int winCountdown; // 10 seconds at 120 FPS = 1200 frames
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
        VBox root = new VBox(topInfo, canvas);
        Scene scene = new Scene(root);
        VBox root2 = new VBox(gameMenu);
        Scene scene2 = new Scene(root2);
        stage.setScene(scene2);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/icon.png"));
        stage.show();
        startTime = System.nanoTime();
        lastFrame = 0;
        lastTime = 0;
        menu.createMenu();
        Sound.menu_sound.play();
        Sound.menu_sound.loop();
        countdown = 160;
        winCountdown = 1200; // 10 seconds at 120 FPS
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
                        score = 0; // Reset score when starting a new game
                        Sound.stage_sound.play();
                        Sound.stage_sound.loop();
                        stage.setScene(scene);
                        try {
                            map.createMap(MAP_URLS[0]);
                            map.resetNumber();
                        } catch (FileNotFoundException e) {
                            System.out.println(e);
                        }
                    }
                } else {
                    if (now - lastFrame >= timePerFrame) {
                        lastFrame = now;
                        map.updateMap();
                        map.renderMap(graphicsContext);
                        map.renderTopInfo(topInfoContext);
                        scene.setOnKeyPressed(keyEvent -> {
                            String code = keyEvent.getCode().toString();
                            KeyInput.keyInput.put(code, true);
                        });
                        scene.setOnKeyReleased(keyEvent -> {
                            String code = keyEvent.getCode().toString();
                            KeyInput.keyInput.put(code, false);
                        });
                        if((backToMenu == true && win == false) || (countdown != 160 && win == false)) {
                            if(countdown == 160) {
                                Sound.game_over.play();
                                stage.setScene(scene2);
                            }
                            backToMenu = false;
                            menu.renderMessage('o', gameMenuContext);
                            countdown--;
                        }
                        if((backToMenu == true && win == true) || (winCountdown != 1200 && win == true)) {
                            if(winCountdown == 1200) {
                                Sound.level_complete.play();
                                stage.setScene(scene2);
                            }
                            backToMenu = false;
                            // Show enhanced win popup
                            menu.renderWinPopup(gameMenuContext, score, winCountdown);
                            
                            // Handle user input for win screen
                            DIRECTION winInput = menu.keyInput.handleKeyInput();
                            menu.keyInput.initialization();
                            
                            if (winInput == DESTROYED) { // ENTER pressed - return to menu immediately
                                winCountdown = 0;
                            } else if (winInput == LEFT) { // ESCAPE pressed - exit game
                                Platform.exit();
                            } else {
                                winCountdown--; // Auto countdown
                            }
                        }
                        if (winCountdown == 0 && win == true) {
                            winCountdown = 1200;
                            countdown = 160;
                            choseStart = false;
                            Sound.stage_sound.stop();
                            Sound.menu_sound.play();
                            Sound.menu_sound.loop();
                            backToMenu = true;
                            win = false;
                        }
                        if (countdown == 0 && win == false) {
                            countdown = 160;
                            choseStart = false;
                            Sound.stage_sound.stop();
                            Sound.menu_sound.play();
                            Sound.menu_sound.loop();
                            backToMenu = true;
                        }

                    }
                }
                frames++;
                if (now - lastTime >= 1000000000) {
                    stage.setTitle(GAME_TITLE + " | " + frames + " FPS");//+ " | LIFES: " + map.getPlayer().getLife());
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
        if (map.getBombs().size() > 0) {
            map.getBombs().remove(0);
        }
    }

    public static void setWin(boolean win) {
        MainGame.win = win;
    }
}