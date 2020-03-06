package aomidi.chess.model;

import aomidi.chess.model.Util.Color;

import java.util.ArrayList;
import java.util.List;

import static aomidi.chess.model.Util.*;
import static aomidi.chess.model.Util.input;

public class Game {
    private Chess chess;
    private List<Move> movesList;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player curPlayer;
    private boolean gameOver;

    // ----------- Constructors -------------
    public Game(Chess chess){
        this.chess = chess;
        this.board = new Board(this);
        this.movesList = new ArrayList<Move>();

        this.whitePlayer = new Player(Color.White, this);
        this.blackPlayer = new Player(Color.Black, this);
        this.curPlayer = this.whitePlayer;

        this.gameOver = false;

        printBoard();
    }

    // ----------- Getters -------------

    public Chess getChess() { return chess; }

    public Board getBoard() { return board; }

    public Player getBlackPlayer() { return blackPlayer; }

    public Player getWhitePlayer() { return whitePlayer; }

    public Player getCurPlayer() {
        return curPlayer;
    }

    public Player getOpposingPlayer() {
        if (curPlayer.getColor() == Color.White){
            return blackPlayer;
        } else {
            return whitePlayer;
        }

    }

    public boolean isGameOver(){ return gameOver; }

    public Color getWinner(){
        if (this.curPlayer == whitePlayer){
            return Color.Black;
        } else if (this.curPlayer == blackPlayer){
            return Color.White;
        } else {
            throw new NullPointerException("No Current Player");
        }
    }

    // ----------- Checkers -------------

    public boolean isCheckmate(){
        if (curPlayer.isKingChecked() && curPlayer.wasKingChecked()){
            gameOver = true;
            return true;
        } else
            return false;
    }

    public boolean playerUnderCheck(Player player){
        return board.isChecked(player);
    }

    public Piece testPieceInput(String piece_moved) throws Exception {
        if (piece_moved.toLowerCase().compareTo("resign") == 0) {
            gameOver = true;
            throw new Exception(curPlayer.getColor() + " Resigns");
        }

        int cur_x = letterToInt(String.valueOf(piece_moved.charAt(1)));
        int cur_y = piece_moved.charAt(2) - '0';
        Piece piece = this.board.getPieceAt(cur_x, cur_y);

        // Throw Errors before testing
        if (piece == null){
            throw new NullPointerException(piece_moved + " does not exist");
        }
        if (piece.getColor() != curPlayer.getColor()){
            throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");
        }

        return piece;
    }

    public Tile testTileInput(String new_tile){
        Tile move_tile;
        // Test if castle was init else treat like regular move
        if (new_tile.toUpperCase().compareTo("O-O") == 0){
            if (curPlayer.getColor() == Util.Color.White)
                move_tile = board.getTileAt(7,1);
            else
                move_tile = board.getTileAt(7,8);
        } else if (new_tile.toUpperCase().compareTo("O-O-O") == 0){
            if (curPlayer.getColor() == Util.Color.White)
                move_tile = board.getTileAt(3,1);
            else
                move_tile = board.getTileAt(3,8);
        } else {
            int new_x = letterToInt(String.valueOf(new_tile.charAt(0)));
            int new_y = new_tile.charAt(1) - '0';
            move_tile = board.getTileAt(new_x, new_y);
        }

        if (move_tile == null){
            throw new NullPointerException("Tile " + new_tile + " does not exist");
        } else {
            return move_tile;
        }
    }

    // ----------- Actions -------------

    public boolean playerTurn(Player curPlayer){
        boolean pieceMoved = false;

        try {
            if (curPlayer.isFirstMove()) {
                System.out.println("\033[0;1m" + curPlayer.getColor() + "Player's Turn" + "\033[0;0m");
                curPlayer.setFirstMove(false);
            } else {
                System.out.println("\033[0;1m" + "Move Again:" + "\033[0;0m");
            }
            // Try to move to piece
            Move move = new Move(curPlayer.getColor(), this);
            pieceMoved = move.move(testPieceInput(input(" * Select Piece: ")), testTileInput(input(" * Move to: ")));
        } catch (Exception e){
            System.out.println( e.getMessage() + "\n");
        }

        return pieceMoved;
    }

    public void printChecks(){
        if (playerUnderCheck(whitePlayer)){
            whitePlayer.setChecked(true);
            System.out.println("Check: White");
        }
        if (playerUnderCheck(blackPlayer)){
            blackPlayer.setChecked(true);
            System.out.println("Check: Black");
        }
    }

    // ----------- Main Function -------------

    public void playGame(){
        boolean pieceMoved = playerTurn(curPlayer);

        while (!isGameOver()) {
            if (pieceMoved) {
                printBoard();
                // Reset players moves
                curPlayer.setFirstMove(true);
                // Switch players
                curPlayer = getOpposingPlayer();
                // Print Checks

                if (isCheckmate()) {
                    System.out.println(getWinner());
                    return;
                }
            }
            pieceMoved = playerTurn(curPlayer);
        }

        System.out.println("\033[0;1m" + "Game Over: " + getWinner() + " Wins" + "\033[0;0m");
    }

    // ----------- Others -------------

    private void printBoard(){
        if (!this.chess.isTest())
            System.out.println(this.board.toSymbol());
        else
            System.out.println(this.board.toString());
    }

}
