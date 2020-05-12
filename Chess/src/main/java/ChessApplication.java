import aomidi.chess.ai.model.*;
import java.util.Scanner;

import static aomidi.chess.ai.model.Util.input;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;

public class ChessApplication {
    // Main
    public static void main(String[] args) throws MoveGeneratorException {
        Chess chess = new Chess();

        chess.startGame();

//        // Creates a new chessboard in the standard initial position
//        Board board = new Board();
//
//        //Make a move from E2 to E4 squares
//        board.doMove(new Move(Square.E2, Square.E4));
//
//        //print the chessboard in a human-readable form
//        System.out.println(board.toString());
//
//        //Make a move from E7 to E5 squares
//        board.doMove(new Move(Square.E7, Square.E5));
//
//        //print the chessboard in a human-readable form
//        System.out.println(board.toString());
//
//        MoveList moves = MoveGenerator.generateLegalMoves(board);
//        System.out.println("Legal moves: " + moves);

    }

}
