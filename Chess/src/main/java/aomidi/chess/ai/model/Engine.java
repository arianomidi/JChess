package aomidi.chess.ai.model;

import aomidi.chess.ai.openingbook.OpeningBookEncoder;
import aomidi.chess.ai.openingbook.OpeningBook;
import aomidi.chess.ai.openingbook.OpeningBookParser;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.*;

import static aomidi.chess.model.Util.*;

public class Engine {

    private static final double MAX_MOVE_TIME = 13.0;
    private static final double MIN_MOVE_TIME = 3.0;

    private List<Double> prev_move_times = new ArrayList<>();

    private double moveTime;
    private int depth;
    private int positionCount;
    private OpeningBook openingBook;
    private boolean out_of_opening_book = false;

    private static final Engine INSTANCE = new Engine(5);

    public Engine(int depth){
        this.depth = depth;
        this.openingBook = OpeningBookEncoder.getSavedOpeningBook();
    }

    // ----------- Getters -------------

    public static Engine getInstance(){
        return INSTANCE;
    }

    public OpeningBook getOpeningBook(){
        return openingBook;
    }

    public int getDepth() {
        return depth;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public double getMoveTime() {
        return moveTime;
    }

    public List<Double> getPrevMoveTimes(){
        return prev_move_times;
    }

    //    public double getPositionEval(Board board){
//        MoveList moves = null;
//        try {
//            moves = MoveGenerator.generateLegalMoves(board);
//            return evaluateBoard(board, moves) / 10;
//        } catch (MoveGeneratorException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }


    // ----------- Setters -------------

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setUseOpeningBook(boolean useOpeningBook){
        this.out_of_opening_book = !useOpeningBook;
    }

    // ----------- Engine -------------

    public Move getBestMove(Board board) {
        if (!out_of_opening_book) {
            Move move = null;

            if (in_opening_book(board))
                move = get_opening_move();

            if (move != null && board.isMoveLegal(move, true)) {
                return move;
            }
        }

        try {
            long t1 = new Date().getTime();
            Move bestMove = miniMaxRoot(board.clone());
            long t2 = new Date().getTime();
            moveTime = (t2 - t1)/1000.0;

//            setBestEngineDepth();

            return bestMove;
        } catch (MoveGeneratorException e) {
            e.printStackTrace();
        }

        return null;
    }

    // todo Figure out a better way of throttling the engine
//    private void setBestEngineDepth(){
//        prev_move_times.add(moveTime);
//
//        if (prev_move_times.size() >= 3){
//            double avg_move_time = (prev_move_times.get(prev_move_times.size() - 1) +
//                    prev_move_times.get(prev_move_times.size() - 2) +
//                    prev_move_times.get(prev_move_times.size() - 3)) / 3;
//
//            if (avg_move_time < MIN_MOVE_TIME){
//                depth++;
//                prev_move_times.clear();
//            } else if (avg_move_time > MAX_MOVE_TIME) {
//                depth--;
//                prev_move_times.clear();
//            }
//        }
//    }

    // ----------- Opening Book -------------

    private Move get_opening_move(){
        Move selected_move = openingBook.getWeightedMove();
        openingBook.doMove(selected_move);
        return selected_move;
    }

    private boolean in_opening_book(Board board){
        openingBook.reset();
        LinkedList<MoveBackup> movesPlayed = board.getBackup();

        for (MoveBackup moveBackup : movesPlayed){
            Move move = moveBackup.getMove();

            this.out_of_opening_book = !openingBook.doMove(move);

            if (out_of_opening_book)
                return false;
        }

        return true;
    }

    private OpeningBook runOpeningBookParser(){
        OpeningBookParser.parseFile();
        return OpeningBookParser.getOpeningBook();
    }

    public String getOpeningName(Board board){
        openingBook.reset();
        LinkedList<MoveBackup> movesPlayed = board.getBackup();

        for (MoveBackup moveBackup : movesPlayed){
            Move move = moveBackup.getMove();

            out_of_opening_book = !openingBook.doMove(move);

            if (out_of_opening_book)
                return openingBook.getOpeningName();
        }

        return openingBook.getOpeningName();
    }

    public void reset(){
        out_of_opening_book = false;
        prev_move_times.clear();
    }

    // --- MinMax --- //

    public Move miniMaxRoot(Board board) throws MoveGeneratorException {
        positionCount = 0;
//        resetMoveEvals();

        MoveList moves = MoveGenerator.generateLegalMoves(board);
        Move bestMoveFound = moves.get(0);
        double bestMove;

        if (board.getSideToMove() == Side.WHITE){
            bestMove = -10000;
            for (Move move : moves) {
                board.doMove(move);
                double value = miniMax(depth - 1, board, -10000, 10000, false);
                board.undoMove();

                if (value >= bestMove) {
                    bestMove = value;
                    bestMoveFound = move;
                }

//                addMove(move, value);
            }
        } else {
            bestMove = 10000;
            for (Move move : moves) {
                board.doMove(move);
                double value = miniMax(depth - 1, board, -10000, 10000, true);
                board.undoMove();

                if (value <= bestMove) {
                    bestMove = value;
                    bestMoveFound = move;
                }

//                addMove(move, value);
            }
        }

        return bestMoveFound;
    }


    private double miniMax(int depth, Board board, double alpha, double beta, boolean isMaximisingPlayer) throws MoveGeneratorException {
        positionCount++;

        if (depth == 0)
            return evaluateBoard(board);

        MoveList moves = MoveGenerator.generateLegalMoves(board);

        if (isMaximisingPlayer){
            double bestMove = -10000;
            for (Move move : moves) {
                board.doMove(move);
                bestMove = Math.max(bestMove, miniMax(depth - 1, board, alpha, beta, false));
                board.undoMove();

                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha)
                    return bestMove;
            }
            return bestMove;
        } else {
            double bestMove = 10000;
            for (Move move : moves) {
                board.doMove(move);
                bestMove = Math.min(bestMove, miniMax(depth - 1, board, alpha, beta, true));
                board.undoMove();

                beta = Math.min(beta, bestMove);
                if (beta <= alpha)
                    return bestMove;
            }
            return bestMove;
        }
    }

    // ----------- Board Evaluation ------------- //

    public static double evaluateBoard(Board board){
        double eval = 0.0;

        // TODO EFFEICENT DRAW CHECKER
//        if (board.isDraw() || board.isStaleMate())
//            return eval;

        Square[] squares = Square.values();

        for (int i = 0; i < 64; i++) {
            Piece piece = board.getPiece(squares[i]);
            if (piece != Piece.NONE) {
                eval += getPieceValue(piece) + getEvalFactor(piece, squares[i]);
            }
        }

        return eval;
    }

    // ----------- Move Map Functions -------------

//    public HashMap<Move, Double> getMoveEvals() {
//        return moveEvals;
//    }
//
//    public void setMoveEvals(HashMap<Move, Double> moveEvals) {
//        this.moveEvals = moveEvals;
//    }
//
//    public void resetMoveEvals() {
//        moveEvals = new HashMap<>();
//    }
//    public void addMove(Move move, Double eval){
//        moveEvals.put(move, eval);
//    }
//
//    private void sortMoveMap(){
//        HashMap<Move, Double> tmp;
//
//        if (side == Side.WHITE) {
//            tmp = moveEvals.entrySet()
//                    .stream()
//                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            Map.Entry::getValue,
//                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//        } else {
//            tmp = moveEvals.entrySet()
//                    .stream()
//                    .sorted(Map.Entry.comparingByValue())
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            Map.Entry::getValue,
//                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//        }
//
//        moveEvals = tmp;
//    }
//
//    public void printLegalMovesEvals() {
//        sortMoveMap();
//        System.out.print("\033[1mLegal moves: \033[0m");
//        moveEvals.forEach((key, value) -> System.out.print(key + ":" + value + ", "));
//        System.out.print("\n");
//    }


}
