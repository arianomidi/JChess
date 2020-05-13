package aomidi.chess.ai.model;

import aomidi.chess.ai.model.Util.*;

import java.util.ArrayList;
import java.util.List;

import static aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.graalvm.compiler.lir.LIRInstruction;

public class Game {
    private Chess chess;
    private List<Move> moves;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player curPlayer;
    private boolean isGameOver;
    private boolean isDraw;

    // ----------- Constructors -------------
    public Game(Chess chess){
        this.chess = chess;
        this.board = new Board();

        chess.setStartingPosition(board);

        // Init Players
        //this.whitePlayer = new AI(4, Side.WHITE, this);
        this.whitePlayer = new User(Side.WHITE, this);
        this.blackPlayer = new AI(chess.getDepth(), Side.BLACK, this);
        this.curPlayer = this.whitePlayer;

        this.isGameOver = false;

        // Print the chessboard
        System.out.println(board.toString());

//        MoveList moves = MoveGenerator.generateLegalMoves(board);
//        System.out.println("Eval: " + Util.evaluateBoard(board) + "\n");
//        printLegalMovesEvals(board);
    }

    // ----------- Getters -------------

    public Chess getChess() {
        return chess;
    }

    public Board getBoard() {
        return board;
    }

    public Player getOpposingPlayer() {
        if (curPlayer.getSide() == Side.WHITE) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
    }

    // ----------- Actions -------------

    public boolean playerTurn(Player curPlayer) {
        boolean pieceMoved = false;

        try {
            if (curPlayer instanceof User){
                if (((User) curPlayer).isFirstMove()) {
                    System.out.println("\033[0;1m" + curPlayer.getSide() + "Player's Turn" + "\033[0;0m");
                    ((User) curPlayer).setFirstMove(false);
                } else
                    System.out.println("\033[0;1m" + "Move Again:" + "\033[0;0m");

                // Get player input
                String input = input(" * Enter Move: ");
                switch (input.toUpperCase()) {
                    case "CLOSE":
                    case "EXIT":
                        isGameOver = true;
                        throw new Exception("Exit Game");
                    case "RESIGN":
                        isGameOver = true;
                        throw new Exception(bold(curPlayer.getSide() + " Resigns"));
                    case "DRAW":
                        //DrawOffer();
                        break;
                    case "EVAL": case "EVALUATION":
                        System.out.println("\033[0;1mEval:\033[0m " + Util.evaluateBoard(board) + "\n");
                        break;
                    case "HINT":
                        Move bestMove = AI.miniMaxRoot(chess.getDepth(), board, curPlayer.getSide() == Side.WHITE);
                        System.out.println(bold("Best Move: ") + bestMove + "\n");
                        break;
                    case "UNDO": case "TAKE BACK":
                        board.undoMove();
                        board.undoMove();
                        System.out.println(board);
                        ((User) curPlayer).setFirstMove(true);
                        break;
                    default:
                        // Try to move to piece
                        pieceMoved = curPlayer.movePiece(input);

//                        if (pieceMoved)
//                            movesList.add(move);
                }
            } else {
                //input(" * Cont: ");
                pieceMoved = ((AI) curPlayer).movePiece();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
        }

        return pieceMoved;
    }
//
//    // ----------- Main Function -------------
//
    public void playGame() {
        boolean pieceMoved = playerTurn(curPlayer);

        while (!isGameOver) {
            if (pieceMoved) {
                if (curPlayer instanceof User) {
                    // Reset players moves
                    ((User) curPlayer).setFirstMove(true);
                }
                // Switch players
                curPlayer = getOpposingPlayer();

                // Check Game Status
                System.out.println(bold(GameStatus()));

                if (isGameOver)
                    break;
            }
            pieceMoved = playerTurn(curPlayer);
        }

        GameOver();
    }

    // ----------- Game Functions -------------

    public String GameStatus() {

        if (board.isKingAttacked()) {
            if (board.isMated()) {
                // TODO
//                // Change last move notation to checkmate
//                last_move.getMove().replace("string", last_move.getMove().get("string") + "#");
                isGameOver = true;
                return "Checkmate";
            } else {
                // TODO
                // Change last move notation to check
//                last_move.getMove().replace("string", last_move.getMove().get("string") + "+");
                return "Check";
            }
        } else if (board.isDraw()){
            isGameOver = true;
            isDraw = true;
            if (board.isStaleMate())
                return "Draw: Stalemate";
            else
                return "Draw: Insufficient Material";
        }

        return "";
    }

//    public void DrawOffer() throws Exception {
//        sleep(1000);
//        System.out.println("\n\n");
//        System.out.println(this.board.toSymbol(getOpposingColor(curPlayer.getColor())));
//        System.out.println("\033[0;1m" + getOpposingColor(curPlayer.getColor()) + "Player's Turn" + "\033[0;0m");
//
//        if (input("\033[0;0m * " + curPlayer.getColor() + " Offers a Draw: Accept(Y/N) \033[0;0m").toUpperCase().compareTo("Y") == 0) {
//            isGameOver = true;
//            isDraw = true;
//            throw new Exception(bold("Draw Accepted"));
//        } else {
//            sleep(1000);
//            System.out.println("\n\n");
//            System.out.println(this.board.toSymbol(curPlayer.getColor()));
//            System.out.println("\033[0;1m" + curPlayer.getColor() + "Player's Turn" + "\033[0;0m");
//            System.out.println(" * Enter Move: Draw");
//            throw new Exception(bold("Draw Offer Declined"));
//        }
//    }
//
    public void GameOver() {
        if (isDraw)
            System.out.println(boldAndUnderline("Game Over: Game Drawn\n") + "\033[0;0m");
        else
            System.out.println(boldAndUnderline("Game Over: " + getOpposingPlayer().getSide() + " Wins\n") + "\033[0;0m");


    }


    // ----------- Prints -------------

//    private void printBoard() {
//        if (this.chess.flipBoardSelected()) {
//            System.out.println(this.board.toSymbol(curPlayer.getColor()));
//            if (!movesList.isEmpty()) {
//                System.out.print("\n\n");
//                sleep(1500);
//                System.out.print("\n\n\n");
//                System.out.println(this.board.toSymbol(getOpposingPlayer().getColor()));
//            }
//        } else if (chess.simpleSelected())
//            System.out.println(this.board.toSimpleSymbol());
//        else
//            System.out.println(this.board.toSymbol());
//    }

    private void printMoves() {
        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                System.out.print(bold(((i / 2) + 1) + ". ") + "\033[0;0m");
            }
            System.out.print(moves.get(i) + " ");
        }
        System.out.println("\n");
    }

}

