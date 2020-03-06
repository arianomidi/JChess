package aomidi.chess.model;

import java.util.HashMap;

import static aomidi.chess.model.Util.*;
import static java.lang.Math.abs;

public class King extends Piece {
    private boolean firstMove;

    public King(Tile tile, Color color, Board board) {
        super(tile, color, board);
        this.firstMove = true;
    }

    //Getters
    @Override
    public PieceType getPieceType() { return PieceType.King; }

    // Checkers
    @Override
    public boolean validMove(Tile tile){
        int cur_x = this.getPosition().getX(), cur_y = this.getPosition().getY();
        int new_x = tile.getX(), new_y = tile.getY();
        int diff_in_y = new_y - cur_y;
        int diff_in_x = new_x - cur_x;

        if (abs(diff_in_x) <= 1 || abs(diff_in_y) <= 1 ){
            if (diff_in_y != 0 && diff_in_x != 0){
                return true;
            }
        }
        // Short Castle Clause
        if (diff_in_x == 2 && diff_in_y == 0){
            System.out.print(canCastle(tile, "short"));
        }
        return false;
    }

    public boolean canCastle(Tile tile, String type){
        if (this.firstMove){
            Tile e1 = this.getPosition();
            Board board = this.getBoard();
            if (type.compareTo("short") == 0){
                Tile f1 = board.getTileAt("f", 1);
                Tile g1 = board.getTileAt("g", 1);
                Tile h1 = board.getTileAt("h", 1);

                if (!f1.hasPiece() && !g1.hasPiece() && tile.equals(g1)){
                    if (h1.getPiece() instanceof Rook && ((Rook) h1.getPiece()).isFirstMove()){
                        return true;
                    }
                }
            }
        }
        return false;
//
//        if (this.firstMove){
//            HashMap<String, Rook> rooks = this.getBoard().getRooks(this.getColor());
//
//            if(!rooks.isEmpty()){
//                if (rooks.containsKey("short")) {
//                    Rook shortRook = rooks.get("short");
//                    if (shortRook.isFirstMove()) {
//                        if (this.getBoard().getPieceAt(x + 1, y) == shortRook) {
//                            return true;
//                        }
//                    }
//                }
//                if (rooks.containsKey("long")) {
//                    Rook longRook = rooks.get("long");
//                    if (longRook.isFirstMove()) {
//                        if (this.getBoard().getPieceAt(x - 1, y) == longRook) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
    }

    // Action
    @Override
    public boolean moveTo(Tile tile) {
        if (super.moveTo(tile)){
            this.firstMove = false;
            return true;
        } else {
            return false;
        }
    }

    // Other
    @Override
    public String toSymbol(int column) {
        String string = this.getPosition().getSymbol(column);
        switch (column){
            case 1:
                return replaceString(string, bold("+"), 6, 6);
            case 2:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold("\\_/"), 5, 7);
                } else {
                    return replaceString(string, bold("\\X/"), 5, 7);
                }
            case 3:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold("{|}"), 5, 7);
                } else {
                    return replaceString(string, bold("{|}"), 5, 7);
                }
            case 4:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold("/_\\"), 5, 7);
                } else {
                    return replaceString(string, bold("/|\\"), 5, 7);
                }
            case 5:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold(") ("), 5, 7);
                } else {
                    return replaceString(string, bold(")X("), 5, 7);
                }
            case 6:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, boldAndUnderline("{   }") + "\033[0;4m   |", 10, string.length());
                } else {
                    return replaceString(string, boldAndUnderline("{/X\\}") + "\033[0;4m   |", 10, string.length());
                }
            default:
                throw new IllegalArgumentException("Column out of range: " + column);
        }
    }
}
