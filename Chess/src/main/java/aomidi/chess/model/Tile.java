package aomidi.chess.model;

import aomidi.chess.model.Util.Color;

import static aomidi.chess.model.Util.*;

public class Tile {
    private int x, y;
    private Color color;
    private Piece piece;

    public Tile(int x_coordinate, int y_coordinate, Color color){
        this.x = x_coordinate;
        this.y = y_coordinate;
        this.color = color;
        this.piece = null;
    }

    public Tile(int x_coordinate, int y_coordinate, Color color, Piece piece){
        this.x = x_coordinate;
        this.y = y_coordinate;
        this.color = color;
        this.piece = piece;
    }

    // Getters
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Color getColor() { return this.color; }

    public Piece getPiece() { return this.piece; }

    // Checkers
    public boolean isBlack() { return (this.color == Color.Black); }

    public boolean isWhite() { return (this.color == Color.White); }

    public boolean hasPiece() { return (this.piece != null); }

    // Setters
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

    // Other
    public String toString(){
        return intToLetter(this.getX()).toLowerCase() + this.getY();
    }

    public String toSymbol(int column){
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

    public String toBoardTile(){
        if (this.hasPiece()) {
            String string =  getTypeLetter(getPiece().getPieceType()) + ":" + getColorLetter(getPiece().getColor());
            return "[" + "\033[0;1m" + string + "\033[0;0m" + "]";
        } else if (this.getColor() == Color.Black) {
            return "[///]";
        } else {
            return "[ / ]";
        }
    }

}
