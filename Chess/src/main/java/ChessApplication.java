import aomidi.chess.model.*;
import java.util.Scanner;

import static aomidi.chess.model.Util.input;

public class ChessApplication {
    // Main
    public static void main(String[] args) {
        Chess chess = new Chess();
        Game game = chess.getGame();
        Board board = game.getBoard();
        System.out.println(board.toString());

        while (true) {
            chess.movePiece();
        }
    }

}
