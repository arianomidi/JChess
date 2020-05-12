package aomidi.chess.ai.model;

import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public class Chess {
    private Game game;

    // ----------- Constructors -------------

    public Chess() throws MoveGeneratorException {
        this.game = new Game(this);
    }

    // ----------- Getters -------------

    public Game getGame() {
        return game;
    }

    // ----------- Main -------------

    public void startGame() throws MoveGeneratorException {
        this.game.playGame();
    }

}
