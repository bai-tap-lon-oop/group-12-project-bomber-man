package game;

import input.MenuInput;
import variables.Variables.DIRECTION;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import input.KeyInput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static graphics.Sprite.SCALED_SIZE;
import static variables.Variables.DIRECTION.*;
import static variables.Variables.MAP_URLS;
import static variables.Variables.*;

public class Menu {
    public KeyInput keyInput = new MenuInput();
    private Image newGame_Start;
    private Image newGame_Exit;
    private Image Background;
    private Image escButton;
    private Image qButton;
    private File high_score;
    private static int highscore;
    private Scanner scanner;
    private DIRECTION direction;
    private int state = 1;
    private boolean start = false;

    private static int gameMode = 1; // lưu mode chơi đã chọn (default = 1)
    private boolean modeSelected = true;
    private int modeState = 1; // 1 = 1 Player, 2 = 2 Players

    public void createMenu() {
        newGame_Start = new Image("/menu/GameMenu_Start.png");
        newGame_Exit = new Image("/menu/GameMenu_Exit.png");
        Background = new Image("/menu/Background.png");
        escButton = new Image("/menu/Button_ESC.png");
        qButton = new Image("/menu/Button_Q.png");
    }
    
    // giao diện menu chính
    public void renderMenu(GraphicsContext graphicsContext) {
        // Màn hình menu chính
        graphicsContext.drawImage(newGame_Start, 0, 0);
        if (state == 1) {
            graphicsContext.drawImage(newGame_Start, 0, 0);
        } else if (state == 0) {
            graphicsContext.drawImage(newGame_Exit, 0, 0);
        }
        try {
            high_score = new File("src/main/resources/menu/highscore.txt");
            scanner = new Scanner(high_score);
            highscore = scanner.nextInt();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        graphicsContext.fillText("Start", SCALED_SIZE * 6.7, SCALED_SIZE * 10.93);
        graphicsContext.fillText("Exit", SCALED_SIZE * 7.14, SCALED_SIZE * 11.95);
        graphicsContext.fillText("Highscore: " + String.valueOf(highscore), SCALED_SIZE * 4.5, SCALED_SIZE * 12.95);
        direction = keyInput.handleKeyInput();
        keyInput.initialization();
        if (direction == UP) {
            state = 1;
        }
        if (direction == DOWN) {
            state = 0;
        }
        if (direction == DESTROYED && state == 1) {
            // Khi bấm Start, chuyển sang màn hình chọn mode
            modeSelected = false;
        }
        if (direction == DESTROYED && state == 0) {
            Platform.exit();
        }
    }

    // giao diện chọn mode
    public void renderModeSelection(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(Background, 0, 0);
        graphicsContext.fillText("Select Game Mode", SCALED_SIZE * 4, SCALED_SIZE * 5);

        // Hiển thị lựa chọn mode
        if (modeState == 1) {
            graphicsContext.setFill(Color.RED);
        } else {
            graphicsContext.setFill(Color.WHITE);
        }
        graphicsContext.fillText("1 Player", SCALED_SIZE * 5.5, SCALED_SIZE * 7);

        if (modeState == 2) {
            graphicsContext.setFill(Color.RED);
        } else {
            graphicsContext.setFill(Color.WHITE);
        }
        graphicsContext.fillText("2 Players", SCALED_SIZE * 5.5, SCALED_SIZE * 8.5);
        graphicsContext.setFill(Color.WHITE);

        // Xử lý input
        direction = keyInput.handleKeyInput();
        keyInput.initialization();
        if (direction == UP) {
            modeState = 1;
        }
        if (direction == DOWN) {
            modeState = 2;
        }
        if (direction == DESTROYED) {
            gameMode = modeState;
            modeSelected = true;
            start = true;
        }
    }


    public void renderMessage(char c, GraphicsContext graphicsContext) {
        graphicsContext.drawImage(Background, 0, 0);
        switch (c) {
            case 's': graphicsContext.fillText("Stage 1", SCALED_SIZE * 6, SCALED_SIZE * 7.5);
                break;
            case 'c':
            {
                try {
                    String nextPath = MAP_URLS[MainGame.currentLevel];
                    graphicsContext.fillText(String.format("Stage %d", MainGame.currentLevel + 1), SCALED_SIZE * 6, SCALED_SIZE * 7.5);
                } catch (Exception e) {
                    graphicsContext.fillText("Level Completed!", SCALED_SIZE * 4, SCALED_SIZE * 7.5);
                }
                break;
            }
            case 'w':
                graphicsContext.fillText("Level Completed!", SCALED_SIZE * 4, SCALED_SIZE * 7.5);
                break;
            case 'l':
                graphicsContext.fillText("Game Over!", SCALED_SIZE * 5, SCALED_SIZE * 7.5);
                break;
            case 'v':
                graphicsContext.fillText("You Win!!", SCALED_SIZE * 6, SCALED_SIZE * 7.5);
                break;
        }
    }
    
    public void resetModeSelection() {
        modeSelected = true; // Reset về true để hiển thị menu chính
        modeState = 1;
        start = false;
    }

    // giao diện pause menu
    public void renderPauseMenu(GraphicsContext graphicsContext) {

        graphicsContext.setFill(Color.rgb(0, 0, 0, 0.7));
        graphicsContext.fillRect(0, 0, WIDTH_SCREEN * SCALED_SIZE, (HEIGHT_SCREEN + UP_BORDER) * SCALED_SIZE);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillText("PAUSED", SCALED_SIZE * 5.7, SCALED_SIZE * 5);

        graphicsContext.drawImage(escButton, SCALED_SIZE * 4.2, SCALED_SIZE * 6.5);
        graphicsContext.fillText("Resume", SCALED_SIZE * 7.4, SCALED_SIZE * 7.5);

        graphicsContext.drawImage(qButton, SCALED_SIZE * 4.2, SCALED_SIZE * 8.7);
        graphicsContext.fillText("  Quit", SCALED_SIZE * 7.4, SCALED_SIZE * 9.5);
    }

    public boolean isModeSelected() {return modeSelected;}
    public boolean isStart() {return start;}
    public void setStart(boolean start) {this.start = start;}

    public static int getHighscore() {return highscore;}
    public static int getGameMode() {return gameMode;}
}