package aomidi.chess.model;

import java.util.Scanner;

import static aomidi.chess.model.Util.input;
import static aomidi.chess.model.Util.letterToInt;

public class Chess {
    private Game game;
    private boolean test;
    private boolean flipBoard;
    private String error;

    // ----------- Constructors -------------

    public Chess(){
        this.test = false;
        this.flipBoard = true;

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

    // ----------- Main -------------

    public void startGame(){
        this.game.playGame();
    }

}
