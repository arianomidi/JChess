package aomidi.chess.ai.model;

import static aomidi.chess.ai.model.Util.*;
import static java.lang.Math.abs;

public class King extends Piece {

    // ----------- Constructor -------------
    public King(Tile tile, Color color) {
        super(tile, color);
    }

    // ----------- Getters -------------
    @Override
    public PieceType getPieceType() {
        return PieceType.King;
    }

//    public boolean isFirstMove() {
//        return this.firstMove;
//    }

    // ----------- Checkers -------------
    @Override
    public boolean validMove(Tile tile) {
        int cur_x = this.getPosition().getX(), cur_y = this.getPosition().getY();
        int new_x = tile.getX(), new_y = tile.getY();
        int diff_in_y = new_y - cur_y;
        int diff_in_x = new_x - cur_x;

        // Check if distance of new tile to cur tile is within 1 tile
        if (abs(diff_in_x) <= 1 && abs(diff_in_y) <= 1) {
            if (diff_in_y != 0 || diff_in_x != 0) {
//                // Check if king is not moving into a attacked tile
//                if (this.getBoard().isTileAttacked(tile, this.getColor())) {
//                    if (tile.hasPiece())
//                        throw new IllegalArgumentException("Invalid Move: King can't capture a defended piece");
//                    else
//                        throw new IllegalArgumentException("Invalid Move: King can't move into check");
//                }

                return true;
            }
        }

        // Short Castle Clause
        if (diff_in_x == 2 && diff_in_y == 0)
            return canCastle(tile, "short");
        // Long Castle Clause
        if (diff_in_x == -2 && diff_in_y == 0)
            return canCastle(tile, "long");

        return false;
    }

    // TODO
    public boolean canCastle(Tile tile, String type) {
//        String error = "";
//
//        if (!this.firstMove)
//            error = "King has moved";
//        else {
//            int rank;
//            if (this.getColor() == Color.White) {
//                rank = 1;
//            } else {
//                rank = 8;
//            }
//
//            Board board = this.getBoard();
//            if (type.compareTo("short") == 0) {
//                Tile f_tile = board.getTileAt(6, rank);
//                Tile g_tile = board.getTileAt(7, rank);
//                Tile h_tile = board.getTileAt(8, rank);
//
//                if (tile.equals(g_tile)) {
//                    Piece piece = h_tile.getPiece();
//                    if (piece instanceof Rook && ((Rook) piece).isFirstMove()) {
//                        piece.moveTo(f_tile, true);
//                        return true;
//                    } else {
//                        error = "Rook has moved";
//                    }
//                }
//            } else if (type.compareTo("long") == 0) {
//                Tile a_tile = board.getTileAt(1, rank);
//                Tile b_tile = board.getTileAt(2, rank);
//                Tile c_tile = board.getTileAt(3, rank);
//                Tile d_tile = board.getTileAt(4, rank);
//
//                if (tile.equals(c_tile) && !b_tile.hasPiece()) {
//                    Piece piece = a_tile.getPiece();
//                    if (piece instanceof Rook && ((Rook) piece).isFirstMove()) {
//                        piece.moveTo(d_tile, true);
//                        return true;
//                    } else {
//                        error = "Rook has moved";
//                    }
//                } else {
//                    error = "Piece on b1 is blocking the King";
//                }
//            }
//        }
//
//        throw new IllegalArgumentException("Cannot Castle: " + error);

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof King ) {
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

    // ----------- Other -------------
    @Override
    public String toSymbol(int column) {
        String string = this.getPosition().getSymbol(column);
        switch (column) {
            case 1:
                return replaceString(string, bold("+"), 6, 6);
            case 2:
                if (this.getColor() == Color.White) {
                    return replaceString(string, bold("\\_/"), 5, 7);
                } else {
                    return replaceString(string, bold("\\X/"), 5, 7);
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
