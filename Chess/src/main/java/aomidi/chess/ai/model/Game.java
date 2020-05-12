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
    private List<Move> movesList;
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player curPlayer;
    private boolean isGameOver;
    private boolean isDraw;

    // ----------- Constructors -------------
    public Game(Chess chess) throws MoveGeneratorException {
        this.chess = chess;
        this.board = new Board();

        board.loadFromFen("r1b1k2r/pp1n1p1p/2p1pqpn/3pN3/4P1Q1/3K4/PBPP1PPP/R4BNR w kq - 0 1");

        this.whitePlayer = new User(Side.WHITE, this);
        this.blackPlayer = new AI(Side.BLACK, this);
        this.curPlayer = this.whitePlayer;

        this.isGameOver = false;

        //print the chessboard in a human-readable form
        System.out.println(board.toString());

        MoveList moves = MoveGenerator.generateLegalMoves(board);
        System.out.println("Eval: " + Util.evaluateBoard(board) + "\n");
        printLegalMovesEvals(board);
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
                if (curPlayer.isFirstMove()) {
                System.out.println("\033[0;1m" + curPlayer.getSide() + "Player's Turn" + "\033[0;0m");
                curPlayer.setFirstMove(false);
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
                    default:
                        // Try to move to piece
                        pieceMoved = curPlayer.movePiece(input);

//                        if (pieceMoved)
//                            movesList.add(move);
                }
            } else {
                pieceMoved = ((AI) curPlayer).movePiece();
            }


//            if (curPlayer.isFirstMove()) {
//                System.out.println("\033[0;1m" + curPlayer.getColor() + "Player's Turn" + "\033[0;0m");
//                curPlayer.setFirstMove(false);
//            } else {
//                System.out.println("\033[0;1m" + "Move Again:" + "\033[0;0m");
//            }
//
//            // Get player input
//            String input = input(" * Enter Move: ");
//            switch (input.toUpperCase()) {
//                case "CLOSE":
//                case "EXIT":
//                    isGameOver = true;
//                    throw new Exception("Exit Game");
//                case "RESIGN":
//                    isGameOver = true;
//                    throw new Exception(bold(curPlayer.getSide() + " Resigns"));
//                case "DRAW":
//                    //DrawOffer();
//                    break;
//                default:
//                    // Try to move to piece
//                    Move move = new Move(input, curPlayer, this);
//                    pieceMoved = move.move();
//
//                    if (pieceMoved)
//                        movesList.add(move);
//            }

        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
        }

        return pieceMoved;
    }
//
//    // ----------- Main Function -------------
//
    public void playGame() throws MoveGeneratorException {
        boolean pieceMoved = playerTurn(curPlayer);

        while (!isGameOver) {
            if (pieceMoved) {
//                if (curPlayer instanceof User) {
                    System.out.println(board);
                    System.out.println("Eval: " + Util.evaluateBoard(board) + "\n");
                    printLegalMovesEvals(board);
                    // Reset players moves
                    curPlayer.setFirstMove(true);
//                }
                // Switch players
                curPlayer = getOpposingPlayer();
                // Get Checks and Checkmates
                if (board.isMated()) {
                    System.out.println("Checkmate");
                    break;
                } else if (board.isDraw()){
                    isDraw = true;
                    break;
                }
            }
            pieceMoved = playerTurn(curPlayer);
        }

        GameOver();
    }

    // ----------- Game Functions -------------

//    public String GameStatus() {
//        Move last_move = movesList.get(movesList.size() - 1);
//
//        if (curPlayer.isUnderCheck()) {
//            if (isCheckmate()) {
//                // Change last move notation to checkmate
//                last_move.getMove().replace("string", last_move.getMove().get("string") + "#");
//                System.out.println("\033[0;1mCheckmate\n");
//
//                isGameOver = true;
//                return "Checkmate";
//            } else {
//                // Change last move notation to check
//                last_move.getMove().replace("string", last_move.getMove().get("string") + "+");
//                System.out.println("\033[0;1mCheck\n");
//
//                return "Check";
//            }
//        }
//
//        return "";
//    }

//    private boolean isCheckmate(){
//        boolean checkmate = true;
//        // If its a singular check, test if there is a piece that can block
//        ArrayList<ArrayList<Tile>> blocking_tiles = board.getTilesBetweenKingCheckingPiece(curPlayer);
//
//        if (blocking_tiles.size() == 1)
//            for (Tile tile : blocking_tiles.get(0)){
//                ArrayList<Piece> defending_pieces = board.getPieces(curPlayer.getColor());
//
//                for (Piece piece : defending_pieces) {
//                    if (!(piece instanceof King)) {
//                        try {
//                            // Test if Move is Valid
//                            Move move = new Move(piece, tile, curPlayer, this);
//
//                            // If piece can move to a blocking tile then its not a checkmate
//                            if (move.move()) {
//                                if (!curPlayer.isUnderCheck()){
//                                    System.out.println(move);
//                                    checkmate = false;
//                                }
//                                move.moveBack();
//                            }
//
//                        } catch (Exception e) {
////                            System.out.println(e.getMessage() + "\n");
//                        }
//                    }
//                }
//            }
//
//        if (!checkmate)
//            return false;
//
//
//        // Check if King has a legal move
//        King king = curPlayer.getKing();
//        for (int x_diff = -1; x_diff <= 1; x_diff++){
//            int file = king.getPosition().getX() + x_diff;
//
//            // Skip if file is out of bounds
//            if (file == 0 || file == 9)
//                continue;
//            for (int y_diff = -1; y_diff <= 1; y_diff++){
//                int rank = king.getPosition().getY() + y_diff;
//
//                // Skip if file is out of bounds or if tile is king
//                if (rank == 0 || rank == 9)
//                    continue;
//                else if (x_diff == 0 && y_diff == 0)
//                    continue;
//
//                Tile test_tile = board.getTileAt(file, rank);
//
//                try {
//                    if (!test_tile.hasPiece() && king.validMove(test_tile))
//                        return false;
//                } catch (Exception e){
//
//                }
//            }
//        }
//
//        return true;
//    }


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

    // ----------- Extra -------------

//    private ArrayList<Move> generateMoves(){
//        ArrayList<Move> legalMoves = new ArrayList<>();
//        ArrayList<Piece> pieces = board.getPieces(curPlayer.getColor());
//
//        for (int file = 1; file <= 8; file++){
//            for (int rank = 1; rank <= 8; rank++){
//                Tile tile = getBoard().getTileAt(file, rank);
//
//                for (Piece piece : pieces) {
//                    try {
//
//                        Move move = new Move(piece, tile, curPlayer, this);
//                        if (move.validAttack(piece, tile) || move.validMove(piece, tile)) {
//                            legalMoves.add(move);
//                        }
//
//                    } catch (Exception e) {
//                        //System.out.println(e.getMessage());
//
//                    }
//                }
//            }
//        }
//        System.out.println(legalMoves);
//
//        return legalMoves;
//    }


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
        for (int i = 0; i < movesList.size(); i++) {
            if (i % 2 == 0) {
                System.out.print(bold(((i / 2) + 1) + ". ") + "\033[0;0m");
            }
            System.out.print(movesList.get(i) + " ");
        }
        System.out.println("\n");
    }

}

