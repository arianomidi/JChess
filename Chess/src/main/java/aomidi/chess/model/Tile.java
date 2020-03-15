package aomidi.chess.model;

import aomidi.chess.model.Util.Color;

import static aomidi.chess.model.Util.*;

public class Tile {
    private int x, y;
    private Color color;
    private Piece piece;

    // ----------- Constructor -------------

    public Tile(int x_coordinate, int y_coordinate, Color color){
        this.x = x_coordinate;
        this.y = y_coordinate;
        this.color = color;
        this.piece = null;
    }

    // ----------- Getters -------------

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Color getColor() { return this.color; }

    public Piece getPiece() { return this.piece; }

    // ----------- Setters -------------

    public boolean setPiece(Piece piece) {
        if (this.piece == null){
            this.piece = piece;
            return true;
        } else {
            return false;
        }
    }

    public boolean removePiece(){
        this.piece = null;
        return true;
    }

    // ----------- Checkers -------------

    public boolean hasPiece() { return (this.piece != null); }

    // ----------- Others -------------

    public String toString(){
        return intToLetter(this.getX()).toLowerCase() + this.getY();
    }

    public String toSymbol(int column){
        // If there's a piece use piece symbol else use empty tile symbol
        if (this.hasPiece()){
            return this.getPiece().toSymbol(column);
        } else {
            return getSymbol(column);
        }
    }

    public String getSymbol(int column){
        switch (column){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                if (this.getColor() == Color.White)
                    return Chess.getBoardColor() + "           |";
                else
                    return Chess.getBoardColor() + " / / / / / |";
            case 6:
                if (this.getColor() == Color.White)
                    return Chess.getBoardColor() + underline("           |");
                else
                    return Chess.getBoardColor() + underline(" / / / / / |");
            default:
                throw new IllegalArgumentException("Column out of range: " + column);
        }
    }

}
