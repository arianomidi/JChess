package aomidi.chess.ai.model;

import aomidi.chess.ai.openingbook.MoveNode;
import aomidi.chess.ai.openingbook.OpeningBook;
import aomidi.chess.ai.openingbook.OpeningBookParser;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.*;
import java.util.stream.Collectors;

import static aomidi.chess.ai.model.Util.*;

public class Engine {
    private HashMap<Move, Double> moveEvals;
    private int depth;
    private Side side;
    private int positionCount;
    private OpeningBook openingBook;
    private boolean out_of_opening_book = false;

    public Engine(int depth){
        this.depth = depth;

        this.openingBook = runOpeningBookParser();
    }

    // ----------- Getters -------------

    public int getPositionCount() {
        return positionCount;
    }

    public int getDepth() {
        return depth;
    }

    public Side getSide() {
        return side;
    }

    public HashMap<Move, Double> getMoveEvals() {
        return moveEvals;
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

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }

    public void setMoveEvals(HashMap<Move, Double> moveEvals) {
        this.moveEvals = moveEvals;
    }

    public void resetMoveEvals() {
        moveEvals = new HashMap<>();
    }

    // ----------- Engine -------------

    public Move getBestMove(Board board) {
        if (!out_of_opening_book) {
            Move move = null;

            if (in_opening_book(board))
                move = get_opening_move();

            if (move != null && board.isMoveLegal(move, true)) {
                System.out.println("OPENING THEORY");
                return move;
            }
        }

        try {
            return miniMaxRoot(board.clone());
        } catch (MoveGeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Move miniMaxRoot(Board board) throws MoveGeneratorException {
        positionCount = 0;
        side = board.getSideToMove();
        resetMoveEvals();

        MoveList moves = MoveGenerator.generateLegalMoves(board);
        Move bestMoveFound = moves.get(0);
        double bestMove;

        if (side == Side.WHITE){
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

//                addMove(move, value);
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

    public void addMove(Move move, Double eval){
        moveEvals.put(move, eval);
    }

    private void sortMoveMap(){
        HashMap<Move, Double> tmp;

        if (side == Side.WHITE) {
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

    // ----------- Opening Book -------------

    public Move get_opening_move(){
        Move selected_move = openingBook.getWeightedMove();
        ArrayList<MoveNode> known_moves = openingBook.getCur_move().getMovesList();

        if (known_moves.isEmpty())
            return null;

        for (MoveNode moveNode : known_moves){
            System.out.print(moveNode.getMove().toString() + "-" + moveNode.getWeight() + ", ");
        }

        System.out.println(": Selected: " + selected_move + " - " + openingBook.getOpeningName());

        openingBook.doMove(selected_move);

        return selected_move;
    }

    public boolean in_opening_book(Board board){
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

    public OpeningBook runOpeningBookParser(){
        OpeningBookParser.parseFile();
        return OpeningBookParser.getOpeningBook();
    }

}
