package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;
import static java.lang.Math.abs;

public class Queen extends Piece {

    // ----------- Constructor -------------
    public Queen(Tile tile, Color color) {
        super(tile, color);
    }

    // ----------- Getters -------------

    public PieceType getPieceType() {
        return PieceType.Queen;
    }

    // ----------- Checkers -------------
    @Override
    public boolean validMove(Tile tile) {
        int cur_x = this.getPosition().getX(), cur_y = this.getPosition().getY();
        int new_x = tile.getX(), new_y = tile.getY();
        int diff_in_y = new_y - cur_y;
        int diff_in_x = new_x - cur_x;

        if (diff_in_x == 0) {
            return diff_in_y != 0;
        } else if (diff_in_y == 0) {
            return true;
        } else
            return abs(diff_in_x) == abs(diff_in_y);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Queen) {
            return super.equals(obj);
        } else {
            return false;
        }
    }

    // ----------- Others -------------
    @Override
    public String toSymbol(int column) {
        String string = this.getPosition().getSymbol(column);
        switch (column) {
            case 1:
                return replaceString(string, bold("*"), 6, 6);
            case 2:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold(")_("), 5, 7);
                } else {
                    return replaceString(string, bold(")X("), 5, 7);
                }
            case 3:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold("{|}"), 5, 7);
                } else {
                    return replaceString(string, bold("{|}"), 5, 7);
                }
            case 4:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold("/_\\"), 5, 7);
                } else {
                    return replaceString(string, bold("/|\\"), 5, 7);
                }
            case 5:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold(") ("), 5, 7);
                } else {
                    return replaceString(string, bold(")X("), 5, 7);
                }
            case 6:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold("{___}") + "\033[4m", 8, 12);
                } else {
                    return replaceString(string, bold("{") + boldAndUnderline("/X\\") + bold("}") + "\033[4m", 8, 12);
                }
            default:
                throw new IllegalArgumentException("Column out of range: " + column);
        }
    }
}
