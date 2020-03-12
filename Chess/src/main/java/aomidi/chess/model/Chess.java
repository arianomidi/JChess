package aomidi.chess.model;

import java.util.Scanner;

import static aomidi.chess.model.Util.input;
import static aomidi.chess.model.Util.letterToInt;

public class Chess {
    private Game game;
    private boolean test;
    private boolean flipBoard;
    private static String boardColor;
    private String error;

    // ----------- Constructors -------------

    public Chess(){
        this.test = false;
        this.flipBoard = true;
        // Grey:37 Green:32 Black:30
        this.boardColor = "32m";

        this.game = new Game(this);
        this.error = "";
    }

    // ----------- Getters -------------

    public Game getGame() { return game; }

    public boolean isTest() {
        return test;
    }

    public boolean flipBoardSelected() {
        return flipBoard;
    }

    public static String getBoardColor() { return "\033[0;" + boardColor; }

    public static int getLen() { return getBoardColor().length(); }

    // ----------- Main -------------

    public void startGame(){
        this.game.playGame();
    }

}
