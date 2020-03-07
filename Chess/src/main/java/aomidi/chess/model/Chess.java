package aomidi.chess.model;

import java.util.Scanner;

import static aomidi.chess.model.Util.input;
import static aomidi.chess.model.Util.letterToInt;

public class Chess {
    private Game game;
    private boolean test;
    private String error;

    // ----------- Constructors -------------

    public Chess(){
        this.test = true;

        this.game = new Game(this);
        this.error = "";
    }

    // ----------- Getters -------------

    public Game getGame() { return game; }

    public boolean isTest() {
        return test;
    }

    // ----------- Main -------------

    public void startGame(){
        this.game.playGame();
    }

}
