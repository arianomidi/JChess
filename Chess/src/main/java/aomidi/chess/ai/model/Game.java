package aomidi.chess.ai.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.move.*;

public class Game {
    // TODO: Pawn Promotions
    private Chess chess;
    private LinkedList<MoveBackup> moves = new LinkedList<>();
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
//        this.whitePlayer = new AI(1, Side.WHITE, this);
        this.whitePlayer = new User(Side.WHITE, this);
        this.blackPlayer = new AI(chess.getDepth(), Side.BLACK, this);
        this.curPlayer = this.whitePlayer;

        this.isGameOver = false;

        // Print the chessboard
        printBoard(board);
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
                    System.out.println("\033[0;1m" + curPlayer.getColor() + " Player's Turn" + "\033[0;0m");
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
                        ((User) curPlayer).setFirstMove(true);
                        board.undoMove(); board.undoMove();
                        moves.removeLast(); moves.removeLast();

                        printBoard(board); break;
                    default:
                        // Try to move to piece
                        pieceMoved = curPlayer.movePiece(input);

                }
            } else {
//                input(" * Cont: ");
                pieceMoved = ((AI) curPlayer).movePiece();
            }

            if (pieceMoved)
                moves.addLast(board.getBackup().getLast());

        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
        }

        return pieceMoved;
    }

    // ----------- Main Function -------------

    public void playGame() {
        boolean pieceMoved = playerTurn(curPlayer);

        while (!isGameOver) {
            if (pieceMoved) {
                if (curPlayer instanceof User) {
                    // Reset players moves
                    ((User) curPlayer).setFirstMove(true);
                    // Print Board And Info
                    printBoard(board);
                    System.out.println(bold("Move Played: ") + board.getBackup().getLast().getMove() + '\n');
                    System.out.println(bold(getOpposingPlayer().getColor() + " Player's Turn"));
                }
                // Switch players
                curPlayer = getOpposingPlayer();


                // Check Game Status
                System.out.println(bold(GameStatus()));
                //TODO: Make notation sidebar on print board
//                printMoves();

                if (isGameOver)
                    break;
            }
            pieceMoved = playerTurn(curPlayer);
//            sleep(2000);
        }

        GameOver();
    }

    // ----------- Game Functions -------------

    public String GameStatus() {

        if (board.isKingAttacked()) {
            Move last_move = moves.getLast().getMove();
            if (board.isMated()) {
                // TODO
                // Change last move notation to checkmate
                moves.getLast().addToMoveNotation("#");
                isGameOver = true;
                return "Checkmate";
            } else {
                // TODO
                // Change last move notation to check
                moves.getLast().addToMoveNotation("+");
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

    // TODO: DRAW ACCEPTANCE
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

    public void GameOver() {
        if (isDraw)
            System.out.println(boldAndUnderline("Game Over: Game Drawn\n") + "\033[0;0m");
        else
            System.out.println(boldAndUnderline("Game Over: " + getOpposingPlayer().getSide() + " Wins\n") + "\033[0;0m");

        printMoves();

        // View Moves
        Board viewBoard = new Board();
        chess.setStartingPosition(viewBoard);
        int i = 0;

        System.out.println(boldAndUnderline("\nGame Analysis: \n"));
        printBoard(viewBoard);
        String input = input("ENTER for Next Move, 'B' for prev move, 'EXIT' to exit").toUpperCase();

        while (input.compareTo("EXIT") != 0) {
            switch (input) {
                case "\n": case "": case "N":
                    if (i < moves.size()) {
                        viewBoard.doMove(moves.get(i).getMove());
                        System.out.println();
                        printBoard(viewBoard);
                        i++;
                    }
                    break;
                case "B":
                    if (i > 0) {
                        viewBoard.undoMove();
                        System.out.println();
                        printBoard(viewBoard);
                        i--;
                    }
                    break;
            }

            input = input("ENTER for Next Move, 'B' for prev move, 'EXIT' to exit").toUpperCase();
        }

        printBoard(viewBoard);
    }

    // ----------- Prints -------------

    private void printMoves() {
        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                System.out.print(bold(((i / 2) + 1) + ". "));
            }

            if (board.getContext().isQueenSideCastle(moves.get(i).getMove()))
                moves.get(i).setMoveNotation("O-O-O");
            else if (board.getContext().isKingSideCastle(moves.get(i).getMove()))
                moves.get(i).setMoveNotation("O-O");

            System.out.print(moves.get(i).getMoveNotation() + " ");
        }
        System.out.println("\n");

    }

}

