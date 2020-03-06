package aomidi.chess.model;
import aomidi.chess.model.Util.Color;

public class Player {
    private Color color;
    private Game game;
    private boolean firstMove;
    private boolean wasChecked;
    private boolean isChecked;

    // ----------- Constructors -------------
    public Player(Color color, Game game){
        this.color = color;
        this.game = game;
        this.firstMove = true;
        this.isChecked = false;
        this.wasChecked = false;
    }

    // ----------- Getters -------------

    public Color getColor() { return color; }

    public Game getGame() { return game; }

    public boolean isFirstMove() { return firstMove; }

    public boolean wasKingChecked() {
        return wasChecked;
    }

    public boolean isKingChecked() {
        return isChecked;
    }

    // ----------- Setters -------------

    public void setFirstMove(boolean isFirstMove) {
        this.firstMove = isFirstMove;
    }

    public void setChecked(boolean checked) {
        this.wasChecked = this.isChecked;
        this.isChecked = checked;
    }

}
