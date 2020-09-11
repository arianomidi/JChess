package aomidi.chess.ai.openingbook;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Arrays;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

import static com.github.bhlangonijr.chesslib.move.MoveGenerator.generatePieceLegalMoves;


public class OpeningBookParser {

    private static OpeningBook openingBook = new OpeningBook();
    private static String filename = "./resources/openings/openings_eco.cvs";
    private static boolean isPGN = false;
    private static boolean isECO = !isPGN;
    private static String opening_name = "";

    public static void parseFile(){
        try {
            File openingsFile = new File(filename);

            Scanner myReader = new Scanner(openingsFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                if (data.length() > 0 && data.charAt(0) == '1')
                    addToOpeningBook(data);
                else;
                    opening_name = data;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void addToOpeningBook(String opening){
        List<String> moves = Arrays.asList(opening.split(" "));
        Board board = new Board();

        System.out.println(opening);
        for (int i = 0; i < moves.size(); i++){
            boolean isDigit = Character.isDigit(moves.get(i).charAt(0));
            String move;

            if (OpeningBookParser.isECO) {
                if (isDigit) {
                    if (moves.get(i).charAt(1) == '/')
                        break;
                    move = moves.get(i).split("\\.")[1];
                } else {
                    move = moves.get(i);
                }
            } else {
                if (isDigit) {
                    continue;
                }
                move = moves.get(i);
            }

            Move openingMove = OpeningBookParser.getMove(move, board);

            if (i == moves.size() - 2)
                openingBook.addNode(openingMove, opening_name);
            else
                openingBook.addNode(openingMove, "");
        }

        openingBook.reset();
    }

    private static Move getMove(String strMove, Board board){
        Move move;
        boolean isPawnMove = true;
        boolean isPawnCapture = false;
        MoveList moveList = new MoveList();

        if (strMove.charAt(strMove.length() - 1) == '+' || strMove.charAt(strMove.length() - 1) == '#')
            strMove = strMove.substring(0, strMove.length() - 1);

        // CASTLE
        if (strMove.compareTo("O-O") == 0) {
            if (board.getSideToMove() == Side.WHITE)
                move = new Move(Square.E1, Square.G1);
            else
                move = new Move(Square.E8, Square.G8);
            board.doMove(move);
            return move;
        } else if (strMove.compareTo("O-O-O") == 0) {
            if (board.getSideToMove() == Side.WHITE)
                move = new Move(Square.E1, Square.C1);
            else
                move = new Move(Square.E8, Square.C8);
            board.doMove(move);
            return move;
        }

        // Pawn Move
        if (strMove.length() == 2 || (strMove.length() == 4 && strMove.charAt(2) == '=')) {
            try {
                moveList = generatePieceLegalMoves(board, PieceType.PAWN, false);
            } catch (MoveGeneratorException e) {
                e.printStackTrace();
            }
        } else if (strMove.charAt(1) == 'x' && Character.isLowerCase(strMove.charAt(0))){
            isPawnCapture = true;
            try {
                moveList = generatePieceLegalMoves(board, PieceType.PAWN, true);
            } catch (MoveGeneratorException e) {
                e.printStackTrace();
            }
        } else {
            isPawnMove = false;
            // Pieces
            try {
                moveList = generatePieceLegalMoves(board, getPieceType(strMove.charAt(0)), false);
            } catch (MoveGeneratorException e) {
                e.printStackTrace();
            }
        }

        Square toSquare = null;
        Square fromSquare = null;

        for (Move legalMove : moveList){
            if (isPawnMove){
                if (!isPawnCapture){
                    if (toSquare == null)
                        toSquare = Square.fromValue(strMove.substring(0,2).toUpperCase());

                    if (toSquare == legalMove.getTo()) {
                        fromSquare = legalMove.getFrom();
                        break;
                    }
                } else {
                    if (toSquare == null)
                        toSquare = Square.fromValue(strMove.substring(2,4).toUpperCase());

                    if (toSquare == legalMove.getTo() && legalMove.getFrom().getFile().getNotation().toLowerCase().charAt(0) == strMove.charAt(0)){
                        fromSquare = legalMove.getFrom();
                        break;
                    }
                }
            } else {
                if (toSquare == null)
                    toSquare = Square.fromValue(strMove.substring(strMove.length() - 2).toUpperCase());

                // NOT AMBIGUOUS
                if (strMove.length() == 3 || strMove.charAt(1) == 'x'){
                    if (toSquare == legalMove.getTo()){
                        fromSquare = legalMove.getFrom();
                        break;
                    }
                } else { // AMBIGUOUS
                    if (toSquare == legalMove.getTo() &&
                            ( Character.toUpperCase(strMove.charAt(1)) == legalMove.getFrom().getFile().getNotation().charAt(0) ||
                                    strMove.charAt(1) == legalMove.getFrom().getRank().getNotation().charAt(0) )){
                        fromSquare = legalMove.getFrom();
                        break;
                    }
                }
            }
        }

        if (isPawnMove && strMove.contains("="))
            move = new Move(fromSquare, toSquare, getPromotionPiece(strMove.split("=")[1], board.getSideToMove()));
        else
            move = new Move(fromSquare, toSquare);

        board.doMove(move);
        return move;
    }

    private static PieceType getPieceType(char piece) {
        piece = Character.toUpperCase(piece);
        switch (piece) {
            case 'N':
                return PieceType.KNIGHT;
            case 'B':
                return PieceType.BISHOP;
            case 'R':
                return PieceType.ROOK;
            case 'Q':
                return PieceType.QUEEN;
            case 'K':
                return PieceType.KING;
            default:
                throw new java.lang.IllegalArgumentException("Illegal Input: " + piece);
        }
    }

    public static Piece getPromotionPiece(String piece, Side side) {
        piece = piece.toUpperCase();
        if (side == Side.WHITE)
            switch (piece) {
                case "N":
                    return Piece.WHITE_KNIGHT;
                case "B":
                    return Piece.WHITE_BISHOP;
                case "R":
                    return Piece.WHITE_ROOK;
                case "Q":
                    return Piece.WHITE_QUEEN;
                default:
                    throw new java.lang.IllegalArgumentException("Illegal Input: " + piece);
            }
        else
            switch (piece) {
                case "N":
                    return Piece.BLACK_KNIGHT;
                case "B":
                    return Piece.BLACK_BISHOP;
                case "R":
                    return Piece.BLACK_ROOK;
                case "Q":
                    return Piece.BLACK_QUEEN;
                default:
                    throw new java.lang.IllegalArgumentException("Illegal Input: " + piece);
            }
    }

    public static OpeningBook getOpeningBook(){
        return  openingBook;
    }

    public static void main(String[] args) {
        OpeningBookParser.parseFile();
    }

}
