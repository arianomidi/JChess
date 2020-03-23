package aomidi.chess.model;

import aomidi.chess.model.Util.Color;
import aomidi.chess.model.Util.PieceType;

import static aomidi.chess.model.Util.getColorLetter;
import static aomidi.chess.model.Util.getTypeLetter;

public abstract class Piece implements Cloneable{
    private Tile tile;
    private Color color;
    private Board board;

    // ----------- Constructors -------------

    public Piece(Tile tile, Color color, Board board) {
        this.tile = tile;
        this.color = color;
        this.board = board;

        if (!this.board.addPieceAt(this, tile)) {
            throw new IllegalArgumentException("Already a Piece on tile " + tile);
        }
    }

    // ----------- Getters -------------

    public Tile getPosition() {
        return this.tile;
    }

    public Color getColor() {
        return this.color;
    }

    public Board getBoard() {
        return this.board;
    }

    public abstract PieceType getPieceType();

    // ----------- Actions -------------

    // If it's a valid move then move piece to new tile and remove from current tile
    public boolean moveTo(Tile tile) {
        if (validMove(tile)) {
            this.tile.removePiece();

            // Set tile to new tile
            this.tile = tile;
            this.tile.setPiece(this);

            return true;
        } else {
            throw new IllegalArgumentException(this.toSimpleString() + " cant move to " + tile);
        }
    }

    // Overide moveTo for Pawn Attack, Castling and Move Back
    public boolean moveTo(Tile tile, boolean overide) {
        this.tile.removePiece();

        // Set tile to new tile
        this.tile = tile;
        this.tile.setPiece(this);

        return true;
    }

    // Attack only if its a valid attack
    public boolean attack(Tile tile) {
        if (validAttack(tile)) {
            this.board.removePieceAt(tile);
            // Move piece to new tile
            return this.moveTo(tile, true);
        } else
            return false;
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

    public void delete() {
        // remove piece from tile and board

    }


}
