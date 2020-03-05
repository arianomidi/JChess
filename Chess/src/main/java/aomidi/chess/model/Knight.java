package aomidi.chess.model;

import static aomidi.chess.model.Util.*;
import static aomidi.chess.model.Util.bold;

public class Knight extends Piece {
    public Knight(Tile tile, Util.Color color) {
        super(tile, color);
    }

    @Override
    public Util.PieceType getPieceType() { return PieceType.Knight; }

    @Override
    public boolean validMove(Tile tile) {
        return false;
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
