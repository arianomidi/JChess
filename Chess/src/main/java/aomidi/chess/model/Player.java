package aomidi.chess.model;
import aomidi.chess.model.Util.Color;

public class Player {
    private Color color;
    private Game game;

    public Player(Color color, Game game){
        this.color = color;
        this.game = game;
    }

    // Getters
    public Color getColor() { return color; }

    public Game getGame() { return game; }
}
