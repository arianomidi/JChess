package aomidi.chess.ai.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.move.*;

public class Game {
    private Board board;
    private final Engine engine;
    private final MoveLog moveLog;


    private Player whitePlayer;
    private Player blackPlayer;
    private Player curPlayer;
    private boolean isGameOver;

    // ----------- Constructors -------------
    public Game(Chess chess){
        this.board = new Board();
        this.engine = new Engine(chess.getDepth());
        this.moveLog = new MoveLog();

//        chess.setStartingPosition(board);

        // Init Players
        this.whitePlayer = new User(Side.WHITE, this);
        this.blackPlayer = new User(Side.BLACK, this);
        this.curPlayer = this.whitePlayer;

        this.isGameOver = false;

        // Print the init chessboard
        printBoard(board, this);
    }

    // ----------- Getters -------------

    public Board getBoard() {
        return board;
    }

    public Player getOpposingPlayer() {
        if (curPlayer.getSide() == Side.WHITE) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
    }

    public Engine getEngine() {
        return engine;
    }


    // ----------- Actions -------------


    // ----------- Main Function -------------


    // ----------- Game Functions -------------

    private String GameStatus() {

        return "";
    }

}

