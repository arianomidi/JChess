package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.Board;

public class Chess {
    private Game game;
    private int computerDepth = 3;
    private String startingFEN = "";

    // Grey:37 Green:32 Black:30
    static String boardColor = "32m";

    // ----------- Constructors -------------

    public Chess(){
        this.game = new Game(this);

    }

    // ----------- Setters -------------

    public void setStartingPosition(Board board){
        if (startingFEN.compareTo("") != 0 && startingFEN != null)
            board.loadFromFen(startingFEN);
    }

    // ----------- Getters -------------

    public Game getGame() {
        return game;
    }

    public int getDepth() {
        return computerDepth;
    }

    public static String getBoardColor() {
        return "\033[0;" + boardColor;
    }

    public static String getBoldBoardColor() {
        return "\033[1;" + boardColor;
    }

    public static int getLen() {
        return getBoardColor().length();
    }

    // ----------- Main -------------

    public void startGame(){
        this.game.playGame();
    }

}
