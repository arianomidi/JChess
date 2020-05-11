package aomidi.chess.ai.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aomidi.chess.ai.model.Util.*;

public class Move {
    private Board currentBoard;
    private Board newBoard;
    //private Map<String, Object> move;
    private String move;
    private Piece piece;
    private Tile newTile;

    // ----------- Constructors -------------

    // input should be in form of '"curTile"-"newTile"' ex. a4-b6
    public Move(String input, Board board) {
        this.currentBoard = board;
        this.move = input;

        this.piece = currentBoard.getPieceAt(letterToInt(input.substring(0,0)), Integer.valueOf(input.substring(1,1)));
        this.newTile = currentBoard.getTileAt(letterToInt(input.substring(3,3)), Integer.valueOf(input.substring(4,4)));
    }

    public Move(Piece piece, Tile tile, Board board) {
        this.currentBoard = board;

        this.piece = piece;
        this.newTile = tile;

        this.move = piece.getPosition().toString() + "-" + tile.toString();
    }

    // ----------- Inputs Checks -------------

    // Return false if pieces can move to same square
    public boolean hasAmbiguousMoves(List<Piece> pieces, String tile) throws IllegalArgumentException {
        String error = "";

        // Check if pieces is empty
        if (pieces.size() == 0)
            throw new IllegalArgumentException("Invalid Move: " + tile + " cannot be reached");
        else if (pieces.size() == 1)
            return false;
        else {
            // TO DO : Handle multiple ambiguous types
            for (int i = 1; i < 2; i++) {
                if (pieces.get(i).getPosition().getX() == pieces.get(i - 1).getPosition().getX()) {
                    error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i).getPosition().getY() + tile + " or ";
                    error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i - 1).getPosition().getY() + tile;
                } else if (pieces.get(i).getPosition().getY() == pieces.get(i - 1).getPosition().getY()) {
                    error += getTypeLetter(pieces.get(i).getPieceType()) + intToLetter(pieces.get(i).getPosition().getX()).toLowerCase() + tile + " or ";
                    error += getTypeLetter(pieces.get(i).getPieceType()) + intToLetter(pieces.get(i - 1).getPosition().getX()).toLowerCase() + tile;
                } else if (pieces.get(0) instanceof Queen){
                    if (pieces.get(i).getPosition().getX() == pieces.get(i - 1).getPosition().getX()) {
                        error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i).getPosition().getY() + tile + " or ";
                        error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i - 1).getPosition().getY() + tile;
                    } else {
                        error += "Q" + intToLetter(pieces.get(i).getPosition().getX()).toLowerCase() + tile + " or ";
                        error += "Q" + intToLetter(pieces.get(i - 1).getPosition().getX()).toLowerCase() + tile;
                    }
                }
            }
        }

        if (error.compareTo("") == 0)
            return false;
        else
            throw new IllegalArgumentException("Ambiguous Move: Specify with " + error);
    }

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

    // ----------- Action -------------

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

    // ----------- Other -------------

//    public String toString() {
//        return (String) move.get("string");
//    }

}
