package aomidi.chess.gui;

import aomidi.chess.ai.model.MoveLog;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private final Board chessBoard;
    private final MoveLog moveLog;

    private final JFrame gameFrame;
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

    public Table(){
        this.chessBoard = new Board();
        this.moveLog = new MoveLog();

        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());

//        todo delete next line if unused
        final JMenuBar menuBar = populateMenuBar();
        this.gameFrame.setJMenuBar(menuBar);
        this.gameFrame.setSize(FRAME_DIMENSION);

        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();

        this.boardPanel = new BoardPanel();
        this.boardOrientation = BoardOrientation.WHITE_DOWN;
        this.highlightLegalMoves = false;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    // --- Menu Bar Functions --- //

    private JMenuBar populateMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createPreferencesMenu());
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
                                System.out.println("Square: " + square + ", Piece: " + humanMovedPiece + ", Source: " + sourceTile.value() + ", Dest: " + destinationTile.value());
                                final Move move = new Move(sourceTile, destinationTile);
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

            System.out.println("Square: " + square + ", Piece: " + humanMovedPiece + ", Source: " + sourceTile.value() + ", Dest: " + destinationTile.value());
        }

        private void resetSelectedTiles(){
            sourceTile = Square.NONE;
            destinationTile = Square.NONE;
            humanMovedPiece = Piece.NONE;
            System.out.println("Square: " + square + ", Piece: " + humanMovedPiece + ", Source: " + sourceTile.value() + ", Dest: " + destinationTile.value());

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
