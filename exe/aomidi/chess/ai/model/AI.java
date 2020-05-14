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
    private HashMap<Move, Double> moveEvals;

    public AI(int depth, Side side, Game game){
        super(side, game);
        this.depth = depth;
        this.board = game.getBoard();

        if (depth == game.getEngine().getDepth())
            this.engine = game.getEngine();
        else
            this.engine = new Engine(depth, board);
    }

    @Override
    public boolean movePiece(String input) throws MoveGeneratorException {
        return movePiece();
    }

    public boolean movePiece() throws MoveGeneratorException {
        long t1 = new Date().getTime();
        Move bestMove = engine.miniMaxRoot();
        long t2 = new Date().getTime();

        double timeTaken = (t2 - t1)/1000.0;

        board.doMove(bestMove);
        printBoard(board);

        engine.printLegalMovesEvals();
        System.out.println(bold("Move Played: ") + bestMove);
        System.out.format("Time Taken: %.3fs, Positions Searched: %d, Pos/s: %.3f\n", timeTaken, engine.getPositionCount(), (engine.getPositionCount()/timeTaken));

        return true;
    }




}
