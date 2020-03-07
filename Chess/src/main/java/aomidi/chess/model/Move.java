package aomidi.chess.model;

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
        // If there is no piece between cur_piece and new tile then it can be moved
        boolean isPieceBlocking = board.hasPieceBetweenTiles(piece.getPosition(), new_tile);

        if (isPieceBlocking)
            throw new IllegalArgumentException(piece.toSimpleString() + " is blocked from getting to " + new_tile);

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
        boolean hasPieceOnTile = board.hasPieceAt(new_tile.getX(), new_tile.getY());

        // Test attack if there's a piece on the tile, else test moveTo
        if (hasPieceOnTile){
            if (validAttack(piece, new_tile)){
                return true;
            } else {
                return false;
            }
        } else {
            if (validMove(piece, new_tile)) {
                return piece.moveTo(new_tile);
            } else {
                return false;
            }
        }
    }

    public boolean move(Piece piece, Tile tile){
        Map<String, Object> new_move = new HashMap<>();
        new_move.put("piece", piece);
        new_move.put("tile", tile);
        setMove(new_move);

        return move();
    }

}
