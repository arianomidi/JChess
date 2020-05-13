package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.*;
import java.util.stream.Collectors;

public class Util {
    public static final String StandardStartingPosition = " r n b q k b n r  p p p p p p p p    .   .   .   .  .   .   .   .      .   .   .   .  .   .   .   .    P P P P P P P P  R N B Q K B N R ";

    // ----------- Enumeration -------------

    public enum Color {
        Black,
        White
    }

    public enum PieceType {
        Pawn,
        Knight,
        Bishop,
        Rook,
        Queen,
        King
    }

    // ----------- Piece Valuation Arrays -------------

    private static final double[][] pawnEvalWhite = {
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
            {5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0},
            {1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
            {0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
            {0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
            {0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
            {0.5,  1.0,  1.0, -2.0, -2.0,  1.0,  1.0,  0.5},
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
    };

    private static final double[][] knightEval = {
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
            {-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
            {-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
            {-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
            {-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
            {-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
            {-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
    };

    private static final double[][] bishopEvalWhite = {
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
            { -1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
            { -1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
            { -1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
            { -1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
            { -1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
    };

    private static final double[][] rookEvalWhite = {
            {  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
            {  0.5,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            { -0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
            {  0.0,   0.0, 0.0,  0.5,  0.5,  0.0,  0.0,  0.0}
    };

    private static final double[][] evalQueen = {
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
            { -1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
            { -0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
            {  0.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
            { -1.0,  0.5,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
            { -1.0,  0.0,  0.5,  0.0,  0.0,  0.0,  0.0, -1.0},
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
    };

    private static final double[][] kingEvalWhite = {
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            { -2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0},
            { -1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
            {  2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0},
            {  2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0}
    };

    public static double[][] reverseArray(double[][] array){
        double[][] newArray = new double[8][8];

        for (int i = 7; i >= 0; i--)
            for (int j = 0; j < 8; j++)
                newArray[7-i][j] = -array[i][j];

        return newArray;
    }

    public static double[][] negativeArray(double[][] array){
        double[][] newArray = new double[8][8];

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                newArray[i][j] = -array[i][j];

        return newArray;
    }

    // ----------- Conversion Functions -------------

    public static String intToLetter(int file) {
        switch (file) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
            case 8:
                return "H";
            default:
                throw new java.lang.IllegalArgumentException("Illegal file number: " + file);
        }
    }

    public static int letterToInt(String file) {
        file = file.toUpperCase();
        switch (file) {
            case "A":
                return 1;
            case "B":
                return 2;
            case "C":
                return 3;
            case "D":
                return 4;
            case "E":
                return 5;
            case "F":
                return 6;
            case "G":
                return 7;
            case "H":
                return 8;
            default:
                throw new java.lang.IllegalArgumentException("Illegal file letter: " + file);
        }
    }

    public static int fileToInt(File file) {
       return letterToInt(file.getNotation()) - 1;
    }

    public static String getTypeLetter(PieceType type) {
        switch (type) {
            case Pawn:
                return "P";
            case Knight:
                return "N";
            case Bishop:
                return "B";
            case Rook:
                return "R";
            case Queen:
                return "Q";
            case King:
                return "K";
            default:
                throw new java.lang.IllegalArgumentException("Illegal PieceType: " + type);
        }
    }

    public static PieceType getPieceType(String piece) {
        piece = piece.toUpperCase();
        switch (piece) {
            case "P":
                return PieceType.Pawn;
            case "N":
                return PieceType.Knight;
            case "B":
                return PieceType.Bishop;
            case "R":
                return PieceType.Rook;
            case "Q":
                return PieceType.Queen;
            case "K":
                return PieceType.King;
            default:
                throw new java.lang.IllegalArgumentException("Illegal Input: " + piece);
        }
    }

    public static String getColorLetter(Color color) {
        switch (color) {
            case Black:
                return "b";
            case White:
                return "w";
            default:
                throw new java.lang.IllegalArgumentException("Illegal Color: " + color);
        }
    }

    public static Color getOpposingColor(Color color) {
        switch (color) {
            case Black:
                return Color.White;
            case White:
                return Color.Black;
            default:
                throw new java.lang.IllegalArgumentException("Illegal Color: " + color);
        }
    }

    // ----------- Checkers -------------

    public static boolean isFile(Character s) {
        s = Character.toUpperCase(s);
        switch (s) {
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
                return true;
            default:
                return false;
        }
    }

    public static boolean isRank(Character c) {
        return '0' < c && c <= '8';
    }

    // ---------- String Manipulation ------------

    public static String input(String msg) {
        System.out.println(msg);
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

//    public static String bold(String string) {
//        return "\033[0;1m" + string + Chess.getBoardColor();
//    }
//
//    public static String underline(String string) {
//        return "\033[4m" + string + Chess.getBoardColor();
//    }
//
//    public static String boldAndUnderline(String string) {
//        return "\033[0m\033[1;4m" + string + Chess.getBoardColor();
//    }
//
//    public static String replaceString(String string,   String substring, int from, int to) {
//        int strlen = string.length();
//        String s1 = string.substring(0, from - 1 + Chess.getLen());
//        String s2 = string.substring(to + Chess.getLen(), strlen);
//        String s = s1 + substring + s2;
//        return s + Chess.getBoardColor();
//    }

    public static String bold(String string) {
        return "\033[0;1m" + string + "\033[0m";
    }

    public static String boldAndUnderline(String string) {
        return "\033[0m\033[1;4m" + string + "\033[0m";
    }

    public static String replaceString(String string, String substring, int from) {
        int strlen = string.length();
        String s1 = string.substring(0, from);
        String s2 = string.substring(from + substring.length(), strlen);
        return s1 + substring + s2;
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------- Piece Evaluation ------------

    public static int getPieceValue(Piece piece){
        int value = 0;

        switch (piece.getPieceType()){
            case PAWN:
                value = 10; break;
            case KNIGHT: case BISHOP:
                value = 30; break;
            case ROOK:
                value = 50; break;
            case QUEEN:
                value = 90; break;
            case KING:
                value = 900; break;
        }

        if (piece.getPieceSide() == Side.BLACK)
            return -value;
        else
            return value;
    }

    private static double getEvalFactor(Piece piece, Square square){
        double[][] pieceEval;
        switch (piece.getPieceType()){
            case PAWN:
                pieceEval = pawnEvalWhite;
                if (piece.getPieceSide() == Side.BLACK)
                    pieceEval = reverseArray(pieceEval);
                break;
            case KNIGHT:
                pieceEval = knightEval;
                if (piece.getPieceSide() == Side.BLACK)
                    pieceEval = negativeArray(pieceEval);
                break;
            case BISHOP:
                pieceEval = bishopEvalWhite;
                if (piece.getPieceSide() == Side.BLACK)
                    pieceEval = reverseArray(pieceEval);
                break;
            case ROOK:
                pieceEval = rookEvalWhite;
                if (piece.getPieceSide() == Side.BLACK)
                    pieceEval = reverseArray(pieceEval);
                break;
            case QUEEN:
                pieceEval = evalQueen;
                if (piece.getPieceSide() == Side.BLACK)
                    pieceEval = negativeArray(pieceEval);
                break;
            case KING:
                pieceEval = kingEvalWhite;
                if (piece.getPieceSide() == Side.BLACK)
                    pieceEval = reverseArray(pieceEval);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + piece.getPieceType());
        }


        //System.out.println(8 - Integer.valueOf(square.getRank().getNotation())  + "-" + (fileToInt(square.getFile())));
        return pieceEval[8 - Integer.parseInt(square.getRank().getNotation()) ][fileToInt(square.getFile())];
    }



    public static ArrayList<Piece> getAllPieces(Board board){
        ArrayList<Piece> pieces = new ArrayList<>();
        Square[] squares = Square.values();

        for (int i = 0; i < 64; i++) {
            Piece piece = board.getPiece(squares[i]);
            if (piece != Piece.NONE)
                pieces.add(piece);
        }

        return pieces;
    }

    public static double evaluateBoard(Board board){
        double eval = 0.0;

        Square[] squares = Square.values();

        for (int i = 0; i < 64; i++) {
            Piece piece = board.getPiece(squares[i]);
            if (piece != Piece.NONE) {
                eval += getPieceValue(piece) + getEvalFactor(piece, squares[i]);
                //System.out.format("Piece: %s-%s - Eval: %d + %f = %f\n", piece.toString(), squares[i].toString(), getPieceValue(piece), getEvalFactor(piece, squares[i]),  getPieceValue(piece) + getEvalFactor(piece, squares[i]));
            }

        }

        return eval;
    }

    // ---------- Move Validation ------------

    public static  HashMap<Move, Double> findBestMoves(Board board) throws MoveGeneratorException {
        MoveList moves = MoveGenerator.generateLegalMoves(board);
        HashMap<Move, Double> sortedMap;

        HashMap<Move, Double> moveMap = new HashMap<>();

        for (Move move : moves){
            board.doMove(move);
            moveMap.put(move, evaluateBoard(board));
            board.undoMove();
        }

        if (board.getSideToMove() == Side.WHITE) {
            sortedMap = moveMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        } else {
            sortedMap = moveMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        }

        return sortedMap;
    }

    public static void printLegalMovesEvals(Board board) throws MoveGeneratorException {
        System.out.print("\033[1mLegal moves: \033[0m");
        findBestMoves(board).forEach((key, value) -> System.out.print(key + ":" + value + ", "));
        System.out.print("\n");
    }

    public static Double getMoveEvalutation(Move move, Board board){
        board.doMove(move);
        Double eval = evaluateBoard(board);
        board.undoMove();

        return eval;
    }
}
