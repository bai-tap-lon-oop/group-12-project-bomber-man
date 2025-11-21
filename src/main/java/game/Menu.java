package game;

import input.MenuInput;
import sound.Sound;
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

    public void createMenu() {
        newGame_Start = new Image("/menu/GameMenu_Start.png");
        newGame_Exit = new Image("/menu/GameMenu_Exit.png");
        Background = new Image("/menu/Background.png");
        escButton = new Image("/menu/Button_ESC.png");
        qButton = new Image("/menu/Button_Q.png");
    }

    public void renderMenu(GraphicsContext graphicsContext) {
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
            start = true;
            Sound.menu_sound.stop();
        }
        if (direction == DESTROYED && state == 0) {
            Platform.exit();
        }
    }

    public void renderMessage(char c, GraphicsContext graphicsContext) {
        graphicsContext.drawImage(Background, 0, 0);
        switch (c) {
            case 's': 
                graphicsContext.fillText("Start Challenge!", SCALED_SIZE * 4, SCALED_SIZE * 7.5);
                break;
            case 'w': 
                graphicsContext.fillText("Level Completed!", SCALED_SIZE * 4, SCALED_SIZE * 7.5);
                break;
            case 'l': 
                graphicsContext.fillText("Game Over!", SCALED_SIZE * 5, SCALED_SIZE * 7.5);
                break;
            case 'v': 
                graphicsContext.fillText("You Win!!", SCALED_SIZE * 5.5, SCALED_SIZE * 7.5);
                break;
        }
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public static int getHighscore() {
        return highscore;
    }

    public void renderPauseMenu(GraphicsContext graphicsContext) {

        graphicsContext.setFill(Color.rgb(0, 0, 0, 0.7));
        graphicsContext.fillRect(0, 0, WIDTH_SCREEN * SCALED_SIZE, (HEIGHT_SCREEN + UP_BORDER) * SCALED_SIZE);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillText("PAUSED", SCALED_SIZE * 5.7, SCALED_SIZE * 5);
        
        // graphicsContext.fillText("Press", SCALED_SIZE * 2.5, SCALED_SIZE * 7.5);
        // graphicsContext.drawImage(escButton, SCALED_SIZE * 5.2, SCALED_SIZE * 6.5);
        // graphicsContext.fillText("to Resume", SCALED_SIZE * 7.7, SCALED_SIZE * 7.5);
        
        // graphicsContext.fillText("Press", SCALED_SIZE * 3.7, SCALED_SIZE * 9.5);
        // graphicsContext.drawImage(qButton, SCALED_SIZE * 6.4, SCALED_SIZE * 8.7);
        // graphicsContext.fillText("to Quit", SCALED_SIZE * 7.9, SCALED_SIZE * 9.5);

        graphicsContext.drawImage(escButton, SCALED_SIZE * 4.2, SCALED_SIZE * 6.5);
        graphicsContext.fillText("Resume", SCALED_SIZE * 7.4, SCALED_SIZE * 7.5);
        
        graphicsContext.drawImage(qButton, SCALED_SIZE * 4.2, SCALED_SIZE * 8.7);
        graphicsContext.fillText("  Quit", SCALED_SIZE * 7.4, SCALED_SIZE * 9.5);
    }
}
