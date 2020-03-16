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
    private boolean isGameOver;
    private boolean isDraw;

    // ----------- Constructors -------------
    public Game(Chess chess) {
        this.chess = chess;
        this.board = new Board(this);
        this.movesList = new ArrayList<>();

        this.whitePlayer = new Player(Color.White, this);
        this.blackPlayer = new Player(Color.Black, this);
        this.curPlayer = this.whitePlayer;

        this.isGameOver = false;

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
                case "CLOSE":
                case "EXIT":
                    isGameOver = true;
                    throw new Exception("Exit Game");
                case "RESIGN":
                    isGameOver = true;
                    throw new Exception(bold(curPlayer.getColor() + " Resigns"));
                case "DRAW":
                    DrawOffer();
                default:
                    // Try to move to piece
                    Move move = new Move(input, curPlayer, this);
                    pieceMoved = move.move();

                    if (pieceMoved)
                        movesList.add(move);
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

        while (!isGameOver) {
            if (pieceMoved) {
                // If player moved is still in check take back move
                if (curPlayer.isUnderCheck()){
                    takeBackMove();
                } else {
                    printBoard();
                    // Reset players moves
                    curPlayer.setFirstMove(true);
                    // Switch players
                    curPlayer = getOpposingPlayer();
                    // Get Checks and Checkmates
                    if (GameStatus().compareTo("Checkmate") == 0)
                        break;
                }
            }
            pieceMoved = playerTurn(curPlayer);
        }

        GameOver();
    }

    // ----------- Game Functions -------------

    public String GameStatus(){
        curPlayer.setChecked(true);
        Move last_move = movesList.get(movesList.size() - 1);

        if (curPlayer.isCheckmated()) {
            // Change last move notation to checkmate
            last_move.getMove().replace("string", ((String) last_move.getMove().get("string")) + "#");
            System.out.println("\033[0;1mCheckmate\n");

            isGameOver = true;
            return "Checkmate";
        } else if (curPlayer.isUnderCheck()) {
            // Change last move notation to check
            last_move.getMove().replace("string", ((String) last_move.getMove().get("string")) + "+");
            System.out.println("\033[0;1mCheck\n");

            return "Check";
        }

        return "";
    }

    public void DrawOffer() throws Exception {
        sleep(1000);
        System.out.println("\n\n");
        System.out.println(this.board.toSymbol(getOpposingColor(curPlayer.getColor())));
        System.out.println("\033[0;1m" + getOpposingColor(curPlayer.getColor()) + "Player's Turn" + "\033[0;0m");

        if (input("\033[0;0m * " + curPlayer.getColor() + " Offers a Draw: Accept(Y/N) \033[0;0m").toUpperCase().compareTo("Y") == 0) {
            isGameOver = true;
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
    }

    public void GameOver(){
        if (isDraw)
            System.out.println(boldAndUnderline("Game Over: Game Drawn\n") + "\033[0;0m");
        else
            System.out.println(boldAndUnderline("Game Over: " + getOpposingPlayer().getColor() + " Wins\n") + "\033[0;0m");
        printMoves();
    }

    // ----------- Prints -------------

    private void printBoard() {
        if (this.chess.flipBoardSelected()) {
            System.out.println(this.board.toSymbol(curPlayer.getColor()));
            if (!movesList.isEmpty()) {
                System.out.print("\n\n");
                sleep(1500);
                System.out.print("\n\n\n");
                System.out.println(this.board.toSymbol(getOpposingPlayer().getColor()));
            }
        }
        else
            System.out.println(this.board.toSymbol());
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

