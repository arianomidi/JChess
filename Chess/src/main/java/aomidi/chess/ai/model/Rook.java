package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;

public class Rook extends Piece {

    // ----------- Constructor -------------
    public Rook(Tile tile, Color color) {
        super(tile, color);
    }

    // ----------- Getters -------------
    @Override
    public PieceType getPieceType() {
        return PieceType.Rook;
    }

    // ----------- Checkers -------------
    @Override
    public boolean validMove(Tile tile) {
        int cur_x = this.getPosition().getX(), cur_y = this.getPosition().getY();
        int new_x = tile.getX(), new_y = tile.getY();
        int diff_in_y = new_y - cur_y;
        int diff_in_x = new_x - cur_x;

        if (diff_in_x == 0)
            return diff_in_y != 0;
        else
            return diff_in_y == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rook) {
            return super.equals(obj);
        } else {
            return false;
        }
    }

    // ----------- Actions -------------
//    @Override
//    public boolean moveTo(Tile tile) {
//        if (super.moveTo(tile)) {
//            this.firstMove = false;
//            return true;
//        } else {
//            return false;
//        }
//    }

    // ----------- Others -------------
    @Override
    public String toSymbol(int column) {
        String string = this.getPosition().getSymbol(column);

        switch (column) {
            case 1:
                return string;
            case 2:
                return replaceString(string, boldAndUnderline("UUU"), 5, 7);
            case 3:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold("[ ]"), 5, 7);
                } else {
                    return replaceString(string, bold("[\\]"), 5, 7);
                }
            case 4:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold(") ("), 5, 7);
                } else {
                    return replaceString(string, bold(")|("), 5, 7);
                }
            case 5:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold("{___}"), 4, 8);
                } else {
                    return replaceString(string, bold("{") + boldAndUnderline("/X\\") + bold("}"), 4, 8);
                }
            case 6:
                return string;
            default:
                throw new IllegalArgumentException("Column out of range: " + column);
        }
    }
}
