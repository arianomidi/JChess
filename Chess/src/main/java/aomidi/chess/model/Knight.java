package aomidi.chess.model;

import static aomidi.chess.model.Util.*;
import static aomidi.chess.model.Util.bold;
import static java.lang.Math.abs;

public class Knight extends Piece {

    public Knight(Tile tile, Util.Color color, Board board) {
        super(tile, color, board);
    }

    @Override
    public Util.PieceType getPieceType() { return PieceType.Knight; }

    @Override
    public boolean validMove(Tile tile){
        int cur_x = this.getPosition().getX(), cur_y = this.getPosition().getY();
        int new_x = tile.getX(), new_y = tile.getY();
        int diff_in_y = new_y - cur_y;
        int diff_in_x = new_x - cur_x;

        if (abs(diff_in_x) == 1 && abs(diff_in_y) == 2){
            return true;
        } else if (abs(diff_in_x) == 2 && abs(diff_in_y) == 1){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toSymbol(int column) {
        String string = this.getPosition().getSymbol(column);
        switch (column){
            case 1:
                return string;
            case 2:
                return replaceString(string, bold("_,,"), 4, 6);
            case 3:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold("\"-==\\~"), 3, 8);
                } else {
                    return replaceString(string, bold("\"-XX\\~"), 3, 8);
                }
            case 4:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold(") ("), 5, 7);
                } else {
                    return replaceString(string, bold(")X("), 5, 7);
                }
            case 5:
                if (this.getColor() == Util.Color.White) {
                    return replaceString(string, bold("{___}"), 4, 8);
                } else {
                    return replaceString(string, bold("{") + boldAndUnderline("/X\\")+ bold("}"), 4, 8);
                }
            case 6:
                return string;
            default:
                throw new IllegalArgumentException("Column out of range: " + column);
        }
    }
}
