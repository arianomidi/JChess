package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.*;

import java.util.*;
import java.util.stream.Collectors;


public class AI extends Player{
    private int depth;
    private Board board;
    private Engine engine;
    private double moveTime;
    private int positionCount;

    public AI(int depth, Side side, Game game){
        super(side, game);
        this.depth = depth;
        this.board = game.getBoard();

        if (depth == game.getEngine().getDepth())
            this.engine = game.getEngine();
        else
            this.engine = new Engine(depth, board);
    }

    public double getMoveTime() {
        return moveTime;
    }

    public int getPositionCount() {
        return positionCount;
    }

    @Override
    public boolean movePiece(String input) throws MoveGeneratorException {
        return movePiece();
    }

    public boolean movePiece() throws MoveGeneratorException {
        long t1 = new Date().getTime();
        Move bestMove = engine.getBestMove();
        long t2 = new Date().getTime();

        moveTime = (t2 - t1)/1000.0;
        positionCount = engine.getPositionCount();

        return board.doMove(bestMove);
    }




}
