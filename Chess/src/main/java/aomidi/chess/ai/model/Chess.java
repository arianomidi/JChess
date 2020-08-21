package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.Board;

import static aomidi.chess.ai.model.Util.*;

public class Chess {
    private Game game;

    // Game Options
    private int computerDepth = 0;
    private String startingFEN = ""; //"r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 1";

    // Features
    private boolean doAnalysis = true;


    // Colors: https://misc.flogisoft.com/bash/tip_colors_and_formatting

    // Wood:52/53 Dark Gray:90 Grey:37 Pink:35 Blue:34 Green:32 Black:30
    public static String boardColor = "52m";
    // Light Yellow:230 White:255 BabyBlue:195
    private static String moveHighlight = "48;5;230m";

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

    public static String getHighlight() {
        return moveHighlight;
    }

    public static int getLen() {
        return getBoardColor().length();
    }

    // ----------- Main -------------

    public void startGame(){
        if (computerDepth == 0) {
            computerDepth = Integer.parseInt(input(boldAndUnderlineBoardColor("\nEnter Computer Strength 1-7 (Recomended 5):")));
            System.out.println();
        }

        if (startingFEN == null) {
            String standardGame = input("Standard Game? (y/n").toLowerCase();

            if (standardGame.compareTo("y") == 0)
                startingFEN = "";
            else
                startingFEN = input("Enter Starting FEN");
        }

        this.game = new Game(this);

        this.game.playGame();
    }

}
