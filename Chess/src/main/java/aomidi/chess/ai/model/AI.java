package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.*;
import java.util.stream.Collectors;


public class AI extends Player{
    static int positionCount;
    int depth;
    static HashMap<Move, Double> moveEvals;

    public AI(int depth, Side side, Game game){
        super(side, game);
        this.depth = depth;
    }

    @Override
    public boolean movePiece(String input) throws MoveGeneratorException {
        return movePiece();
    }

    public boolean movePiece() throws MoveGeneratorException {
        positionCount = 0;
        Board board = getGame().getBoard();
        moveEvals = new HashMap<>();

        long t1 = new Date().getTime();
        Move bestMove = miniMaxRoot(depth, board, getSide() == Side.WHITE);
        long t2 = new Date().getTime();

        double timeTaken = (t2 - t1)/1000.0;

        //printLegalMovesEvals();
        board.doMove(bestMove);
        System.out.println(board);

        System.out.println(bold("Move Played: ") + bestMove);
        System.out.format("Time Taken: %.3fs, Positions Searched: %d, Pos/s: %.3f\n", timeTaken, positionCount, (positionCount/timeTaken));

        return true;
    }

    public static Move miniMaxRoot(int depth, Board board, boolean isMaximisingPlayer) throws MoveGeneratorException {
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        Move bestMoveFound = moves.get(0);
        double bestMove;

        if (isMaximisingPlayer){
            bestMove = -10000;
            for (int i = 0; i < moves.size(); i++){
                Move move = moves.get(i);

                board.doMove(move);
                double value = miniMax(depth - 1, board, -10000, 10000, false);
                board.undoMove();

                if (value >= bestMove) {
                    bestMove = value;
                    bestMoveFound = move;
                }

                addMove(move, value);
            }
        } else {
            bestMove = 10000;
            for (int i = 0; i < moves.size(); i++){
                Move move = moves.get(i);

                board.doMove(move);
                double value = miniMax(depth - 1, board, -10000, 10000, true);
                board.undoMove();

                if (value <= bestMove) {
                    bestMove = value;
                    bestMoveFound = move;
                }

                addMove(move, value);
            }
        }

        return bestMoveFound;
    }

    public static double miniMax(int depth, Board board, double alpha, double beta, boolean isMaximisingPlayer) throws MoveGeneratorException {
        positionCount++;
        if (depth == 0){
            return evaluateBoard(board);
        }


        MoveList moves = MoveGenerator.generateLegalMoves(board);

        if (isMaximisingPlayer){
            double bestMove = -10000;
            for (int i = 0; i < moves.size(); i++){
                board.doMove(moves.get(i));
                bestMove = Math.max(bestMove, miniMax(depth - 1, board, alpha, beta, false));
                board.undoMove();

                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha)
                    return bestMove;
            }
            return bestMove;
        } else {
            double bestMove = 10000;
            for (int i = 0; i < moves.size(); i++){
                board.doMove(moves.get(i));
                bestMove = Math.min(bestMove, miniMax(depth - 1, board, alpha, beta, true));
                board.undoMove();

                beta = Math.min(beta, bestMove);
                if (beta <= alpha)
                    return bestMove;
            }
            return bestMove;
        }
    }

    public static void addMove(Move move, Double eval){
        moveEvals.put(move, eval);
    }

    private void sortMoveMap(){
        HashMap<Move, Double> tmp;

        if (getSide() == Side.WHITE) {
            tmp = moveEvals.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        } else {
            tmp = moveEvals.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        }

        moveEvals = tmp;
    }

    public void printLegalMovesEvals() {
        sortMoveMap();
        System.out.print("\033[1mLegal moves: \033[0m");
        moveEvals.forEach((key, value) -> System.out.print(key + ":" + value + ", "));
        System.out.print("\n");
    }


}
