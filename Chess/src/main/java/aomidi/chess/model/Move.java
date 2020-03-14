package aomidi.chess.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Move {
    private Game game;
    private Board board;
    private Util.Color player;
    private Map<String, Object> move;

    // ----------- Constructors -------------
    public Move(Map<String, Object> move, Util.Color player, Game game){
        this.game = game;
        this.board = game.getBoard();
        this.player = player;
        this.move = move;
    }

    // ----------- Getters -------------

    public Util.Color getPlayerTurn() { return this.player; }

    public Map<String, Object> getMove() {
        return move;
    }

    // ----------- Setters -------------

    public void setPlayerTurn(Util.Color color){ this.player = color; }

    public void setMove(Map<String, Object> move) {
        this.move = move;
    }

    // ----------- Checkers -------------

    public boolean validMove(Piece piece, Tile new_tile){
        // Check if king is not moving into a attacked tile
        if (piece instanceof King){
            boolean move_into_check = false;
            move_into_check = board.isTileAttacked(new_tile, game.getCurPlayer());

            if (move_into_check)
                if (new_tile.hasPiece())
                    throw new IllegalArgumentException("Invalid Move: King can't capture a defended piece");
                else
                    throw new IllegalArgumentException("Invalid Move: King can't move into check");
        }
        // Check if king is checked and if move gets king out of check
        else if (game.getCurPlayer().isKingChecked()) {
            boolean move_gets_out_of_check = false;
            // Piece has to block or take attacking piece
            ArrayList<ArrayList<Tile>> blocking_tiles = board.getTilesBetweenKingCheckingPiece(game.getCurPlayer());

            if (blocking_tiles.size() == 1) {
                // Loop through all tiles to see if selected tile is one of the blocking tiles
                for (Tile t : blocking_tiles.get(0)) {
                    if (t.equals(new_tile)){
                        move_gets_out_of_check = true;
                        break;
                    }
                }
            }

            if (!move_gets_out_of_check)
                throw new IllegalArgumentException("Invalid Move: King is Checked");
        }

        // If there is no piece between cur_piece and new tile then it can be moved
        boolean isPieceBlocking = board.hasPieceBetweenTiles(piece.getPosition(), new_tile);

        if (isPieceBlocking)
            throw new IllegalArgumentException(piece.toSimpleString() + " is blocked from getting to " + new_tile);

        // Dont return all values as if they were false then an exception would be thrown
        return !isPieceBlocking;
    }

    public boolean validAttack(Piece piece, Tile new_tile){
        Piece pieceOnTile = board.getPieceAt(new_tile.getX(), new_tile.getY());
        boolean canReachTile = validMove(piece, new_tile);

        // If there is no piece between cur_piece and new_tile and the piece on new_tile is of opposite color then call attack
        if (canReachTile) {
            if (pieceOnTile.getColor() == piece.getColor()) {
                throw new IllegalArgumentException("There is already a "+ piece.getColor() + " piece on " + new_tile);
            } else {
                return piece.attack(new_tile);
            }
        } else {
            return false;
        }
    }

    // ----------- Action -------------

    public boolean move(){
        Piece piece = (Piece) move.get("piece");
        Tile new_tile = (Tile) move.get("tile");
        move.put("old_tile", ((Piece) move.get("piece")).getPosition());
        boolean hasPieceOnTile = board.hasPieceAt(new_tile.getX(), new_tile.getY());

        // Test attack if there's a piece on the tile, else test moveTo
        if (hasPieceOnTile){
            if (validAttack(piece, new_tile)){
                addToMoveList();
                return true;
            } else {
                return false;
            }
        } else {
            if (validMove(piece, new_tile)) {
                boolean moved = piece.moveTo(new_tile);

                if (moved)
                    addToMoveList();

                return moved;
            } else {
                return false;
            }
        }
    }

    public boolean moveBack(){
        Piece piece = (Piece) move.get("piece");
        Tile old_tile = (Tile) move.get("old_tile");

        return piece.moveTo(old_tile, true);
    }

    // ----------- Other -------------

    public void addToMoveList(){
        game.addMove(this);
    }

    public String toString(){
        return (String) move.get("string");
    }

}
