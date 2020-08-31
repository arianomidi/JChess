package aomidi.chess.gui;

import aomidi.chess.ai.model.Engine;
import aomidi.chess.ai.model.Game;
import aomidi.chess.ai.model.MoveLog;
import aomidi.chess.ai.model.Player;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.game.PlayerType;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static aomidi.chess.ai.model.Util.getPromotionPiece;
import static javax.swing.JFrame.setDefaultLookAndFeelDecorated;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private Board chessBoard;
    private final Engine engine;
    private final MoveLog moveLog;

    private final JFrame gameFrame;
    private final GameSetup gameSetup;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;

    private BoardOrientation boardOrientation;
    private boolean highlightLegalMoves;

    private Square sourceTile = Square.NONE;
    private Square destinationTile = Square.NONE;;
    private Piece humanMovedPiece = Piece.NONE;

    private static Dimension FRAME_DIMENSION = new Dimension(820,700);
    private static Dimension BOARD_PANEL_SIZE = new Dimension(640, 640);
    private static Dimension TILE_PANEL_SIZE = new Dimension(BOARD_PANEL_SIZE.width / 8, BOARD_PANEL_SIZE.height / 8);


    private static final Color defaultLightTileColor = Color.decode("#ECD9B9");
    private static final Color defaultDarkTileColor = Color.decode("#AE8968");
    private Color lightTileColor = Color.decode("#ECD9B9");
    private Color darkTileColor = Color.decode("#AE8968");
    private final Color lightTileSelectedColor = Color.decode("#8A966F");
    private final Color darkTileSelectedColor = Color.decode("#6B6E47");
    private final Color darkTilePreviousMoveColor = Color.decode("#AAA256");
    private final Color lightTilePreviousMoveColor = Color.decode("#CFD186");
    private static String pieceIconPath = "resources/art/pieces/plain/";
    private Move computerMove;

    private static final Table INSTANCE = new Table();

    private Table(){
        this.chessBoard = new Board();
        this.moveLog = new MoveLog();

        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(FRAME_DIMENSION);
        this.gameFrame.setJMenuBar(populateMenuBar());

        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();

        this.boardPanel = new BoardPanel();
        this.boardOrientation = BoardOrientation.WHITE_DOWN;
        this.highlightLegalMoves = true;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        setDefaultLookAndFeelDecorated(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        center(this.gameFrame);
        this.gameFrame.setVisible(true);

        this.engine = new Engine(gameSetup.getSearchDepth());
        this.addObserver(new TableGameAIObserver());
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    public void redraw(){
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }

    private static void center(final JFrame frame) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        frame.setLocation(x, y);
    }

    private JFrame getGameFrame() {
        return this.gameFrame;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    private Engine getEngine() {
        return this.engine;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private boolean getHighlightLegalMoves() {
        return this.highlightLegalMoves;
    }

    private void setupUpdate(GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }

    // --- Menu Bar Functions --- //

    private JMenuBar populateMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createPreferencesMenu());
        menuBar.add(createOptionsMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(e -> System.out.println("PGN Reading Not Added Yet"));
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");

        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardOrientation = boardOrientation.flip();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoard);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem highlightMovesItem = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        highlightMovesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = highlightMovesItem.isSelected();
            }
        });
        preferencesMenu.add(highlightMovesItem);

        preferencesMenu.addSeparator();

        final JMenu chessMenChoiceSubMenu = new JMenu("Choose Piece Set");

        final JCheckBoxMenuItem defaultMenuItem = new JCheckBoxMenuItem("Plain (Default)", true);
        chessMenChoiceSubMenu.add(defaultMenuItem);

        final JCheckBoxMenuItem abstractMenMenuItem = new JCheckBoxMenuItem("Abstract");
        chessMenChoiceSubMenu.add(abstractMenMenuItem);

        final JCheckBoxMenuItem holyWarriorsMenuItem = new JCheckBoxMenuItem("Holy");
        chessMenChoiceSubMenu.add(holyWarriorsMenuItem);

        final JCheckBoxMenuItem fancyMenMenuItem = new JCheckBoxMenuItem("Fancy");
        chessMenChoiceSubMenu.add(fancyMenMenuItem);

        final JCheckBoxMenuItem fancyMenMenuItem2 = new JCheckBoxMenuItem("Fancy 2");
        chessMenChoiceSubMenu.add(fancyMenMenuItem2);


        defaultMenuItem.addActionListener(e -> {
            for (Component jCheckBoxMenuItem : chessMenChoiceSubMenu.getMenuComponents()){
                if (jCheckBoxMenuItem instanceof JCheckBoxMenuItem)
                    ((JCheckBoxMenuItem) jCheckBoxMenuItem).setState(false);
            }
            defaultMenuItem.setState(true);

            pieceIconPath = "resources/art/pieces/plain/";
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });

        abstractMenMenuItem.addActionListener(e -> {
            for (Component jCheckBoxMenuItem : chessMenChoiceSubMenu.getMenuComponents()){
                if (jCheckBoxMenuItem instanceof JCheckBoxMenuItem)
                    ((JCheckBoxMenuItem) jCheckBoxMenuItem).setState(false);
            }
            abstractMenMenuItem.setState(true);

            pieceIconPath = "resources/art/pieces/simple/";
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });

        holyWarriorsMenuItem.addActionListener(e -> {
            for (Component jCheckBoxMenuItem : chessMenChoiceSubMenu.getMenuComponents()){
                if (jCheckBoxMenuItem instanceof JCheckBoxMenuItem)
                    ((JCheckBoxMenuItem) jCheckBoxMenuItem).setState(false);
            }
            holyWarriorsMenuItem.setState(true);

            pieceIconPath = "resources/art/pieces/holywarriors/";
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });

        fancyMenMenuItem2.addActionListener(e -> {
            for (Component jCheckBoxMenuItem : chessMenChoiceSubMenu.getMenuComponents()){
                if (jCheckBoxMenuItem instanceof JCheckBoxMenuItem)
                    ((JCheckBoxMenuItem) jCheckBoxMenuItem).setState(false);
            }
            fancyMenMenuItem2.setState(true);

            pieceIconPath = "resources/art/pieces/fancy2/";
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });

        fancyMenMenuItem.addActionListener(e -> {
            for (Component jCheckBoxMenuItem : chessMenChoiceSubMenu.getMenuComponents()){
                if (jCheckBoxMenuItem instanceof JCheckBoxMenuItem)
                    ((JCheckBoxMenuItem) jCheckBoxMenuItem).setState(false);
            }
            fancyMenMenuItem.setState(true);

            pieceIconPath = "resources/art/pieces/fancy/";
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });

        preferencesMenu.add(chessMenChoiceSubMenu);

        final JMenu colorChooserSubMenu = new JMenu("Choose Board Colors");
        colorChooserSubMenu.setMnemonic(KeyEvent.VK_S);

        final JMenuItem defaultColorsMenuItem = new JMenuItem(
                "Default Colors");
        colorChooserSubMenu.add(defaultColorsMenuItem);

        final JMenuItem chooseDarkMenuItem = new JMenuItem("Choose Dark Tile Color");
        colorChooserSubMenu.add(chooseDarkMenuItem);

        final JMenuItem chooseLightMenuItem = new JMenuItem("Choose Light Tile Color");
        colorChooserSubMenu.add(chooseLightMenuItem);

        preferencesMenu.add(colorChooserSubMenu);

        defaultColorsMenuItem.addActionListener(e -> {
            Table.get().getBoardPanel().setTileLightColor(chessBoard, defaultLightTileColor);
            Table.get().getBoardPanel().setTileDarkColor(chessBoard, defaultDarkTileColor);
        });

        chooseDarkMenuItem.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Dark Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setTileDarkColor(chessBoard, colorChoice);
            }
        });

        chooseLightMenuItem.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Light Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setTileLightColor(chessBoard, colorChoice);
            }
        });

        return preferencesMenu;
    }

    private JMenu createOptionsMenu() {

        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);

        final JMenuItem resetMenuItem = new JMenuItem("New Game", KeyEvent.VK_P);
        resetMenuItem.addActionListener(e -> resetGame());
        optionsMenu.add(resetMenuItem);

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game", KeyEvent.VK_S);
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);

        // TODO BOARD EVAL
//        final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board", KeyEvent.VK_E);
//        evaluateBoardMenuItem.addActionListener(e -> System.out.println(StandardBoardEvaluator.get().evaluationDetails(chessBoard, gameSetup.getSearchDepth())));
//        optionsMenu.add(evaluateBoardMenuItem);

        final JMenuItem undoMoveMenuItem = new JMenuItem("Undo last move", KeyEvent.VK_M);
        undoMoveMenuItem.addActionListener(e -> {
            if(Table.get().getMoveLog().size() > 0) {
                undoLastMove();
            }
        });
        optionsMenu.add(undoMoveMenuItem);
        optionsMenu.addSeparator();

        final JCheckBoxMenuItem cbUseBookMoves = new JCheckBoxMenuItem(
                "Use Book Moves", true);
        cbUseBookMoves.addActionListener(e -> engine.setUseOpeningBook(cbUseBookMoves.isSelected()));
        optionsMenu.add(cbUseBookMoves);

        return optionsMenu;
    }

    private static class TableGameAIObserver implements Observer {
        @Override
        public void update(final Observable o,
                           final Object arg) {

            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getSideToMove()) &&
                    !Table.get().getGameBoard().isMated() &&
                    !Table.get().getGameBoard().isStaleMate() && !Table.get().getGameBoard().isDraw()) {
                System.out.println(Table.get().getGameBoard().getSideToMove() + " (AI) is thinking....");
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().isMated()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: " + Table.get().getGameBoard().getSideToMove() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().isStaleMate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: " + Table.get().getGameBoard().getSideToMove() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().isDraw()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Draw", "Draw",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // --- Game Functions --- //

    public void updateComputerMove(Move move){
        this.computerMove = move;
    }

    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }

    private void undoLastMove() {
        this.computerMove = null;
        Table.get().getMoveLog().removeLastMove();
        this.chessBoard.undoMove();

        if (this.gameSetup.isAIPlayer(this.chessBoard.getSideToMove())) {
            Table.get().getMoveLog().removeLastMove();
            this.chessBoard.undoMove();
        }

        redraw();
    }

    private void resetGame() {
        this.computerMove = null;
        Table.get().getMoveLog().clear();
        this.chessBoard = new Board();

        redraw();
    }
    
    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {
        }

        @Override
        protected Move doInBackground() {
            Table.get().getEngine().setDepth(Table.get().getGameSetup().getSearchDepth());
            final Move bestMove = Table.get().getEngine().getBestMove(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().getGameBoard().doMove(bestMove, true);
                Table.get().getMoveLog().addMove(Table.get().getGameBoard().getBackup().getLast());
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.ENGINE);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }


    /* ---- JPANELS ---- */

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8,8));

            this.boardTiles = new ArrayList<>();
            for(int i = 63; i >= 0; i--){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);

                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_SIZE);
            setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
            setBackground(Color.decode("#8B4726"));

            validate();
        }

        // --- Drawing --- //

        public void drawBoard(final Board board){
            removeAll();

            for (final TilePanel tilePanel : boardOrientation.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }

        void setTileDarkColor(final Board board,
                              final Color darkColor) {
            for (final TilePanel boardTile : boardTiles) {
                boardTile.setDarkTileColor(darkColor);
            }
            drawBoard(board);
        }

        void setTileLightColor(final Board board,
                               final Color lightColor) {
            for (final TilePanel boardTile : boardTiles) {
                boardTile.setLightTileColor(lightColor);
            }
            drawBoard(board);
        }
    }


    private class TilePanel extends JPanel{
        private final Square square;

        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());

            this.square = Square.squareAtGUI(tileId);

            setPreferredSize(TILE_PANEL_SIZE);
            assignTileColor();
            assignPieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isRightMouseButton(e)){
                        resetSelectedTiles();
                    } else if (isLeftMouseButton(e)){
                        if (sourceTile == Square.NONE){
                            selectSourceTile();
                        } else {
                            if (chessBoard.getPiece(square).getPieceSide() == humanMovedPiece.getPieceSide()) {
                                selectSourceTile();
                            } else {
                                destinationTile = square;

                                // TODO add all types of piece promotion
                                final Move move;
                                if (humanMovedPiece.getPieceType() == PieceType.PAWN && (destinationTile.getRank() == Rank.RANK_8 || destinationTile.getRank() == Rank.RANK_1))
                                    move = new Move(sourceTile, destinationTile, getPromotionPiece("Q", humanMovedPiece.getPieceSide()));
                                else
                                    move = new Move(sourceTile, destinationTile);

                                try {
                                    if (MoveGenerator.generateLegalMoves(chessBoard).contains(move)) {
                                        chessBoard.doMove(move, true);
                                        moveLog.addMove(chessBoard.getBackup().getLast());
                                    }
                                } catch (MoveGeneratorException ex) {
                                    ex.printStackTrace();
                                }

                                resetSelectedTiles();
                            }
                        }
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            Table.get().moveMadeUpdate(PlayerType.HUMAN);

                            boardPanel.drawBoard(chessBoard);
                        }
                    });
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            validate();
        }

        // --- Mouse Click Functions --- //

        private void selectSourceTile(){
            sourceTile = square;
            humanMovedPiece = chessBoard.getPiece(sourceTile);

            if (humanMovedPiece == Piece.NONE)
                sourceTile = Square.NONE;
            else if (humanMovedPiece.getPieceSide() != chessBoard.getSideToMove()) {
                sourceTile = Square.NONE;
                humanMovedPiece = Piece.NONE;
            }
        }

        private void resetSelectedTiles(){
            sourceTile = Square.NONE;
            destinationTile = Square.NONE;
            humanMovedPiece = Piece.NONE;
        }


        // --- Drawing & Tile Visuals --- //

        void setLightTileColor(final Color color) {
            lightTileColor = color;
        }

        void setDarkTileColor(final Color color) {
            darkTileColor = color;
        }

        private void assignTileColor() {
            if (this.square.isLightSquare())
                setBackground(lightTileColor);
            else
                setBackground(darkTileColor);
        }

        private void assignPieceIcon(final Board board){
            this.removeAll();

            if (board.isSquareOccupied(this.square)){
                try {
                    final BufferedImage image =
                            ImageIO.read(new File(pieceIconPath + board.getPiece(this.square).value() + ".png" ));
                    Image scaledImage = image.getScaledInstance(TILE_PANEL_SIZE.width, TILE_PANEL_SIZE.height, Image.SCALE_SMOOTH);
                    add(new JLabel(new ImageIcon(scaledImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            highlightLegalMoves(Table.get().getGameBoard(), g);
        }

        private void highlightLegalMoves(final Board board, Graphics g){
            if (highlightLegalMoves){
                for (Move move : MoveGenerator.generateLegalMovesForPieceOnSquare(sourceTile, board)){
                    if (move.getTo() == square){
                        if (!board.isSquareOccupied(square)) {
                            Graphics2D g2 = (Graphics2D) g;
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                            if (square.isLightSquare())
                                g2.setColor(Color.decode("#89956E"));
                            else
                                g2.setColor(Color.decode("#6B6D46"));

                            g2.fillOval(this.getWidth() / 2 - 10, this.getHeight() / 2 - 10, 20, 20);
                        } else {
                            assignTileColor();

                            Graphics2D g2 = (Graphics2D) g;
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            if (square.isLightSquare())
                                g2.setColor(Color.decode("#B1B086"));
                            else
                                g2.setColor(Color.decode("#85794E"));

                            final int[][] xPoints = {{0, 0, 20}, {getWidth(), getWidth() - 20, getWidth()}, {getWidth(), getWidth() - 20, getWidth()}, {0, 0, 20}};
                            final int[][] yPoints = {{0, 20, 0}, {0, 0, 20}, {getHeight(), getHeight(), getHeight() - 20}, {getHeight(), getHeight() - 20, getHeight()}};

                            g2.fillPolygon(xPoints[0], yPoints[0], 3);
                            g2.fillPolygon(xPoints[1], yPoints[1], 3);
                            g2.fillPolygon(xPoints[2], yPoints[2], 3);
                            g2.fillPolygon(xPoints[3], yPoints[3], 3);
                        }
                    }
                }
            }
        }

        private void highlightLegalMoves(final Board board){
            if (highlightLegalMoves){
                for (Move move : MoveGenerator.generateLegalMovesForPieceOnSquare(sourceTile, board)){
                    if (move.getTo() == square){
//                        try {
//                            add(new JLabel(new ImageIcon(ImageIO.read(new File("resources/art/misc/green_dot.png")))));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        }

        private void highlightTileBorder() {
            if(humanMovedPiece != Piece.NONE && sourceTile == square) {
                setBorder(BorderFactory.createLineBorder(Color.cyan));
            }
        }

        private void highlightSelectedPiece() {
            if(humanMovedPiece != Piece.NONE && sourceTile == square) {
                if (this.square.isLightSquare())
                    setBackground(lightTileSelectedColor);
                else
                    setBackground(darkTileSelectedColor);
            }
        }

        private void highlightLastMove() {
            if(moveLog.size() > 0) {
                final Move prevMove = moveLog.getMoves().getLast().getMove();
                if (prevMove.getTo() == square || prevMove.getFrom() == square)
                    if (this.square.isLightSquare())
                        setBackground(lightTilePreviousMoveColor);
                    else
                        setBackground(darkTilePreviousMoveColor);
            }
        }

        public void drawTile(final Board board){
            assignTileColor();
            assignPieceIcon(board);
            highlightLegalMoves(board);
            highlightLastMove();
            highlightSelectedPiece();
            validate();
            repaint();
        }
    }


    // --- Helper Functions and Enums --- //

    public enum BoardOrientation {
        WHITE_DOWN {
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardOrientation flip() {
                return BLACK_DOWN;
            }
        },

        BLACK_DOWN {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                List<TilePanel> newBoardTiles = new ArrayList<>();
                for (int i = boardTiles.size() - 1; i >= 0; i--)
                    newBoardTiles.add(boardTiles.get(i));

                return newBoardTiles;
            }

            @Override
            BoardOrientation flip() {
                return WHITE_DOWN;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardOrientation flip();

    }

}
