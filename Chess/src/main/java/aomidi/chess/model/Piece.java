package aomidi.chess.model;

import aomidi.chess.model.Util.Color;
import aomidi.chess.model.Util.PieceType;

import static aomidi.chess.model.Util.*;

public abstract class Piece {
    private Tile tile;
    private Color color;
    private Board board;
    private boolean inPlay;

    // ----------- Constructors -------------

    public Piece(Tile tile, Color color, Board board) {
        this.tile = tile;
        this.color = color;
        this.board = board;
        this.inPlay = true;

        if (!this.board.addPieceAt(this, tile)) {
            throw new IllegalArgumentException("Already a Piece on tile " + tile);
        }
    }

    // ----------- Getters -------------

    public Tile getPosition(){ return this.tile; }

    public Color getColor(){ return this.color; }

    public Board getBoard(){ return this.board; }

    public boolean isInPlay() { return this.inPlay; }

    public abstract PieceType getPieceType();

    // ----------- Actions -------------

    // If it's a valid move then move piece to new tile and remove from current tile
    public boolean moveTo(Tile tile){
        if (validMove(tile)){
            this.tile.removePiece();

            // Set tile to new tile
            this.tile = tile;
            this.tile.setPiece(this);

            return true;
        } else {
            throw new IllegalArgumentException(this.toSimpleString() + " cant move to " + tile);
        }
    }

    // Overide moveTo for Pawn Attack and Castling
    public boolean moveTo(Tile tile, boolean overide){
        if (overide){
            this.tile.removePiece();

            // Set tile to new tile
            this.tile = tile;
            this.tile.setPiece(this);

            return true;
        }
        return false;
    }

    // Attack only if its a valid attack
    public boolean attack(Tile tile){
        if (validAttack(tile)){
            tile.getPiece().delete();
            // Move piece to new tile
            return this.moveTo(tile, true);
        } else {
            //throw new IllegalArgumentException("Piece cant move to tile: " + tile);
            return false;
        }
    }

    // ----------- Checkers -------------

    // Valid Attack = Valid Move unless overridden
    public boolean validAttack(Tile tile){
        return validMove(tile);
    }

    public abstract boolean validMove(Tile tile);

    // Equals
    public boolean equals(Object obj) {
        if ( obj instanceof Piece && ((Piece) obj).getPosition().equals(this.tile) && ((Piece) obj).getColor() == this.color) {
            return true;
        } else {
            return false;
        }
    }

    // ----------- Others -------------

    public String toString(){
        return this.toSimpleString() + "-" + getColorLetter(this.getColor());
    }

    public String toSimpleString() {
        return getTypeLetter(this.getPieceType()) + this.tile;
    }

    public abstract String toSymbol(int column);

    public void delete(){
        // remove piece from tile and board
        this.board.removePieceAt(this.tile);
        this.tile.removePiece();

        this.inPlay = false;
        this.tile = null;
        this.color = null;
    }

}
