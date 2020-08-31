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
import static aomidi.chess.ai.model.Util.input;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final Board chessBoard;
    private final MoveLog moveLog;
    private static Player whitePlayer;
    private static Player blackPlayer;

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
    private static Dimension BOARD_PANEL_SIZE = new Dimension(400, 400);
    private static Dimension TILE_PANEL_SIZE = new Dimension(10, 10);

    private final Color lightTileColor = Color.decode("#ECD9B9");
    private final Color darkTileColor = Color.decode("#AE8968");
    private final Color lightTileSelectedColor = Color.decode("#8A966F");
    private final Color darkTileSelectedColor = Color.decode("#6B6E47");
    private final Color darkTilePreviousMoveColor = Color.decode("#AAA256");
    private final Color lightTilePreviousMoveColor = Color.decode("#CFD186");
    private static String defaultImagesPath = "resources/art/pieces/plain/";
    private Move computerMove;

    private static final Table INSTANCE = new Table();

    private Table(){
        this.chessBoard = new Board();
//        this.chessBoard.loadFromFen("3qkbnr/P2ppppp/8/8/8/8/3PPPPP/R2QKBNR w KQk - 0 1");
        this.moveLog = new MoveLog();

        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());

//        todo delete next line if unused
        final JMenuBar menuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(menuBar);
        this.gameFrame.setSize(FRAME_DIMENSION);

        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();

        this.boardPanel = new BoardPanel();
        this.boardOrientation = BoardOrientation.WHITE_DOWN;
        this.highlightLegalMoves = false;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);

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

    private JFrame getGameFrame() {
        return this.gameFrame;
    }

    private Board getGameBoard() {
        return this.chessBoard;
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

//    private boolean getUseBook() {
//        return this.useBook;
//    }
    private static Player getPlayer(Side side){
        if (side == Side.WHITE)
            return whitePlayer;
        else
            return blackPlayer;
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

        final JCheckBoxMenuItem highlightMovesItem = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        highlightMovesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = highlightMovesItem.isSelected();
            }
        });
        preferencesMenu.add(highlightMovesItem);

        return preferencesMenu;
    }

    private JMenu createOptionsMenu() {

        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);

//        final JMenuItem resetMenuItem = new JMenuItem("New Game", KeyEvent.VK_P);
//        resetMenuItem.addActionListener(e -> undoAllMoves());
//        optionsMenu.add(resetMenuItem);

//        final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board", KeyEvent.VK_E);
//        evaluateBoardMenuItem.addActionListener(e -> System.out.println(StandardBoardEvaluator.get().evaluationDetails(chessBoard, gameSetup.getSearchDepth())));
//        optionsMenu.add(evaluateBoardMenuItem);

//        final JMenuItem escapeAnalysis = new JMenuItem("Escape Analysis Score", KeyEvent.VK_S);
//        escapeAnalysis.addActionListener(e -> {
//            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
//            if(lastMove != null) {
//                System.out.println(MoveUtils.exchangeScore(lastMove));
//            }
//
//        });
//        optionsMenu.add(escapeAnalysis);

//        final JMenuItem legalMovesMenuItem = new JMenuItem("Current State", KeyEvent.VK_L);
//        legalMovesMenuItem.addActionListener(e -> {
////            System.out.println(chessBoard.getWhitePieces());
////            System.out.println(chessBoard.getBlackPieces());
////            System.out.println(playerInfo(chessBoard.currentPlayer()));
////            System.out.println(playerInfo(chessBoard.currentPlayer().getOpponent()));
//        });
//        optionsMenu.add(legalMovesMenuItem);
//
//        final JMenuItem undoMoveMenuItem = new JMenuItem("Undo last move", KeyEvent.VK_M);
//        undoMoveMenuItem.addActionListener(e -> {
//            if(Table.get().getMoveLog().size() > 0) {
//                undoLastMove();
//            }
//        });
//        optionsMenu.add(undoMoveMenuItem);

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game", KeyEvent.VK_S);
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);

        return optionsMenu;
    }

    private static class TableGameAIObserver implements Observer {
        @Override
        public void update(final Observable o,
                           final Object arg) {

            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getSideToMove()) &&
                    !Table.get().getGameBoard().isMated() &&
                    !Table.get().getGameBoard().isStaleMate()) {
                System.out.println(Table.get().getGameBoard().getSideToMove() + " is set to AI, thinking....");
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().isMated()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().getSideToMove() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().isStaleMate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().getSideToMove() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void updateComputerMove(Move move){
        this.computerMove = move;
    }

    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }
    
    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {
        }

        @Override
        protected Move doInBackground() {
            final Move bestMove;
//            final Move bookMove = Table.get().getUseBook()
//                    ? MySqlGamePersistence.get().getNextBestMove(Table.get().getGameBoard(),
//                    Table.get().getGameBoard().currentPlayer(),
//                    Table.get().getMoveLog().getMoves().toString().replaceAll("\\[", "").replaceAll("]", ""))
//                    : MoveFactory.getNullMove();
//            if (Table.get().getUseBook() && bookMove != MoveFactory.getNullMove()) {
//                bestMove = bookMove;
//            }
//            else {

            final Engine engine = new Engine(Table.get().getGameSetup().getSearchDepth());
//            engine.addObserver(Table.get().getDebugPanel());
            bestMove = engine.getBestMove(Table.get().getGameBoard());

            return bestMove;
        }


        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().getGameBoard().doMove(bestMove);
                Table.get().getMoveLog().addMove(Table.get().getGameBoard().getBackup().getLast());
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
//                Table.get().getDebugPanel().redo();
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
                            ImageIO.read(new File(
                            defaultImagesPath + board.getPiece(this.square).value() + ".png" ));
//                    final BufferedImage image =
//                            ImageIO.read(new File(
//                                    defaultImagesPath + "ChessPiecesSprite_resized.png" ));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        private void highlightLegalMoves(final Board board){
            if (highlightLegalMoves){
                for (Move move : MoveGenerator.generateLegalMovesForPieceOnSquare(sourceTile, board)){
                    if (move.getTo() == square){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("resources/art/misc/green_dot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
