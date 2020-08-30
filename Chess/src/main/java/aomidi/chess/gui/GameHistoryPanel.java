package aomidi.chess.gui;

import aomidi.chess.ai.model.MoveLog;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.MoveBackup;
import com.github.bhlangonijr.chesslib.Side;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,40);
    private static final Color PANEL_COLOR = Color.decode("0xEBEDE9");

    public GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(20);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());


        setBackground(PANEL_COLOR);

        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);

    }

    public void redo(final Board board, final MoveLog moveLog){
        int currentRow = 0;
        this.model.clear();

        for (final MoveBackup move : moveLog.getMoves()){
            final String moveText = move.getMoveNotation();
            if (move.getMovingPiece().getPieceSide() == Side.WHITE){
                this.model.setValueAt(moveText, currentRow, 0);
            } else if (move.getMovingPiece().getPieceSide() == Side.BLACK){
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveLog.size() > 0){
            final MoveBackup lastMove = moveLog.getMoves().getLast();
            final String moveText = lastMove.getMoveNotation();

            if (lastMove.getMovingPiece().getPieceSide() == Side.WHITE){
                this.model.setValueAt(moveText + calculateCheckAndMateHash(board), currentRow, 0);
            } else if (lastMove.getMovingPiece().getPieceSide() == Side.BLACK){
                this.model.setValueAt(moveText + calculateCheckAndMateHash(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    private String calculateCheckAndMateHash(Board board) {
        if (board.isKingAttacked()){
            if (board.isMated())
                return "#";
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel{
        private final List<Row> values;
        private static final String[] NAMES = {"WHITE", "BLACK"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount(){
            if (this.values == null)
                return 0;
            return this.values.size();
        }

        @Override
        public int getColumnCount(){
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            final Row currentRow = this.values.get(row);

            if (column == 0)
                return currentRow.getWhiteMove();
            else if (column == 1)
                return currentRow.getBlackMove();
            return null;
        }

        @Override
        public void setValueAt(final Object value, final int row, final int column){
            final Row currentRow;

            if (this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }

            if (column == 0){
                currentRow.setWhiteMove((String) value);
                fireTableRowsInserted(row, row);
            } else if (column == 1){
                currentRow.setBlackMove((String) value);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column){
            return MoveBackup.class;
        }

        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }

        private static class Row {
            private String whiteMove;
            private String blackMove;

            Row(){
            }

            public String getWhiteMove() {
                return whiteMove;
            }

            public String getBlackMove() {
                return blackMove;
            }

            public void setWhiteMove(String move) {
                this.whiteMove = move;
            }

            public void setBlackMove(String move) {
                this.blackMove = move;
            }
        }
    }
}
