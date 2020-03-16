package aomidi.chess.model;
import aomidi.chess.model.Util.Color;

import java.util.ArrayList;

import static aomidi.chess.model.Util.boldAndUnderline;
import static aomidi.chess.model.Util.replaceString;

public class Player {
    private Color color;
    private Game game;
    private boolean firstMove;
    private King king;
    private boolean isChecked;


    // ----------- Constructors -------------
    public Player(Color color, Game game){
        this.color = color;
        this.game = game;
        this.firstMove = true;
        this.isChecked = false;
        this.king = (King) game.getBoard().getPiecesOfType(Util.PieceType.King, color).get(0);
    }

    // ----------- Getters -------------

    public Color getColor() { return color; }

    public boolean isFirstMove() { return firstMove; }

    public King getKing(){ return king; }

    // ----------- Setters -------------

    public void setFirstMove(boolean isFirstMove) {
        this.firstMove = isFirstMove;
    }

    public void setChecked(boolean checked) { this.isChecked = checked; }

    // ----------- Checkers -------------

    public boolean isUnderCheck() {
        ArrayList<Piece> attacking_pieces = game.getBoard().getOpposingPieces(king.getColor());

        for (Piece p: attacking_pieces){
            if (game.getBoard().isAttacking(p, king)){
                return true;
            }
        }

        return false;
    }

    public boolean isCheckmated() {
        return false;
//        if (whitePlayer.isKingChecked() && whitePlayer.wasKingChecked()) {
//            // Change last move notation to checkmate
//            Move last_move = movesList.get(movesList.size() - 1);
//            String new_move_str = (String) last_move.getMove().get("string");
//            last_move.getMove().replace("string", replaceString(new_move_str, "#", new_move_str.length() - 1));
//
//            gameOver = true;
//            System.out.println(boldAndUnderline("Checkmate: Black Wins\n") + "\033[0m");
//            return true;
//        } else if (blackPlayer.isKingChecked() && blackPlayer.wasKingChecked()) {
//            // Change last move notation to checkmate
//            Move last_move = movesList.get(movesList.size() - 1);
//            String new_move_str = (String) last_move.getMove().get("string");
//            last_move.getMove().replace("string", replaceString(new_move_str, "#", new_move_str.length() - 1));
//
//            gameOver = true;
//            System.out.println(boldAndUnderline("Checkmate: White Wins\n") + "\033[0m");
//            return true;
//        } else
//            return false;
    }

}
