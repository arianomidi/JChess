package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.*;

import java.util.HashMap;
import java.util.Map;

import static aomidi.chess.ai.model.Util.getPromotionPiece;
import static aomidi.chess.ai.model.Util.input;

public class User extends Player{
    private boolean firstMove;


    public User(Side side, Game game){
        super(side, game);
        this.firstMove = true;
    }

    // ----------- Setters -------------

    public boolean isFirstMove() {
        return firstMove;
    }

    // ----------- Setters -------------

    public void setFirstMove(boolean isFirstMove) {
        this.firstMove = isFirstMove;
    }

    // ----------- Inputs -------------

    public Map<String, Object> getMoveInput(String input) {
        Board board = getGame().getBoard();
        Map<String, Object> move = new HashMap<>();
        Square new_square;
        Square cur_square;

        input = input.toUpperCase();

        switch (input) {
            case "O-O":
                if (this.getSide() == Side.WHITE)
                    new_square = Square.G1;
                else
                    new_square = Square.G8;
                cur_square = board.getKingSquare(getSide());
                break;
            case "O-O-O":
                if (this.getSide() == Side.WHITE)
                    new_square = Square.C1;
                else
                    new_square = Square.C8;
                cur_square = board.getKingSquare(getSide());
                break;
            default:
                // Get Move to Square and Piece
                new_square = getInputtedSquare(input);
                cur_square = getInputtedCurSquare(input);
        }

        move.put("from", cur_square);
        move.put("to", new_square);
        move.put("string", input);
        return move;
    }

    public Square getInputtedSquare(String string) {
        String input = string.substring(string.length() - 2);
        Square new_square = Square.fromValue(input);

        if (new_square == null)
            throw new NullPointerException("Invalid Move: " + string.substring(string.length() - 2) + " is not a valid square");

        return new_square;
    }

    public Square getInputtedCurSquare(String string) {
        String input = string.substring(0,2);
        Square cur_square = Square.fromValue(input);

        if (cur_square == null)
            throw new NullPointerException("Invalid Move: " + string.substring(string.length() - 2) + " is not a valid square");

        return cur_square;
    }

    // ----------- Checkers -------------

    public boolean isLegalMove(Move move) throws MoveGeneratorException {
        Board board = getGame().getBoard();

        if (board.isMoveLegal(move, true)) {
            for (Move m : MoveGenerator.generateLegalMoves(board)){
                if (move.equals(m))
                    return true;
            }
        }

        throw new NullPointerException("Illegal Move");
    }
    
    // ----------- Action -------------

    public boolean movePiece(String input) throws MoveGeneratorException {
        Map<String, Object> moveInput = getMoveInput(input);
        Board board = getGame().getBoard();
        Move move;

        Square cur_square = (Square) moveInput.get("from");
        Square new_square = (Square) moveInput.get("to");
        Piece piece = board.getPiece(cur_square);

        if (piece.getPieceType() == PieceType.PAWN && (new_square.getRank() == Rank.RANK_8 || new_square.getRank() == Rank.RANK_1))
             move = new Move(cur_square, new_square, getPromotionPiece(input("Enter Promotion: (N/B/R/Q)"), getSide()));
        else
            move = new Move(cur_square, new_square);

        if (isLegalMove(move)) {
            return board.doMove(move);
        } else
            return false;

    }
}
