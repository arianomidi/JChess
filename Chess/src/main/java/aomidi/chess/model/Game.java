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
    private int numMoves;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player curPlayer;
    private boolean gameOver;
    private boolean isDraw;

    // ----------- Constructors -------------
    public Game(Chess chess) {
        this.chess = chess;
        this.board = new Board(this);
        this.movesList = new ArrayList<>();
        this.numMoves = 1;

        this.whitePlayer = new Player(Color.White, this);
        this.blackPlayer = new Player(Color.Black, this);
        this.curPlayer = this.whitePlayer;

        this.gameOver = false;

        printBoard();
    }

    // ----------- Getters -------------

    public Chess getChess() {
        return chess;
    }

    public Board getBoard() {
        return board;
    }

    public Player getOpposingPlayer() {
        if (curPlayer.getColor() == Color.White) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Color getWinner() {
        if (this.curPlayer == whitePlayer) {
            return Color.Black;
        } else if (this.curPlayer == blackPlayer) {
            return Color.White;
        } else {
            throw new NullPointerException("No Current Player");
        }
    }

    // ----------- Setters -------------

    public void addMove(Move move){
        movesList.add(move);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    // ----------- Checkers -------------

    public boolean isCheckmate() {
        if (whitePlayer.isKingChecked() && whitePlayer.wasKingChecked()) {
            // Change last move notation to checkmate
            Move last_move = movesList.get(movesList.size() - 1);
            String new_move_str = (String) last_move.getMove().get("string");
            last_move.getMove().replace("string", replaceString(new_move_str, "#", new_move_str.length() - 1));

            gameOver = true;
            System.out.println(boldAndUnderline("Checkmate: Black Wins\n") + "\033[0m");
            return true;
        } else if (blackPlayer.isKingChecked() && blackPlayer.wasKingChecked()) {
            // Change last move notation to checkmate
            Move last_move = movesList.get(movesList.size() - 1);
            String new_move_str = (String) last_move.getMove().get("string");
            last_move.getMove().replace("string", replaceString(new_move_str, "#", new_move_str.length() - 1));

            gameOver = true;
            System.out.println(boldAndUnderline("Checkmate: White Wins\n") + "\033[0m");
            return true;
        } else
            return false;
    }

    public boolean playerUnderCheck(Player player) {
        return board.isChecked(player);
    }

    // ----------- Actions -------------

    public boolean playerTurn (Player curPlayer){
        boolean pieceMoved = false;

        try {
            if (curPlayer.isFirstMove()) {
                System.out.println("\033[0;1m" + curPlayer.getColor() + "Player's Turn" + "\033[0;0m");
                curPlayer.setFirstMove(false);
            } else {
                System.out.println("\033[0;1m" + "Move Again:" + "\033[0;0m");
            }

            // Get player input
            String input = input(" * Enter Move: ");

            switch (input.toUpperCase()) {
                case "EXIT":
                    gameOver = true;
                    throw new Exception("Exit Game");
                case "RESIGN":
                    gameOver = true;
                    throw new Exception(bold(curPlayer.getColor() + " Resigns"));
                case "DRAW":
                    sleep(1000);
                    System.out.println("\n\n");
                    System.out.println(this.board.toSymbol(getOpposingColor(curPlayer.getColor())));
                    System.out.println("\033[0;1m" + getOpposingColor(curPlayer.getColor()) + "Player's Turn" + "\033[0;0m");

                    if (input("\033[0;0m * " + curPlayer.getColor() + " Offers a Draw: Accept(Y/N) \033[0;0m").toUpperCase().compareTo("Y") == 0) {
                        gameOver = true;
                        isDraw = true;
                        throw new Exception(bold("Draw Accepted"));
                    } else {
                        sleep(1000);
                        System.out.println("\n\n");
                        System.out.println(this.board.toSymbol(curPlayer.getColor()));
                        System.out.println("\033[0;1m" + curPlayer.getColor() + "Player's Turn" + "\033[0;0m");
                        System.out.println(" * Enter Move: Draw");
                        throw new Exception(bold("Draw Offer Declined"));
                    }
                default:
                    // Try to move to piece
                    Move move = new Move(input, curPlayer, this);
                    pieceMoved = move.move();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
        }

        return pieceMoved;
    }

    public boolean takeBackMove (){
        // Remove Last Move
        Move lastMove = movesList.get(movesList.size() - 1);
        movesList.remove(movesList.size() - 1);

        System.out.println("Illegal Move: Move puts King in check\n");
        return lastMove.moveBack();
    }

    // ----------- Main Function -------------

    public void playGame () {
        boolean pieceMoved = playerTurn(curPlayer);

        while (!isGameOver()) {
            if (pieceMoved) {
                // If player moved is still in check take back move
                if (playerUnderCheck(curPlayer)){
                    takeBackMove();
                } else {
                    this.numMoves += 1;
                    printBoard();
                    // Reset players moves
                    curPlayer.setFirstMove(true);
                    // Switch players
                    curPlayer = getOpposingPlayer();
                    // Print Checks
                    printChecks();

                    if (isCheckmate()) {
                        printMoves();
                        return;
                    }
                }
            }
            pieceMoved = playerTurn(curPlayer);
        }

        if (isDraw)
            System.out.println(boldAndUnderline("Game Over: Game Ends in Draw\n") + "\033[0;0m");
        else
            System.out.println(boldAndUnderline("Game Over: " + getWinner() + " Wins\n") + "\033[0;0m");
        printMoves();
    }

    // ----------- Others -------------

    private void printChecks () {
        if (playerUnderCheck(whitePlayer)) {
            whitePlayer.setChecked(true);
            // Change last move notation to check
            Move last_move = movesList.get(movesList.size() - 1);
            last_move.getMove().replace("string", ((String) last_move.getMove().get("string")) + "+");

            System.out.println("\033[0;1mCheck: White\n");
        } else
            whitePlayer.setChecked(false);
        if (playerUnderCheck(blackPlayer)) {
            blackPlayer.setChecked(true);
            // Change last move notation to check
            Move last_move = movesList.get(movesList.size() - 1);
            last_move.getMove().replace("string", ((String) last_move.getMove().get("string")) + "+");

            System.out.println("\033[0;1mCheck: Black\n");
        } else
            blackPlayer.setChecked(false);
    }

    private void printBoard() {
//        if (!this.chess.isTest())
            if (this.chess.flipBoardSelected()) {
                System.out.println(this.board.toSymbol(curPlayer.getColor()));
                if (numMoves != 1) {
                    System.out.print("\n\n");
                    sleep(1500);
                    System.out.print("\n\n\n");
                    System.out.println(this.board.toSymbol(getOpposingPlayer().getColor()));
                }
            }
            else
                System.out.println(this.board.toSymbol());
//        else
//            System.out.println(this.board.toString());
    }

    private void printMoves() {
        for (int i = 0; i < movesList.size(); i++){
            if (i % 2 == 0) {
                Integer move_num = (i / 2) + 1;
                System.out.print(bold(move_num.toString() + ". ") + "\033[0;0m");
            }
            System.out.print(movesList.get(i) + " ");
        }
        System.out.println("\n");
    }

}

