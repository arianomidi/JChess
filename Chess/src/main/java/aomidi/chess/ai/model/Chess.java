package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.Board;

public class Chess {
    private Game game;

    // Game Options
    private int computerDepth = 4;
    private String startingFEN = "";//"8/P3P3/1b1K2P1/q4Q1p/7r/5pP1/n3PPp1/3k4 w - - 0 1";

    // Features
    private boolean doAnalysis = false;


    // Colors: https://misc.flogisoft.com/bash/tip_colors_and_formatting

    // Wood:52/53 Dark Gray:90 Grey:37 Pink:35 Blue:34 Green:32 Black:30
    public static String boardColor = "52m";
    // Light Yellow:230 White:255 BabyBlue:195
    private static String moveHighlight = "48;5;230m";


    // ----------- Constructors -------------

    public Chess(){
        this.game = new Game(this);
    }

    public static String getHighlight() {
        return moveHighlight;
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

    public boolean getDoAnalysis() { return doAnalysis;}

    public static String getBoardColor() {
        return "\033[49;38;5;" + boardColor;
    }

    public static String getBoldBoardColor() {
        return "\033[1;0;38;5;" + boardColor;
    }

    public static String getUnderlineBoardColor() {
        return "\033[0;4;38;5;" + boardColor;
    }

    public static int getLen() {
        return getBoardColor().length();
    }

    // ----------- Main -------------

    public void startGame(){
        this.game.playGame();
    }

}
