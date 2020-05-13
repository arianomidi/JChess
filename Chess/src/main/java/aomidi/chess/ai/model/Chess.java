package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public class Chess {
    private Game game;
    private int computerDepth = 4;
    private String startingFEN = "";

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

    // ----------- Main -------------

    public void startGame(){
        this.game.playGame();
    }

}
