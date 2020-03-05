package aomidi.chess.model;

import java.util.Scanner;

import static aomidi.chess.model.Util.input;
import static aomidi.chess.model.Util.letterToInt;

public class Chess {
    private Game game;
    private Board board;
    private String error;
    private boolean firstMove;

    public Chess(){
        this.game = new Game(this);
        this.board = game.getBoard();
        this.error = "";
        this.firstMove = true;
    }

    // Getters
    public Game getGame() { return game; }

    // Functions
    public void movePiece(){
        boolean pieceMoved = false;
        try {
            if (this.firstMove) {
                System.out.println("\033[0;1m" + game.getTurn() + "Player's Turn" + "\033[0;0m");
                this.firstMove = false;
            }

            pieceMoved = this.getGame().move(testPieceInput(input(" * Select Piece: ")), testTileInput(input(" * Move to: ")));
        } catch (Exception e){
            System.out.println( e.getMessage() + "\n");
        }

        if (pieceMoved){
            System.out.println(this.board.toSymbol());
            if (game.getTurn() == Util.Color.White){
                game.setTurn(Util.Color.Black);
            } else {
                game.setTurn(Util.Color.White);
            }
            this.firstMove = true;
        } else {
            System.out.println("\033[0;1m" + "Move Again:" + "\033[0;0m");
        }
    }

    // Checkers
    public Piece testPieceInput(String piece_moved){
        int cur_x = letterToInt(String.valueOf(piece_moved.charAt(1)));
        int cur_y = piece_moved.charAt(2) - '0';
        Piece piece = this.board.getPieceAt(cur_x, cur_y);

        // Throw Errors before testing
        if (piece == null){
            throw new NullPointerException(piece_moved + " does not exist");
        }
        if (piece.getColor() != game.getTurn()){
            throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");
        }

        return piece;
    }

    public Tile testTileInput(String new_tile){
        int new_x = letterToInt(String.valueOf(new_tile.charAt(0)));
        int new_y = new_tile.charAt(1) - '0';
        Tile move_tile = board.getTileAt(new_x, new_y);

        if (move_tile == null){
            throw new NullPointerException("Tile " + new_tile + " does not exist");
        } else {
            return move_tile;
        }
    }




}
