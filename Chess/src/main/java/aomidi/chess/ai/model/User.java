package aomidi.chess.ai.model;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aomidi.chess.model.Util.isFile;
import static aomidi.chess.model.Util.letterToInt;

public class User extends Player{
    
    public User(Side side, Game game){
        super(side, game);
    }

    // ----------- Inputs -------------

    public Map<String, Object> getMoveInput(String input) {
        Board board = getGame().getBoard();
        Map<String, Object> move = new HashMap<>();
        Square new_square;
        Square cur_square;
        Piece piece;

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

//                // Throw Errors before testing
//                if (piece.getColor() != player.getColor())
//                    throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");
        }

//        move.put("piece", piece);
        move.put("from", cur_square);
        move.put("to", new_square);
        move.put("string", input);
        return move;
    }

//    public aomidi.chess.model.Piece getInputtedPiece(String string, Square new_square){
//        aomidi.chess.model.Piece piece;
//
//        // Pawn Case: string length is 2 or 3 containing x
//        if (isFile(string.charAt(0)) && ((string.length() == 2) || (string.length() == 4 && string.contains("x")))) {
//            int dir = -1;
//            if (getSide() == Side.WHITE)
//                dir = 1;
//
//            // If there's file inputted then test attack else move
//            if (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x') {
//                if (board.getSquareAt(letterToInt(String.valueOf(string.charAt(0))), new_square.getY() - Integer.signum(dir)).hasPiece()) {
//                    piece = board.getSquareAt(letterToInt(String.valueOf(string.charAt(0))), new_square.getY() - Integer.signum(dir)).getPiece();
//                    return piece;
//                }
//            } else {
//                // Check if piece 1 or 2 behind/ahead of tile has a Pawn
//                if (tileHasPawn(new_square.getX(), new_square.getY() - Integer.signum(dir))) {
//                    return board.getSquareAt(new_square.getX(), new_square.getY() - Integer.signum(dir)).getPiece();
//                } else if (tileHasPawn(new_square.getX(), new_square.getY() - 2 * Integer.signum(dir))) {
//                    return board.getSquareAt(new_square.getX(), new_square.getY() - 2 * Integer.signum(dir)).getPiece();
//                }
//            }
//        }
//        // Knight Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'N') {
//            List<aomidi.chess.model.Piece> valid_knights;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Knights
//                valid_knights = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Knight, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_knights, string.substring(string.length() - 2));
//                return valid_knights.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_knights = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Knight, specified_char, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_knights, string.substring(string.length() - 2));
//                return valid_knights.get(0);
//            }
//        }
//        // Bishop Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'B') {
//            List<aomidi.chess.model.Piece> valid_bishops;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_bishops = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Bishop, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_bishops, string.substring(string.length() - 2));
//                return valid_bishops.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_bishops = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Bishop, specified_char, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_bishops, string.substring(string.length() - 2));
//                return valid_bishops.get(0);
//            }
//        }
//        // Rook Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'R') {
//            List<aomidi.chess.model.Piece> valid_rooks;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_rooks = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Rook, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_rooks, string.substring(string.length() - 2));
//                return valid_rooks.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_rooks = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Rook, specified_char, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_rooks, string.substring(string.length() - 2));
//                return valid_rooks.get(0);
//            }
//        }
//        // Queen Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'Q') {
//            List<aomidi.chess.model.Piece> valid_queen;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_queen = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Queen, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_queen, string.substring(string.length() - 2));
//                return valid_queen.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_queen = board.getPiecesOfTypeCanMoveTo(aomidi.chess.model.Util.PieceType.Queen, specified_char, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_queen, string.substring(string.length() - 2));
//                return valid_queen.get(0);
//            }
//        }
//        // King Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'K') {
//            List<Piece> valid_king;
//
//            // Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_king = board.getPiecesOfTypeCanMoveTo(Util.PieceType.King, player.getColor(), new_square);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_king, string.substring(string.length() - 2));
//                return valid_king.get(0);
//            }
//        }
//
//        throw new IllegalArgumentException("Invalid Input");
//    }

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

        Square cur_square = (Square) moveInput.get("from");
        Square new_square = (Square) moveInput.get("to");

        Move move = new Move(cur_square, new_square);

        if (isLegalMove(move)) {
            board.doMove(move);
            System.out.println("Eval: " + Util.evaluateBoard(board) + "\n");
            return true;
        } else
            return false;

    }
}
