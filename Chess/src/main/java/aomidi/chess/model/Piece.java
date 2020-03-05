package aomidi.chess.model;

import aomidi.chess.model.Util.Color;
import aomidi.chess.model.Util.PieceType;

import static aomidi.chess.model.Util.*;

public abstract class Piece {
    private Tile tile;
    private Color color;
    private boolean inPlay;

    // Constructors
    public Piece(Tile tile, Color color) {
        this.tile = tile;
        this.color = color;
        this.inPlay = true;

        if (!this.tile.setPiece(this)) {
            throw new IllegalArgumentException("This tile already has a piece");
        }
    }

    // Getters
    public Tile getPosition(){ return this.tile; }

    public Color getColor(){ return this.color; }

    public abstract PieceType getPieceType();

    // Action
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

    public boolean moveTo(Tile tile, boolean overide){
        if (overide){
            this.tile.removePiece();

            // Set tile to new tile
            this.tile = tile;
            this.tile.setPiece(this);

            return true;
        } else {
            //throw new IllegalArgumentException("Piece cant move to tile: " + tile);
            return false;
        }
    }

    public boolean attack(Tile tile){
        if (validAttack(tile)){
            tile.getPiece().delete();

            // Move piece to new tile
            return moveTo(tile, true);
        } else {
            //throw new IllegalArgumentException("Piece cant move to tile: " + tile);
            return false;
        }
    }

    // Checkers
    public boolean validAttack(Tile tile){
        return validMove(tile);
    }

    public abstract boolean validMove(Tile tile);

    public boolean equals(Object obj) {
        if ( obj instanceof Piece && ((Piece) obj).getPosition().equals(this.tile) && ((Piece) obj).getColor() == this.color) {
            return true;
        } else {
            return false;
        }
    }

    // Others
    public String toString(){
        return this.toSimpleString() + "-" + getColorLetter(this.getColor());
    }

    public String toSimpleString() {
        return getTypeLetter(this.getPieceType()) + this.tile;
    }

    public abstract String toSymbol(int column);

    public void delete(){
        this.tile.removePiece();

        this.inPlay = false;
        this.tile = null;
        this.color = null;
    }

}
