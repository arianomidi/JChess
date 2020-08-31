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
    private Chess chess;
    private LinkedList<MoveBackup> moves = new LinkedList<>();
    private Board board;
    private Engine engine;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player curPlayer;
    private boolean isGameOver;
    private boolean isDraw;

    // Testing Vars
    private int moveCounter = 0;
    private int AIMoveLimit = 0;

    // ----------- Constructors -------------
    public Game(Chess chess){
        this.chess = chess;
        this.board = new Board();
        this.engine = new Engine(chess.getDepth());

        chess.setStartingPosition(board);

        // Init Players
//        this.whitePlayer = new AI(chess.getDepth(), Side.WHITE, this);
        this.whitePlayer = new User(Side.WHITE, this);
        this.blackPlayer = new AI(chess.getDepth(), Side.BLACK, this);
        this.curPlayer = this.whitePlayer;

        this.isGameOver = false;

        // Print the init chessboard
        printBoard(board, this);
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

    public Engine getEngine() {
        return engine;
    }

    public LinkedList<MoveBackup> getMoves() {
        return moves;
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
                    case "CLOSE": case "EXIT":
                        isGameOver = true;
                        throw new Exception("Exit Game");
                    case "RESIGN":
                        isGameOver = true;
                        throw new Exception(bold(curPlayer.getSide() + " Resigns"));
                    case "DRAW":
                        //DrawOffer();
                        break;
//                    case "EVAL": case "EVALUATION":
//                        System.out.println("\033[0;1mEval:\033[0m " + engine.getPositionEval(board) + "\n");
//                        break;
                    case "HINT":
                        Move bestMove = engine.miniMaxRoot(board);
                        System.out.println(bold("Best Move: ") + bestMove + "\n");
                        break;
                    case "UNDO": case "TAKE BACK":
                        ((User) curPlayer).setFirstMove(true);
                        board.undoMove(); board.undoMove();
                        moves.removeLast(); moves.removeLast();

                        printBoard(board, this); break;
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
                moveCounter++;

                // Get Game Status
                String status = GameStatus();

                if (curPlayer instanceof User) {
                    // Reset players moves
                    ((User) curPlayer).setFirstMove(true);
                    // Print Board And Info
                    printBoard(board, this);

                    System.out.println(bold(getOpposingPlayer().getColor() + " Player's Turn"));
                } else {
                    printBoard(board, this);
                    System.out.format("Time Taken: %.3fs, Positions Searched: %d, Pos/s: %.3f\n", ((AI) curPlayer).getMoveTime(), ((AI) curPlayer).getPositionCount(), ((AI) curPlayer).getPositionCount()/((AI) curPlayer).getMoveTime());
                }

                // Switch players
                curPlayer = getOpposingPlayer();

                // Print Game Status
                System.out.println(bold(status));

                if (isGameOver)
                    break;
            }
            pieceMoved = playerTurn(curPlayer);
//            sleep(2000);
        }

        GameOver();
    }

    // ----------- Game Functions -------------

    private String GameStatus() {

        if (board.isKingAttacked()) {
            Move last_move = moves.getLast().getMove();
            if (board.isMated()) {
                // Change last move notation to checkmate
                moves.getLast().addToMoveNotation("#");
                isGameOver = true;
                return "\nCheckmate\n";
            } else {
                // Change last move notation to check
                moves.getLast().addToMoveNotation("+");
                return "\nCheck\n";
            }
        } else if (board.isDraw()){
            isGameOver = true;
            isDraw = true;
            if (board.isStaleMate())
                return "\nDraw: Stalemate\n";
            else
                return "\nDraw: Insufficient Material\n";
        } else if (AIMoveLimit > 0 && moveCounter > AIMoveLimit)
            isGameOver = true;

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

    private void GameOver() {
        if (isDraw)
            System.out.println(boldAndUnderline("Game Over: Game Drawn\n") + "\033[0;0m");
        else
            System.out.println(boldAndUnderline("Game Over: " + getOpposingPlayer().getSide() + " Wins\n") + "\033[0;0m");

        printMoves();

        if (chess.getDoAnalysis())
            analysis();
    }

    private void analysis(){
        // Continue to Analysis
        String input = input("Press 'ENTER' to continue to Game Analysis").toUpperCase();

        if (input.compareTo("") != 0)
            return;


        System.out.println(boldAndUnderline("\nGame Analysis: \n"));

        // View Starting Board
        Board viewBoard = new Board();
        chess.setStartingPosition(viewBoard);
        int i = 0;

        printBoard(viewBoard, this);

        input = input("ENTER for Next Move, 'B' for prev move, 'EXIT' to exit").toUpperCase();

        while (input.compareTo("EXIT") != 0) {
            switch (input) {
                case "\n": case "": case "N":
                    if (i < moves.size()) {
                        moves.get(i).setMoveNotation(boldBoardColor(moves.get(i).getMoveNotation()));

                        viewBoard.doMove(moves.get(i).getMove());

                        System.out.println();

                        if (i > 0)
                            moves.get(i-1).setMoveNotation(defaultColor(moves.get(i-1).getMoveNotation()));

                        printBoard(viewBoard, this);
                        i++;
                    }
                    break;
                case "B":
                    if (i > 0) {
                        viewBoard.undoMove();
                        System.out.println();

                        moves.get(i - 1).setMoveNotation(defaultColor(moves.get(i-1).getMoveNotation()));
                        if (i > 1)
                            moves.get(i - 2).setMoveNotation(boldBoardColor(moves.get(i-2).getMoveNotation()));

                        printBoard(viewBoard, this);
                        i--;
                    }
                    break;
            }

            input = input("ENTER for Next Move, 'B' for prev move, 'EXIT' to exit").toUpperCase();
        }
    }

    // ----------- Prints -------------

    private void printMoves() {
        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                System.out.print(bold(((i / 2) + 1) + ". "));
            }

            System.out.print(moves.get(i).getMoveNotation() + " ");
        }
        System.out.println("\n");

    }

}

