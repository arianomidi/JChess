package aomidi.chess.model;

import aomidi.chess.model.Util.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aomidi.chess.model.Util.*;
import static aomidi.chess.model.Util.input;
import static aomidi.chess.model.Util.isFile;

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

    public boolean comparePieceTo(Piece piece, PieceType type, Color color) throws Exception{
        if (piece.getColor() != color)
            throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");

        if (piece.getPieceType() == type){
            return true;
        } else
            return false;
    }

    public boolean tileHasMovedPiece(int x, int y, PieceType type) throws Exception{
        // Check board boundries
        if (1 > x || x > 8 || 1 > y || y > 8){
            return false;
        }

        if (board.getTileAt(x,y).hasPiece()) {
            Piece piece = board.getTileAt(x, y).getPiece();
            // Check if it's a Piece of type Type
            if (comparePieceTo(piece, type, curPlayer.getColor()))
                return true;
        }
        return false;
    }

    // Return false if pieces cant move to same square
    public boolean hasAmbiguousMoves(List<Piece> pieces, String tile) throws Exception{
        String error = "";

        // Check if pieces is empty
        if (pieces.size() == 0)
            throw new IllegalArgumentException("Invalid Move: " + tile + " cannot be reached");
        else if (pieces.size() == 1)
            return false;
        else {
            for (int i = 1; i < pieces.size(); i++){
                if (pieces.get(i).getPosition().getX() == pieces.get(i-1).getPosition().getX()){
                    error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i).getPosition().getY() + tile + " or ";
                    error += getTypeLetter(pieces.get(i).getPieceType()) + pieces.get(i-1).getPosition().getY() + tile;
                } else if (pieces.get(i).getPosition().getY() == pieces.get(i-1).getPosition().getY()) {
                    error += getTypeLetter(pieces.get(i).getPieceType()) + intToLetter(pieces.get(i).getPosition().getX()).toLowerCase() + tile + " or ";
                    error += getTypeLetter(pieces.get(i).getPieceType()) + intToLetter(pieces.get(i-1).getPosition().getX()).toLowerCase()  + tile;
                }
            }
        }

        if (error.compareTo("") == 0)
            return false;
        else
            throw new IllegalArgumentException("Ambiguous Move: Specify with " + error);
    }

    // ----------- Inputs -------------

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

    public Map<String, Object> getMoveInput(String string) throws Exception {
        Map<String, Object> move = new HashMap<>();

        // Case: Resign
        if (string.toLowerCase().compareTo("resign") == 0) {
            gameOver = true;
            throw new Exception(curPlayer.getColor() + " Resigns");
        }

        // Get Move to Tile and Piece
        Tile move_tile = getInputtedTile(string);
        Piece piece = getInputtedPiece(string, move_tile);

        // Throw Errors before testing
        if (piece.getColor() != curPlayer.getColor())
            throw new IllegalArgumentException("You cannot move a " + piece.getColor() + " piece");

        move.put("piece", piece);
        move.put("tile", move_tile);
        return move;
    }

    public Piece getInputtedPiece(String string, Tile move_tile) throws Exception{
        Piece piece;

        // Pawn Case:
        if (isFile(string.charAt(0))){
            int dir = -1;
            if (curPlayer.getColor() == Color.White)
                dir = 1;

            // If there's file inputted then test attack else move
            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')){
                if (board.getTileAt(letterToInt(String.valueOf(string.charAt(0))), move_tile.getY() - 1 * Integer.signum(dir)).hasPiece()) {
                    piece = board.getTileAt(letterToInt(String.valueOf(string.charAt(0))), move_tile.getY() - 1 * Integer.signum(dir)).getPiece();
                    return piece;
                }
            } else {
                // Check if piece 1 or 2 behind/ahead of tile has a Pawn
                if (tileHasMovedPiece(move_tile.getX(), move_tile.getY() - 1 * Integer.signum(dir), PieceType.Pawn)) {
                    return board.getTileAt(move_tile.getX(), move_tile.getY() - 1 * Integer.signum(dir)).getPiece();
                } else if (tileHasMovedPiece(move_tile.getX(), move_tile.getY() - 2 * Integer.signum(dir), PieceType.Pawn)) {
                    return board.getTileAt(move_tile.getX(), move_tile.getY() - 2 * Integer.signum(dir)).getPiece();
                }
            }
        }

        // Knight Case:
        if (Character.toUpperCase(string.charAt(0)) == 'N'){
            List<Piece> valid_knights = new ArrayList<>();

            // Simple Move or Take
            if (string.length() == 3 || (string.length() == 4 && Character.toLowerCase(string.charAt(1)) == 'x')){
                // All possible Knight squares
                ArrayList<Piece>  all_knights = board.getPiecesOfType(PieceType.Knight, curPlayer.getColor());

                // Check if Knights are valid
                for (Piece knight : all_knights){
                    if (knight instanceof Knight && knight.validMove(move_tile)){
                        valid_knights.add(knight);
                    }
                }

                // Check for Ambiguous Moves
                if (!hasAmbiguousMoves(valid_knights, string.substring(string.length() - 2)));
                    return valid_knights.get(0);
            }
        }

        throw new IllegalArgumentException("Invalid Move");
    }

    public Tile getInputtedTile(String string){
        Tile move_tile;
        int start_index = string.length() - 2;

        int new_x = letterToInt(String.valueOf(string.charAt(start_index)));
        int new_y = string.charAt(start_index + 1) - '0';
        move_tile = board.getTileAt(new_x, new_y);

        if (move_tile == null){
            throw new NullPointerException("Invalid Move: " + string.substring(string.length() - 2) + " is not a valid square");
        } else if (move_tile.hasPiece() && move_tile.getPiece().getColor() == curPlayer.getColor()){
            throw new NullPointerException("Invalid Move: " + string.substring(string.length() - 2) + " already has a " + curPlayer.getColor() + " piece");
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
            Move move = new Move(getMoveInput(input(" * Select Piece: ")),curPlayer.getColor(), this);
            pieceMoved = move.move();

            //pieceMoved = move.move(testPieceInput(input(" * Select Piece: ")), testTileInput(input(" * Move to: ")));
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
                printChecks();

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
