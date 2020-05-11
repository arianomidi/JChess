package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;


public abstract class Piece implements Cloneable{
    private Tile tile;
    private Color color;

    // ----------- Constructors -------------

    public Piece(Tile tile, Color color) {
        this.tile = tile;
        this.color = color;
    }

    // ----------- Getters -------------

    public Tile getPosition() {
        return this.tile;
    }

    public Color getColor() {
        return this.color;
    }

    public abstract PieceType getPieceType();

    // ----------- Setters -------------

    public void setTile(Tile tile) {
        this.tile.removePiece();
        this.tile = tile;
    }

    // ----------- Checkers -------------

    // Valid Attack = Valid Move unless overridden
    public boolean validAttack(Tile tile) {
        return validMove(tile);
    }

    public abstract boolean validMove(Tile tile);

    // Equals
    public boolean equals(Object obj) {
        return obj instanceof Piece && ((Piece) obj).getPosition().equals(this.tile) && ((Piece) obj).getColor() == this.color;
    }

    // ----------- Others -------------

    public String toString() {
        return this.toSimpleString() + "-" + getColorLetter(this.getColor());
    }

    public String toSimpleString() {
        return getTypeLetter(this.getPieceType()) + this.tile;
    }

    public abstract String toSymbol(int column);

    public String toSimpleSymbol() {
        if (color == Color.Black)
            return getTypeLetter(this.getPieceType()).toLowerCase();
        else
            return "\033[0;37m" + getTypeLetter(this.getPieceType()) + "\033[0m";
    }

    public void delete() {
        this.tile.removePiece();

        this.color = null;
        this.tile = null;
    }


}
