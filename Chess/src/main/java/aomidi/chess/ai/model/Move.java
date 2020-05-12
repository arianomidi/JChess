package aomidi.chess.ai.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aomidi.chess.ai.model.Util.*;

public class Move {
//    private Game game;
//    private Board board;
//    private Player player;
//    private Map<String, Object> move;
//
//    // ----------- Constructors -------------
//
//    public Move(String input, Player player, Game game) {
//        this.game = game;
//        this.board = game.getBoard();
//        this.player = player;
//        this.move = getMoveInput(input);
//    }
//
//    public Move(Piece piece, Tile tile, Player player, Game game) {
//        this.game = game;
//        this.board = game.getBoard();
//        this.player = player;
//
//        this.move = new HashMap<>();
//        move.put("piece", piece);
//        move.put("tile", tile);
//        this.move.put("string", getTypeLetter(piece.getPieceType()) + tile);
//    }
//
//    // ----------- Inputs -------------
//
//    public Map<String, Object> getMoveInput(String input) {
//        Map<String, Object> move = new HashMap<>();
//        Tile move_tile;
//        Piece piece;
//
//        switch (input.toUpperCase()) {
//            case "O-O":
//                if (player.getColor() == Util.Color.White)
//                    move_tile = board.getTileAt(7, 1);
//                else
//                    move_tile = board.getTileAt(7, 8);
//                piece = player.getKing();
//                break;
//            case "O-O-O":
//                if (player.getColor() == Util.Color.White)
//                    move_tile = board.getTileAt(3, 1);
//                else
//                    move_tile = board.getTileAt(3, 8);
//                piece = player.getKing();
//                break;
//            default:
//                // Get Move to Tile and Piece
//                move_tile = getInputtedTile(input);
//                piece = getInputtedPiece(input, move_tile);
//
//                // Throw Errors before testing
//                if (piece.getColor() != player.getColor())
//                    throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");
//        }
//
//        move.put("piece", piece);
//        move.put("tile", move_tile);
//        move.put("string", input);
//        return move;
//    }
//
//    public Piece getInputtedPiece(String string, Tile move_tile){
//        Piece piece;
//
//        // Pawn Case: string length is 2 or 3 containing x
//        if (isFile(string.charAt(0)) && ((string.length() == 2) || (string.length() == 4 && string.contains("x")))) {
//            int dir = -1;
//            if (player.getColor() == Color.White)
//                dir = 1;
//
//            // If there's file inputted then test attack else move
//            if (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x') {
//                if (board.getTileAt(letterToInt(String.valueOf(string.charAt(0))), move_tile.getY() - Integer.signum(dir)).hasPiece()) {
//                    piece = board.getTileAt(letterToInt(String.valueOf(string.charAt(0))), move_tile.getY() - Integer.signum(dir)).getPiece();
//                    return piece;
//                }
//            } else {
//                // Check if piece 1 or 2 behind/ahead of tile has a Pawn
//                if (tileHasPawn(move_tile.getX(), move_tile.getY() - Integer.signum(dir))) {
//                    return board.getTileAt(move_tile.getX(), move_tile.getY() - Integer.signum(dir)).getPiece();
//                } else if (tileHasPawn(move_tile.getX(), move_tile.getY() - 2 * Integer.signum(dir))) {
//                    return board.getTileAt(move_tile.getX(), move_tile.getY() - 2 * Integer.signum(dir)).getPiece();
//                }
//            }
//        }
//        // Knight Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'N') {
//            List<Piece> valid_knights;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Knights
//                valid_knights = board.getPiecesOfTypeCanMoveTo(PieceType.Knight, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_knights, string.substring(string.length() - 2));
//                return valid_knights.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_knights = board.getPiecesOfTypeCanMoveTo(PieceType.Knight, specified_char, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_knights, string.substring(string.length() - 2));
//                return valid_knights.get(0);
//            }
//        }
//        // Bishop Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'B') {
//            List<Piece> valid_bishops;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_bishops = board.getPiecesOfTypeCanMoveTo(PieceType.Bishop, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_bishops, string.substring(string.length() - 2));
//                return valid_bishops.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_bishops = board.getPiecesOfTypeCanMoveTo(PieceType.Bishop, specified_char, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_bishops, string.substring(string.length() - 2));
//                return valid_bishops.get(0);
//            }
//        }
//        // Rook Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'R') {
//            List<Piece> valid_rooks;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_rooks = board.getPiecesOfTypeCanMoveTo(PieceType.Rook, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_rooks, string.substring(string.length() - 2));
//                return valid_rooks.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_rooks = board.getPiecesOfTypeCanMoveTo(PieceType.Rook, specified_char, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_rooks, string.substring(string.length() - 2));
//                return valid_rooks.get(0);
//            }
//        }
//        // Queen Case:
//        if (Character.toUpperCase(string.charAt(0)) == 'Q') {
//            List<Piece> valid_queen;
//
//            // Simple Move or Take
//            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')) {
//                // Valid Bishops
//                valid_queen = board.getPiecesOfTypeCanMoveTo(PieceType.Queen, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_queen, string.substring(string.length() - 2));
//                return valid_queen.get(0);
//            }
//            // Specified Move or Take
//            else if (string.length() == 4 || (string.length() == 5 && Character.toLowerCase(string.charAt(2)) == 'x')) {
//                Character specified_char = Character.toLowerCase(string.charAt(1));
//                valid_queen = board.getPiecesOfTypeCanMoveTo(PieceType.Queen, specified_char, player.getColor(), move_tile);
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
//                valid_king = board.getPiecesOfTypeCanMoveTo(PieceType.King, player.getColor(), move_tile);
//
//                // Check for Ambiguous Moves
//                hasAmbiguousMoves(valid_king, string.substring(string.length() - 2));
//                return valid_king.get(0);
//            }
//        }
//
//        throw new IllegalArgumentException("Invalid Input");
//    }
//
//    public Tile getInputtedTile(String string) {
//        Tile move_tile;
//        int start_index = string.length() - 2;
//
//        int new_x = letterToInt(String.valueOf(string.charAt(start_index)));
//        int new_y = string.charAt(start_index + 1) - '0';
//        move_tile = board.getTileAt(new_x, new_y);
//
//        if (move_tile == null) {
//            throw new NullPointerException("Invalid Move: " + string.substring(string.length() - 2) + " is not a valid square");
//        } else if (move_tile.hasPiece() && move_tile.getPiece().getColor() == player.getColor()) {
//            throw new NullPointerException("Invalid Move: " + string.substring(string.length() - 2) + " already has a " + player.getColor() + " piece");
//        } else {
//            return move_tile;
//        }
//    }
//
//    // ----------- Inputs Checks -------------
//
//    // Return false if pieces can move to same square
//    public boolean hasAmbiguousMoves(List<Piece> pieces, String tile) throws IllegalArgumentException {
//        String error = "";
//
//        // Check if pieces is empty
//        if (pieces.size() == 0)
//            throw new IllegalArgumentException("Invalid Move: " + tile + " cannot be reached");
//        else if (pieces.size() == 1)
//            return false;
//        else {
//            // TO DO : Handle multiple ambiguous types
//            for (int i = 1; i < 2; i++) {
//                if (pieces.get(i).getPosition().getX() == pieces.get(i - 1).getPosition().getX()) {
//                    error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i).getPosition().getY() + tile + " or ";
//                    error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i - 1).getPosition().getY() + tile;
//                } else if (pieces.get(i).getPosition().getY() == pieces.get(i - 1).getPosition().getY()) {
//                    error += getTypeLetter(pieces.get(i).getPieceType()) + intToLetter(pieces.get(i).getPosition().getX()).toLowerCase() + tile + " or ";
//                    error += getTypeLetter(pieces.get(i).getPieceType()) + intToLetter(pieces.get(i - 1).getPosition().getX()).toLowerCase() + tile;
//                } else if (pieces.get(0) instanceof Queen){
//                    if (pieces.get(i).getPosition().getX() == pieces.get(i - 1).getPosition().getX()) {
//                        error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i).getPosition().getY() + tile + " or ";
//                        error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i - 1).getPosition().getY() + tile;
//                    } else {
//                        error += "Q" + intToLetter(pieces.get(i).getPosition().getX()).toLowerCase() + tile + " or ";
//                        error += "Q" + intToLetter(pieces.get(i - 1).getPosition().getX()).toLowerCase() + tile;
//                    }
//                }
//            }
//        }
//
//        if (error.compareTo("") == 0)
//            return false;
//        else
//            throw new IllegalArgumentException("Ambiguous Move: Specify with " + error);
//    }
//
//    public boolean tileHasPawn(int x, int y) {
//        // Check board boundries
//        if (1 > x || x > 8 || 1 > y || y > 8) {
//            return false;
//        }
//
//        // Check if it's a its a pawn of the same color
//        if (board.getTileAt(x, y).hasPiece()) {
//            Piece piece = board.getTileAt(x, y).getPiece();
//
//            if (piece.getColor() != player.getColor())
//                throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");
//
//            return piece.getPieceType() == PieceType.Pawn;
//        }
//
//        return false;
//    }
//
//    // ----------- Getters -------------
//
//    public Map<String, Object> getMove() {
//        return move;
//    }
//
//    // ----------- Checkers -------------
//
//    public boolean validMove(Piece piece, Tile new_tile) {
//        // Check if king is checked and if move blocks check (piece != king)
//        if (player.isUnderCheck() && !(piece instanceof King)) {
//            boolean move_gets_out_of_check = false;
//            // Piece has to block or take attacking piece
//            ArrayList<ArrayList<Tile>> blocking_tiles = board.getTilesBetweenKingCheckingPiece(player);
//
//            if (blocking_tiles.size() == 1) {
//                // Loop through all tiles to see if selected tile is one of the blocking tiles
//                for (Tile t : blocking_tiles.get(0)) {
//                    if (t.equals(new_tile)) {
//                        move_gets_out_of_check = true;
//                        break;
//                    }
//                }
//            }
//
//            if (!move_gets_out_of_check)
//                throw new IllegalArgumentException("Invalid Move: King is Checked");
//        }
//
//        // If there is no piece between cur_piece and new tile then it can be moved
//        boolean isPieceBlocking = board.hasPieceBetweenTiles(piece.getPosition(), new_tile);
//
//        if (isPieceBlocking)
//            throw new IllegalArgumentException(piece.toSimpleString() + " is blocked from getting to " + new_tile);
//
//        // Dont return all values as if they were false then an exception would be thrown
//        return true;
//    }
//
//    public boolean validAttack(Piece piece, Tile new_tile) {
//        Piece pieceOnTile = board.getPieceAt(new_tile.getX(), new_tile.getY());
//        boolean canReachTile = validMove(piece, new_tile);
//
//        // If there is no piece between cur_piece and new_tile and the piece on new_tile is of opposite color then call attack
//        if (canReachTile) {
//            if (pieceOnTile.getColor() == piece.getColor())
//                throw new IllegalArgumentException("There is already a " + piece.getColor() + " piece on " + new_tile);
//            else
//                return true;
//        } else {
//            return false;
//        }
//    }
//
//    // ----------- Action -------------
//
//    public boolean move() throws CloneNotSupportedException {
//        Piece piece = (Piece) move.get("piece");
//        Tile new_tile = (Tile) move.get("tile");
//        move.put("old_tile", ((Piece) move.get("piece")).getPosition());
//        boolean hasPieceOnTile = board.hasPieceAt(new_tile.getX(), new_tile.getY());
//
//        // Test attack if there's a piece on the tile, else test moveTo
//        if (hasPieceOnTile) {
//            if (validAttack(piece, new_tile)) {
//                move.put("attacked_piece", new_tile.getPiece());
//                return piece.attack(new_tile);
//            } else {
//                return false;
//            }
//        } else {
//            if (validMove(piece, new_tile))
//                return piece.moveTo(new_tile);
//            else
//                return false;
//        }
//    }
//
//    public void moveBack() {
//        Piece piece = (Piece) move.get("piece");
//        Tile old_tile = (Tile) move.get("old_tile");
//
//        piece.moveTo(old_tile, true);
//
//        if (move.containsKey("attacked_piece")) {
//            Tile attacked_tile = (Tile) move.get("tile");
//            Piece attacked_piece = (Piece) move.get("attacked_piece");
//
//            board.addPieceAt(attacked_piece, attacked_tile);
//        }
//    }
//
//    // ----------- Other -------------
//
//    public String toString() {
//        return (String) move.get("string");
//    }

}
