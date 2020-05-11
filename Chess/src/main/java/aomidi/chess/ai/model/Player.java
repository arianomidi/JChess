package aomidi.chess.ai.model;

import aomidi.chess.ai.model.Util.Color;

import java.util.ArrayList;

public class Player {
    private Color color;
    private Game game;
    private boolean firstMove;
    private King king;

    // ----------- Constructors -------------
    public Player(Color color, Game game) {
        this.color = color;
        this.game = game;
        this.firstMove = true;
        //this.king = (King) game.getBoard().getPiecesOfType(Util.PieceType.King, color).get(0);
    }

    // ----------- Getters -------------

    public Color getColor() {
        return color;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public King getKing() {
        return king;
    }

    // ----------- Setters -------------

    public void setFirstMove(boolean isFirstMove) {
        this.firstMove = isFirstMove;
    }

    // ----------- Checkers -------------

    public boolean isUnderCheck() {
        ArrayList<Piece> attacking_pieces = game.getBoard().getOpposingPieces(king.getColor());

        for (Piece p : attacking_pieces) {
            if (game.getBoard().isAttacking(p, king)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCheckmated() {
        boolean checkmate = true;
        // If its a singular check, test if there is a piece that can block
        ArrayList<ArrayList<Tile>> blocking_tiles = game.getBoard().getTilesBetweenKingCheckingPiece(this);

//        if (blocking_tiles.size() == 1)
//            for (Tile tile : blocking_tiles.get(0)){
//                ArrayList<Piece> defending_pieces = game.getBoard().getPieces(king.getColor());
//
//                for (Piece piece : defending_pieces) {
//                    if (!(piece instanceof King)) {
//                        try {
//                            // Test if Move is Valid
//                            Move move = new Move(piece, tile, this, game);
//
//                            if (tile.hasPiece())
//                                move.validAttack(piece, tile);
//                            else
//                                move.validMove(piece, tile);
//
//                            // If piece can move to a blocking tile then its not a checkmate
//                            if (piece.validMove(tile)) {
//
//                                System.out.println(move);
//                                checkmate = false;
//                            }
//
//                        } catch (Exception e) {
////                            System.out.println(e.getMessage() + "\n");
//                        }
//                    }
//                }
//            }

        if (!checkmate)
            return false;

        // Check if King has a legal move
        for (int x_diff = -1; x_diff <= 1; x_diff++){
            int file = king.getPosition().getX() + x_diff;

            // Skip if file is out of bounds
            if (file == 0 || file == 9)
                continue;
            for (int y_diff = -1; y_diff <= 1; y_diff++){
                int rank = king.getPosition().getY() + y_diff;

                // Skip if file is out of bounds or if tile is king
                if (rank == 0 || rank == 9)
                    continue;
                else if (x_diff == 0 && y_diff == 0)
                    continue;

                Tile test_tile = game.getBoard().getTileAt(file, rank);

                try {
                    if (!test_tile.hasPiece() && king.validMove(test_tile))
                        return false;
                } catch (Exception e){

                }
            }
        }

        return true;
    }

}
