package aomidi.chess.ai.model;

import aomidi.chess.ai.model.Util.*;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public abstract class Player {
    private Side side;
    private Game game;

    // ----------- Constructors -------------
    public Player(Side side, Game game) {
        this.side = side;
        this.game = game;
    }

    // ----------- Getters -------------

    public Game getGame() { return game; }

    public Side getSide() {
        return side;
    }

    public Color getColor() { return Util.getColor(side); }

    // ----------- Action -------------

    abstract public boolean movePiece(String input) throws MoveGeneratorException;

}
