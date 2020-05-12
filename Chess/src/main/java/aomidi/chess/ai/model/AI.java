package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.concurrent.ThreadLocalRandom;


public class AI extends Player{
    public AI(Side side, Game game){
        super(side, game);
    }

    @Override
    public boolean movePiece(String input) throws MoveGeneratorException {
        return movePiece();
    }

    public boolean movePiece() throws MoveGeneratorException {
        Board board = getGame().getBoard();
        Move bestMove = selectMove(board);

        board.doMove(bestMove);

        System.out.println("Move Picked: " + bestMove + ", Eval: " + evaluateBoard(board));

        return true;
    }

    public Move selectMove(Board board) throws MoveGeneratorException {
        int minEval = evaluateBoard(board);
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        Move bestMove = moves.get(0);

        for (Move move : moves){
            board.doMove(move);

            int newEval = evaluateBoard(board);
            if (newEval < minEval){
                minEval = newEval;
                bestMove = move;
            }

            board.undoMove();
        }

        return bestMove;
    }




}
