package aomidi.chess.model;

import aomidi.chess.model.Util.*;

import java.util.ArrayList;
import java.util.HashMap;

import static aomidi.chess.model.Util.*;
import static java.lang.Math.abs;

public class Board {
    private Game game;
    private HashMap<String, ArrayList<Tile>> tiles;
    private ArrayList<Piece> allPieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Piece> whitePieces;

    // ----------- Constructors -------------

    public Board(Game game) {
        this.game = game;
        this.allPieces = new ArrayList<>();
        this.blackPieces = new ArrayList<>();
        this.whitePieces = new ArrayList<>();
        this.tiles = new HashMap<>();

        // Init tiles
        for (int file = 1; file <= 8; file++) {
            ArrayList<Tile> row = new ArrayList<>();

            for (int rank = 1; rank <= 8; rank++) {
                Tile tile;
                // Black tile condition
                if ((file + rank) % 2 == 0) {
                    tile = new Tile(file, rank, Color.Black);
                } else {
                    tile = new Tile(file, rank, Color.White);
                }

                // Add tile to rank
                row.add(rank - 1, tile);
            }
            // Add file to specific letter
            this.tiles.put(intToLetter(file), row);
        }
        // Add Pieces
        this.addStartingPieces();
    }

    public void addStartingPieces() {
        String pieces = StandardStartingPosition;
        if (!game.getChess().isStandardGame())
            pieces = game.getCustomStartingPieces();

        for (int rank = 8; rank >= 1; rank--) {
            for (int file = 1; file <= 8; file++) {
                String str_type = pieces.substring((16 * (8 - rank)) + 2 * (file - 1), (16 * (8 - rank)) + 2 * file - 1);

                if (str_type.compareTo("#") != 0 && str_type.compareTo(" ") != 0) {
                    PieceType type = getPieceType(str_type);

                    Color color;
                    if (rank >= 5)
                        color = Color.Black;
                    else
                        color = Color.White;

                    this.addStartingPiece(type, file, rank, color);
                }
            }
        }
    }

    private void addStartingPiece(PieceType type, int file, int rank, Color color) {
        switch (type) {
            case Pawn:
                new Pawn(getTileAt(file, rank), color, this);
                break;
            case Knight:
                new Knight(getTileAt(file, rank), color, this);
                break;
            case Bishop:
                new Bishop(getTileAt(file, rank), color, this);
                break;
            case Rook:
                new Rook(getTileAt(file, rank), color, this);
                break;
            case Queen:
                new Queen(getTileAt(file, rank), color, this);
                break;
            case King:
                new King(getTileAt(file, rank), color, this);
                break;
        }
    }

    // ----------- Getters -------------

    public Tile getTileAt(Integer file, Integer rank) {
        return this.tiles.get(intToLetter(file)).get(rank - 1);
    }

    public Piece getPieceAt(Integer x, Integer y) {
        return getTileAt(x, y).getPiece();
    }

    public ArrayList<Piece> getOpposingPieces(Color color) {
        if (getOpposingColor(color) == Color.White)
            return whitePieces;
        else
            return blackPieces;

    }

    public ArrayList<Piece> getPiecesOfType(PieceType type, Color color) {
        ArrayList<Piece> return_pieces = new ArrayList<>();
        ArrayList<Piece> pieces;

        if (color == Color.White)
            pieces = whitePieces;
        else
            pieces = blackPieces;

        for (Piece p : pieces) {
            if (p.getPieceType() == type) {
                return_pieces.add(p);
            }
        }

        return return_pieces;
    }

    public ArrayList<Piece> getPiecesOfTypeCanMoveTo(PieceType type, Color color, Tile tile) {
        ArrayList<Piece> return_pieces = new ArrayList<>();
        ArrayList<Piece> pieces = getPiecesOfType(type, color);

        // If no Piece was found throw exception
        if (pieces.size() == 0)
            throw new IllegalArgumentException("Invalid Input");

        for (Piece p : pieces) {
            if (p.validMove(tile)) {
                return_pieces.add(p);
            }
        }

        // If no Piece was found throw exception
        if (return_pieces.size() == 0)
            throw new IllegalArgumentException("Invalid Move: No " + type + " can reach " + tile);

        return return_pieces;
    }

    public ArrayList<Piece> getPiecesOfTypeCanMoveTo(PieceType type, Character specified_char, Color color, Tile tile) {
        ArrayList<Piece> return_pieces = new ArrayList<>();
        ArrayList<Piece> pieces = getPiecesOfTypeCanMoveTo(type, color, tile);

        for (Piece p : pieces) {
            if (p.getPieceType() == type) {
                if (isFile(specified_char) && p.getPosition().getX() == letterToInt(String.valueOf(specified_char))) {
                    return_pieces.add(p);
                } else if (isRank(specified_char) && p.getPosition().getY() == Integer.parseInt(String.valueOf(specified_char))) {
                    return_pieces.add(p);
                }
            }
        }
        // If no Piece was found throw exception
        if (return_pieces.size() == 0)
            throw new IllegalArgumentException("Invalid Input");

        return return_pieces;
    }

    public ArrayList<ArrayList<Tile>> getTilesBetweenKingCheckingPiece(Player player) {
        King king = player.getKing();
        ArrayList<Piece> opposing_pieces = getOpposingPieces(player.getColor());
        ArrayList<ArrayList<Tile>> tiles_to_block = new ArrayList<>();

        for (Piece p : opposing_pieces) {
            if (isAttacking(p, king)) {
                tiles_to_block.add(getTilesBetween(king, p));
            }
        }

        return tiles_to_block;
    }

    private ArrayList<Tile> getTilesBetween(Piece p1, Piece p2) {
        int p1_x = p1.getPosition().getX(), p1_y = p1.getPosition().getY();
        int p2_x = p2.getPosition().getX(), new_y = p2.getPosition().getY();
        int diff_x = p2_x - p1_x, diff_y = new_y - p1_y;

        ArrayList<Tile> tiles_between = new ArrayList<>();

        if (diff_x == 0) {
            for (int i = 1; i <= abs(diff_y); i++) {
                tiles_between.add(this.getTileAt(p1_x, p1_y + i * Integer.signum(diff_y)));
            }
        } else if (diff_y == 0) {
            for (int i = 1; i <= abs(diff_x); i++) {
                tiles_between.add(this.getTileAt(p1_x + i * Integer.signum(diff_x), p1_y));
            }
        } else if (abs(diff_x) == abs(diff_y)) {
            for (int i = 1; i <= abs(diff_x); i++) {
                tiles_between.add(this.getTileAt(p1_x + i * Integer.signum(diff_x), p1_y + i * Integer.signum(diff_y)));
            }
        } else {
            tiles_between.add(p2.getPosition()); // Knight's
        }

        return tiles_between;
    }

    // ----------- Setters -------------

    public boolean addPieceAt(Piece piece, Integer x, Integer y) {
        // Add to arrays
        allPieces.add(piece);
        if (piece.getColor() == Color.White) {
            whitePieces.add(piece);
        } else {
            blackPieces.add(piece);
        }

        Tile tile = getTileAt(x, y);
        if (!tile.hasPiece()) {
            tile.setPiece(piece);
            return true;
        } else {
            return false;
        }
    }

    public boolean addPieceAt(Piece piece, Tile tile) {
        return addPieceAt(piece, tile.getX(), tile.getY());
    }

    public void removePieceAt(Tile tile) {
        if (tile.hasPiece()) {
            Color color = tile.getPiece().getColor();

            if (color == Color.White) {
                this.whitePieces.remove(tile.getPiece());
            } else {
                this.blackPieces.remove(tile.getPiece());
            }
            this.allPieces.remove(tile.getPiece());

            tile.removePiece();
        }
    }

    // ----------- Checkers -------------

    public boolean hasPieceAt(Integer x, Integer y) {
        return (getPieceAt(x, y) != null);
    }

    public boolean hasPieceBetweenTiles(Tile cur_tile, Tile new_tile) {
        int cur_x = cur_tile.getX(), cur_y = cur_tile.getY();
        int new_x = new_tile.getX(), new_y = new_tile.getY();
        int diff_x = new_x - cur_x, diff_y = new_y - cur_y;

        boolean existsPiece = false;

        if (diff_x == 0) {
            for (int i = 1; i < abs(diff_y); i++) {
                existsPiece = this.hasPieceAt(cur_x, cur_y + i * Integer.signum(diff_y));
                if (existsPiece) {
                    break;
                }
            }
        } else if (diff_y == 0) {
            for (int i = 1; i < abs(diff_x); i++) {
                existsPiece = this.hasPieceAt(cur_x + i * Integer.signum(diff_x), cur_y);
                if (existsPiece) {
                    break;
                }
            }
        } else if (abs(diff_x) == abs(diff_y)) {
            for (int i = 1; i < abs(diff_x); i++) {
                existsPiece = this.hasPieceAt(cur_x + i * Integer.signum(diff_x), cur_y + i * Integer.signum(diff_y));
                if (existsPiece) {
                    break;
                }
            }
        } else {
            return false;     // Knight's
        }

        return existsPiece;
    }

    public boolean isAttacking(Piece piece1, Piece piece2) {
        return isAttacking(piece1, piece2.getPosition());
    }

    public boolean isAttacking(Piece piece, Tile tile) {
        return !hasPieceBetweenTiles(piece.getPosition(), tile) && piece.validAttack(tile);
    }

    public boolean isTileAttacked(Tile tile, Color color) {
        ArrayList<Piece> attacking_pieces = getOpposingPieces(color);

        for (Piece p : attacking_pieces) {
            if (isAttacking(p, tile)) {
                return true;
            }
        }

        return false;
    }

    // ----------- Others -------------

    public String toSymbol() {
        String string = Chess.getBoardColor() + "  " + underline("                                                                                                \n");

        for (int rank = 1; rank <= 8; rank++) {

            for (int column = 1; column <= 6; column++) {
                for (int file = 0; file <= 9; file++) {
                    if (file == 0) {
                        string += " |";
                    } else if (file != 9) {
                        string += getTileAt(file, 9 - rank).toSymbol(column);
                    } else {
                        if (column == 3) {
                            string += bold("   " + (9 - rank));
                        }
                    }
                }
                string += "\n";
            }
        }
        string += bold("\n       A           B           C           D           E           F           G           H\n");
        return string;
    }

    public String toSymbol(Color color) {
        if (color == Color.White) {
            return this.toSymbol();
        } else {
            String string = Chess.getBoardColor() + "  " + underline("                                                                                                \n");

            for (int rank = 1; rank <= 8; rank++) {

                for (int column = 1; column <= 6; column++) {
                    for (int file = 0; file <= 9; file++) {
                        if (file == 0) {
                            string += " |";
                        } else if (file != 9) {
                            string += getTileAt(9 - file, rank).toSymbol(column);
                        } else {
                            if (column == 3) {
                                string += bold("   " + (rank));
                            }
                        }
                    }
                    string += "\n";
                }
            }
            string += bold("\n       H           G           F           E           D           C           B           A\n");
            return string;
        }
    }

}

