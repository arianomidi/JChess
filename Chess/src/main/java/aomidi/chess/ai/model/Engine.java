package aomidi.chess.ai.model;

import aomidi.chess.openingbook.MoveNode;
import aomidi.chess.openingbook.OpeningBook;
import aomidi.chess.openingbook.OpeningBookParser;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.*;
import java.util.stream.Collectors;

import static aomidi.chess.ai.model.Util.evaluateBoard;

public class Engine {
    private HashMap<Move, Double> moveEvals;
    private int depth;
    private Board board;
    private Side side;
    private int positionCount;
    private OpeningBook openingBook;
    private boolean out_of_opening_book = false;

    public Engine(int depth, Board board){
        this.board = board;
        this.depth = depth;

        this.openingBook = runOpeningBookParser();
    }

    // ----------- Getters -------------

    public int getPositionCount() {
        return positionCount;
    }

    public Board getBoard() {
        return board;
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

    public double getPositionEval() throws MoveGeneratorException {
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        return evaluateBoard(board, moves) / 10;
    }


    // ----------- Setters -------------

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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

    public Move getBestMove() throws MoveGeneratorException {
        if (!out_of_opening_book) {
            Move move = null;

            if (in_opening_book())
                move = get_opening_move();

            if (move != null && board.isMoveLegal(move, true))
                return move;
        }

        return miniMaxRoot();
    }


    public Move miniMaxRoot() throws MoveGeneratorException {
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

    public double miniMax(int depth, Board board, double alpha, double beta, boolean isMaximisingPlayer) throws MoveGeneratorException {
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
        ArrayList<MoveNode> known_moves = openingBook.getCur_move().getMovesList();

        if (known_moves.isEmpty())
            return null;

        for (MoveNode moveNode : known_moves){
            System.out.print(moveNode.getMove() + ", ");
        }

        Random random = new Random();
        MoveNode randomElement = known_moves.get(random.nextInt(known_moves.size()));

        System.out.println(": Selected: " + randomElement.getMove());

        openingBook.doMove(randomElement);

        return new Move(randomElement.getMove());
    }

    public boolean in_opening_book(){
        LinkedList<MoveBackup> moves = board.getBackup();

        // Last move played by User
        Move lastMove = moves.getLast().getMove();
//        System.out.println("\"" + (lastMove.getFrom().value() + lastMove.getTo().value()).toLowerCase() + "\"");

        String move = (lastMove.getFrom().value() + lastMove.getTo().value()).toLowerCase();

        this.out_of_opening_book = !openingBook.doMove(move);

        return !out_of_opening_book;
    }

    public OpeningBook runOpeningBookParser(){
        OpeningBookParser.parseFile();
        return OpeningBookParser.getOpeningBook();
    }

}
