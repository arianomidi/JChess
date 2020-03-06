package aomidi.chess.model;

import aomidi.chess.model.Util.Color;

import static aomidi.chess.model.Util.boldAndUnderline;
import static aomidi.chess.model.Util.letterToInt;

public class Game {
    private Chess chess;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Color turn;

    public Game(Chess chess){
        this.chess = chess;
        this.whitePlayer = new Player(Color.White, this);
        this.blackPlayer = new Player(Color.Black, this);
        this.board = new Board(this);
        this.turn = Color.White;
    }

    // Getters
    public Chess getChess() { return chess; }

    public Board getBoard() { return board; }

    public Player getBlackPlayer() { return blackPlayer; }

    public Player getWhitePlayer() { return whitePlayer; }

    public Color getTurn() { return turn; }

    // Setters
    public void setTurn(Color color){ this.turn = color; }

    // Checkers
    public boolean validMove(Piece piece, Tile new_tile){
        boolean isPieceBlocking = board.hasPieceBetweenTiles(piece.getPosition(), new_tile);

        if (isPieceBlocking)
            throw new IllegalArgumentException(piece.toSimpleString() + " is blocked from getting to " + new_tile);

        return !isPieceBlocking;
    }

    public boolean validAttack(Piece piece, Tile new_tile){
        Piece pieceOnTile = board.getPieceAt(new_tile.getX(), new_tile.getY());
        boolean canReachTile = validMove(piece, new_tile);

        if (canReachTile) {
            if (pieceOnTile.getColor() == piece.getColor()) {
                throw new IllegalArgumentException("There is already a "+ piece.getColor() + " piece on " + new_tile);
            } else {
                return piece.attack(new_tile);
            }
        } else {
            return false;
        }
    }

    // Action
    public boolean move(Piece piece, Tile new_tile){
        boolean hasPieceOnTile = board.hasPieceAt(new_tile.getX(), new_tile.getY());

        // Attack or Move
        if (hasPieceOnTile){
            if (validAttack(piece, new_tile)){
                return true;
            } else {
                return false;
            }
        } else {
            if (validMove(piece, new_tile)) {
                return piece.moveTo(new_tile);
            } else {
                return false;
            }
        }
    }

}
