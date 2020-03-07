package aomidi.chess.model;

import java.util.Scanner;

public class Util {
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

    public enum FileLetter {
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H
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

    public static Color getOpposingColor(Color color){
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

    public static boolean isFile(Character s){
        s = Character.toUpperCase(s);
        switch (s){
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

    public static boolean isRank(Character c){
        if ('0' < c && c <= '8')
            return true;
        else
            return false;
    }


    // ---------- String Manipulation ------------

    public static String input(String msg){
        System.out.println(msg);
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        return input;
    }

    public static String bold(String string){
        return "\033[0;1m" + string + "\033[0;0m";
    }

    public static String underline(String string){
        return "\033[0;4m" + string + "\033[0;0m";
    }

    public static String boldAndUnderline(String string){
        return"\033[1;4m" + string + "\033[0;0m";
    }

    public static String replaceString(String string, String substring, int from, int to){
        int strlen = string.length();
        String s1 = string.substring(0, from - 1);
        String s2 = string.substring(to, strlen);
        String s = s1 + substring + s2;
        return s;
    }

}
