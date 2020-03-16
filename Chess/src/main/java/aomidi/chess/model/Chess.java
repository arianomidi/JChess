package aomidi.chess.model;

public class Chess {
    private Game game;
    private boolean standardGame;
    private boolean flipBoard;
    private static String boardColor;
    private String error;

    // ----------- Constructors -------------

    public Chess() {
        this.standardGame = false;
        this.flipBoard = true;
        // Grey:37 Green:32 Black:30
        boardColor = "32m";

        this.game = new Game(this);
        this.error = "";
    }

    // ----------- Getters -------------

    public Game getGame() {
        return game;
    }

    public boolean isStandardGame() {
        return standardGame;
    }

    public boolean flipBoardSelected() {
        return flipBoard;
    }

    public static String getBoardColor() {
        return "\033[0;" + boardColor;
    }

    public static int getLen() {
        return getBoardColor().length();
    }

    // ----------- Main -------------

    public void startGame() {
        this.game.playGame();
    }

}
