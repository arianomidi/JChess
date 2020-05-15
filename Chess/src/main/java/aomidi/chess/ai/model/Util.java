package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.*;

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

    public static final Square[][] SQUARES = {
            {Square.A1,  Square.A2,  Square.A3,  Square.A4,  Square.A5,  Square.A6,  Square.A7,  Square.A8},
            {Square.B1,  Square.B2,  Square.B3,  Square.B4,  Square.B5,  Square.B6,  Square.B7,  Square.B8},
            {Square.C1,  Square.C2,  Square.C3,  Square.C4,  Square.C5,  Square.C6,  Square.C7,  Square.C8},
            {Square.D1,  Square.D2,  Square.D3,  Square.D4,  Square.D5,  Square.D6,  Square.D7,  Square.D8},
            {Square.E1,  Square.E2,  Square.E3,  Square.E4,  Square.E5,  Square.E6,  Square.E7,  Square.E8},
            {Square.F1,  Square.F2,  Square.F3,  Square.F4,  Square.F5,  Square.F6,  Square.F7,  Square.F8},
            {Square.G1,  Square.G2,  Square.G3,  Square.G4,  Square.G5,  Square.G6,  Square.G7,  Square.G8},
            {Square.H1,  Square.H2,  Square.H3,  Square.H4,  Square.H5,  Square.H6,  Square.H7,  Square.H8},
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

    public static Color getColor(Side side) {
        switch (side) {
            case WHITE:
                return Color.White;
            case BLACK:
                return Color.Black;
            default:
                throw new java.lang.IllegalArgumentException("Illegal Side: " + side);
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

    public static String getSimpleString(String string){
        String output = string;
        while (output.contains("\033")) {
            output = output.replace(output.substring(output.indexOf("\033"), output.indexOf('m', output.indexOf("\033")) + 1), "");
        }
        return output;
    }

    public static String replaceString(String string, String substring, int from, int to) {
        int strlen = string.length();
        String s1 = string.substring(0, from - 1 + Chess.getLen());
        String s2 = string.substring(to + Chess.getLen(), strlen);
        String s = s1 + substring + s2;
        return s + Chess.getBoardColor();
    }

    public static String insertPiece(String string, String substring, int from) {
        String simpleString = getSimpleString(string);

        String s1 = simpleString.substring(0, from);
        String s2 = simpleString.substring(from + substring.length(), simpleString.length() - 1) + boardColor("|");

        String output;
        if (string.contains("\033[107m"))
            output = boardColor(color(s1, 107)) + bold(substring) + boardColor(color(s2, 107));
        else
            output = boardColor(s1) + bold(substring) + boardColor(s2);

//        return output.replace(".", color(".", 107) + Chess.getBoardColor());
        return output;

    }

    public static String insertPiece(String string, String substring, int insertFrom, int underlineAt, int underlineLength) {
        String simpleString = getSimpleString(string);

        String s1 = simpleString.substring(0, insertFrom);
        String s2 = simpleString.substring(insertFrom + substring.length(), simpleString.length() - 1) + boardColor("|");

        int underlineIndex = underlineAt - s1.length();
        String pieceString = bold(substring.substring(0, underlineIndex))
                + boldAndUnderline(substring.substring(underlineIndex, underlineIndex + underlineLength))
                + bold(substring.substring(underlineIndex + underlineLength));

        String output;
        if (string.contains("\033[107m"))
            output = boardColor(color(s1, 107)) + bold(pieceString) + boardColor(color(s2, 107));
        else
            output = boardColor(s1) + bold(pieceString) + boardColor(s2);
//        return output.replace(".", color(".", 107) + Chess.getBoardColor());
        return output;
    }

    public static String insertPieceLastColumn(String string, String substring, int insertFrom, int underlineAt, int underlineLength) {
        String simpleString = getSimpleString(string);

        String s1 = simpleString.substring(0, insertFrom);
        String s2 = simpleString.substring(insertFrom + substring.length(), simpleString.length() - 1) + boardColor("|");

        int underlineIndex = underlineAt - s1.length();
        String pieceString = bold(substring.substring(0, underlineIndex))
                + boldAndUnderline(substring.substring(underlineIndex, underlineIndex + underlineLength))
                + bold(substring.substring(underlineIndex + underlineLength));

        String output;
        if (string.contains("\033[107m"))
            output = boardColor("\033[4m" + color(s1, 107)) + pieceString + boardColor("\033[4m" + color(s2, 107));
        else
            output = boardColor("\033[4m" + s1) + pieceString + boardColor("\033[4m" + s2);

//        return output.replace(".", color(".", 107) + Chess.getBoardColor());
        return output;
    }

    public static String insertPieceLastColumn(String string, String substring, int from) {
        String simpleString = getSimpleString(string);

        String s1 = "\033[4m" + simpleString.substring(0, from);
        String s2 = "\033[4m" + simpleString.substring(from + substring.length(), simpleString.length() - 1) + boardColor("|");

        String output;
        if (string.contains("\033[107m"))
            output = boardColor(color(s1, 107)) + bold(substring) + boardColor(color(s2, 107));
        else
            output = boardColor(s1) + bold(substring) + boardColor(s2);
//        return output.replace(".", color(".", 107) + Chess.getBoardColor());
        return output;
    }


    // ---------- Color Manipulation ------------
    public static String makeBoardColor(String string) {
        return Chess.getBoardColor() + string.substring(string.indexOf('m') + 1) +  "\033[0m";
    }

    public static String boardColor(String string){
        return Chess.getBoardColor() + string + "\033[0m";
    }

    public static String boldBoardColor(String string) {
        return Chess.getBoldBoardColor() + string.substring(string.indexOf('m') + 1) +  "\033[0m";
    }

    public static String makeDefaultColor(String string) {
        return "\033[0m" + string.substring(string.indexOf('m') + 1);
    }

    public static String color(String string, int color){
        return "\033[" + color + "m" + string + "\033[0m";
    }

    public static String defaultColor(String string) {
        return "\033[0m" + string;
    }

    // ---------- Font Manipulation ------------
    public static String bold(String string) {
        return "\033[0;1m" + string + "\033[0m";
    }

    public static String boldPiece(String string) {
        return "\033[0;1m" + string + Chess.getBoardColor();
    }

    public static String boldAndUnderline(String string) {
        return "\033[0m\033[1;4m" + string + Chess.getBoardColor();
    }

    public static String underline(String string) {
        return "\033[4m" + string + Chess.getBoardColor();
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
            }
        }

        return eval;
    }

    // ---------- Board GUI ------------

    public static String getBoardFEN(Board board){
        String FEN = board.getFen();

        return FEN.substring(0, FEN.indexOf(' '));
    }

    public static void printBoard(Board board){
        String string = Chess.getBoardColor() + "  " + underline("                                                                                                \n");

        for (int rank = 7; rank >= 0; rank--) {
            for (int column = 0; column < 6; column++) {
                for (int file = -1; file <= 8; file++) {
                    if (file == -1) {
                        string += " |";
                    } else if (file != 8) {
                        string += getSymbol(file, rank, column, board);
                    } else {
                        if (column == 3) {
                            string += boldPiece("   " + (rank + 1));
                        }
                    }
                }
                string += "\n";
            }
        }
        string += bold("\n       A           B           C           D           E           F           G           H\n");


        System.out.println(string);
    }

    public static void printBoardAndMoves(Board board, Game game){
        Move move = game.getCurMove();
        String string = Chess.getBoardColor() + "  " + underline("                                                                                                \n");

        for (int rank = 7; rank >= 0; rank--) {
            for (int column = 0; column < 6; column++) {
                for (int file = -1; file <= 8; file++) {
                    if (file == -1) {
                        string += makeBoardColor(" |");
                    } else if (file != 8) {
                        if (move != null)
                            string += getSymbol(file, rank, column, board, move);
                        else
                            string += getSymbol(file, rank, column, board);

                    } else {
                        if (column == 3) {
                            string += boldPiece("   " + (rank + 1));
                        } else {
                            string += "    ";
                        }

                        if (rank == 7 && column == 0) {
                            string += "           "  + boldAndUnderline("Moves:");
                        } else if (column % 2 == 0)
                            string += "           " + getMoveNotationRow(rank, column, game);
                    }
                }
                string += "\n";
            }
        }
        string += bold("\n       A           B           C           D           E           F           G           H\n");


        System.out.println(string);
    }

    public static String getMoveNotationRow(int rank, int column, Game game){
        String moveNotationRow = "";

        for (int factor = 0; factor < game.getMoves().size(); factor += 23 * 2 ){
            int whiteMoveNum = ((7-rank) * 3 + (column / 2) - 1) * 2 + factor;

            moveNotationRow += getMoveNotation(whiteMoveNum, game);
        }

        return moveNotationRow;
    }

    public static String getMoveNotation(int whiteMoveNum, Game game){
        String moveNotation = "";
        int blackMoveNum = whiteMoveNum + 1;
        if (whiteMoveNum < game.getMoves().size()) {
            moveNotation += bold(((whiteMoveNum / 2 + 1) + ". ")) + game.getMoves().get(whiteMoveNum).getMoveNotation() + " ";
            if (blackMoveNum < game.getMoves().size())
                moveNotation += game.getMoves().get(blackMoveNum).getMoveNotation();
        }

        int colorChangersLength = moveNotation.length() - getSimpleString(moveNotation).length();

        while (moveNotation.length() - colorChangersLength < 23)
            moveNotation += " ";

        return moveNotation;
    }

    public static String getSymbol(int file, int rank, int column, Board board){
        Square square = SQUARES[file][rank];
        Piece piece = board.getPiece(square);

        if (piece != Piece.NONE){
            return color(getPieceSymbol(piece, square, column),47);
        } else
            return getSquareSymbol(square, column);
    }

    public static String getSquareSymbol(Square square, int column){
        switch (column) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                if (square.isLightSquare())
                    return Chess.getBoardColor() + "           |";
                else
                    return Chess.getBoardColor() + " / / / / / |";
            case 5:
                if (square.isLightSquare())
                    return Chess.getBoardColor() + underline("           |");
                else
                    return Chess.getBoardColor() + underline(" / / / / / |");
            default:
                throw new IllegalArgumentException("Column out of range: " + column);
        }
    }

    public static String getPieceSymbol(Piece piece, Square square, int column) {
        String string = getSquareSymbol(square, column);

        switch (piece.getPieceType()) {
            case PAWN:
                switch (column) {
                    case 0:
                    case 1:
                        return string;
                    case 2:
                        return insertPiece(string, "()", 5);
                    case 3:
                        return insertPiece(string, ")(", 5);
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{__}", 4);
                        } else {
                            return insertPiece(string, "{XX}", 4, 5, 2);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case KNIGHT:
                switch (column) {
                    case 0:
                        return string;
                    case 1:
                        return insertPiece(string, "_,,", 3);
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "\"-==\\~", 2);
                        } else {
                            return insertPiece(string, "\"-XX\\~", 2);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{___}", 3);
                        } else {
                            return insertPiece(string, "{/X\\}", 3, 4, 3);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case BISHOP:
                switch (column) {
                    case 0:
                        return insertPiece(string, ",", 5);
                    case 1:
                        return insertPiece(string, "(^)", 4);
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "/ \\", 4);
                        } else {
                            return insertPiece(string, "/|\\", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{|}", 4);
                        } else {
                            return insertPiece(string, "{X}", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{___}", 3);
                        } else {
                            return insertPiece(string, "{/X\\}", 3, 4, 3);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case ROOK:
                switch (column) {
                    case 0:
                        return string;
                    case 1:
                        return insertPiece(string, "UUU", 4);
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "[ ]", 4);
                        } else {
                            return insertPiece(string, "[\\]", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")|(", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{___}", 3);
                        } else {
                            return insertPiece(string, "{/X\\}", 3, 4, 3);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case QUEEN:
                switch (column) {
                    case 0:
                        return insertPiece(string, "*", 5);
                    case 1:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ")_(", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{|}", 4);
                        } else {
                            return insertPiece(string, "{|}", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "/_\\", 4);
                        } else {
                            return insertPiece(string, "/|\\", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 5:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPieceLastColumn(string, "{___}", 3);
                        } else {
                            return insertPieceLastColumn(string, "{/X\\}", 3, 4, 3);
                        }
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case KING:
                switch (column) {
                    case 0:
                        return insertPiece(string, "+", 5);
                    case 1:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "\\_/", 4);
                        } else {
                            return insertPiece(string, "\\X/", 4);
                        }
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{|}", 4);
                        } else {
                            return insertPiece(string, "{|}", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "/_\\", 4);
                        } else {
                            return insertPiece(string, "/|\\", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 5:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPieceLastColumn(string, "{___}", 3);
                        } else {
                            return insertPieceLastColumn(string, "{/X\\}", 3, 4, 3);
                        }
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            default:
                throw new IllegalArgumentException("Invalid Piece Type" + piece.getPieceType());

        }
    }



    public static String getSymbol(int file, int rank, int column, Board board, Move move){
        Square square = SQUARES[file][rank];
        Piece piece = board.getPiece(square);

        if (piece != Piece.NONE){
            return getPieceSymbolMove(piece, square, column, move);
        } else {
            return getSquareSymbolMove(square, column, move);
        }

    }

    public static String getSquareSymbolMove(Square square, int column, Move move){
        if (move.getFrom() == square || move.getTo() == square)
            switch (column) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    if (square.isLightSquare())
//                        return Chess.getBoardColor() +   ". . . . . .|".replace(".", color("." , 107) + Chess.getBoardColor());
                        return Chess.getBoardColor() +   color("           ", 107) + Chess.getBoardColor() + "|";

                //                    return Chess.getBoardColor() + "           |";
                    else
//                        return Chess.getBoardColor() + " / / / / / |" + Chess.getBoardColor();
//                        return Chess.getBoardColor() + "./././././.|".replace(".", color("." , 107) + Chess.getBoardColor());
                        return Chess.getBoardColor() +   color(" / / / / / ", 107) + Chess.getBoardColor() + "|";
                case 5:
                    if (square.isLightSquare())
//                        return Chess.getBoardColor() + underline("           |");
                        return Chess.getBoardColor() +   underline(color("           ", 107) + Chess.getBoardColor() + "|");
                    else
//                        return Chess.getBoardColor() + underline(" / / / / / |");
                        return Chess.getBoardColor() +   underline(color(" / / / / / ", 107) + Chess.getBoardColor() + "|");
                default:
                    throw new IllegalArgumentException("Column out of range: " + column);
            }
        else
            return getSquareSymbol(square, column);
    }

    public static String getPieceSymbolMove(Piece piece, Square square, int column, Move move) {
        String string = getSquareSymbolMove(square, column, move);

        switch (piece.getPieceType()) {
            case PAWN:
                switch (column) {
                    case 0:
                    case 1:
                        return string;
                    case 2:
                        return insertPiece(string, "()", 5);
                    case 3:
                        return insertPiece(string, ")(", 5);
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{__}", 4);
                        } else {
                            return insertPiece(string, "{XX}", 4, 5, 2);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case KNIGHT:
                switch (column) {
                    case 0:
                        return string;
                    case 1:
                        return insertPiece(string, "_,,", 3);
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "\"-==\\~", 2);
                        } else {
                            return insertPiece(string, "\"-XX\\~", 2);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{___}", 3);
                        } else {
                            return insertPiece(string, "{/X\\}", 3, 4, 3);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case BISHOP:
                switch (column) {
                    case 0:
                        return insertPiece(string, ",", 5);
                    case 1:
                        return insertPiece(string, "(^)", 4);
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "/ \\", 4);
                        } else {
                            return insertPiece(string, "/|\\", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{|}", 4);
                        } else {
                            return insertPiece(string, "{X}", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{___}", 3);
                        } else {
                            return insertPiece(string, "{/X\\}", 3, 4, 3);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case ROOK:
                switch (column) {
                    case 0:
                        return string;
                    case 1:
                        return insertPiece(string, "UUU", 4);
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "[ ]", 4);
                        } else {
                            return insertPiece(string, "[\\]", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")|(", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{___}", 3);
                        } else {
                            return insertPiece(string, "{/X\\}", 3, 4, 3);
                        }
                    case 5:
                        return string;
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case QUEEN:
                switch (column) {
                    case 0:
                        return insertPiece(string, "*", 5);
                    case 1:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ")_(", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{|}", 4);
                        } else {
                            return insertPiece(string, "{|}", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "/_\\", 4);
                        } else {
                            return insertPiece(string, "/|\\", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 5:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPieceLastColumn(string, "{___}", 3);
                        } else {
                            return insertPieceLastColumn(string, "{/X\\}", 3, 4, 3);
                        }
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            case KING:
                switch (column) {
                    case 0:
                        return insertPiece(string, "+", 5);
                    case 1:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "\\_/", 4);
                        } else {
                            return insertPiece(string, "\\X/", 4);
                        }
                    case 2:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "{|}", 4);
                        } else {
                            return insertPiece(string, "{|}", 4);
                        }
                    case 3:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, "/_\\", 4);
                        } else {
                            return insertPiece(string, "/|\\", 4);
                        }
                    case 4:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPiece(string, ") (", 4);
                        } else {
                            return insertPiece(string, ")X(", 4);
                        }
                    case 5:
                        if (piece.getPieceSide() == Side.WHITE) {
                            return insertPieceLastColumn(string, "{___}", 3);
                        } else {
                            return insertPieceLastColumn(string, "{/X\\}", 3, 4, 3);
                        }
                    default:
                        throw new IllegalArgumentException("Column out of range: " + column);
                }
            default:
                throw new IllegalArgumentException("Invalid Piece Type" + piece.getPieceType());

        }
    }



}
