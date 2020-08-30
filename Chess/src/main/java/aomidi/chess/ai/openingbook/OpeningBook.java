package aomidi.chess.ai.openingbook;

public class OpeningBook {

    private MoveNode root;
    private MoveNode cur_move;

    public OpeningBook(){
        this.root = new MoveNode("root", null);
        this.cur_move = this.root;
    }

    public void addNode(MoveNode node){
        this.cur_move.addToMoveList(node);
    }

    public void addNode(String move){
        this.cur_move = this.cur_move.addToMoveList(move);
    }

    public boolean doMove(String move){
        MoveNode new_move = cur_move.getMoveNode(move);

        if (new_move != null) {
            cur_move = new_move;
            return true;
        }

        return false;
    }

    public boolean doMove(MoveNode moveNode){
        MoveNode new_move = cur_move.getMoveNode(moveNode.getMove());

        if (new_move != null) {
            cur_move = new_move;
            return true;
        }

        return false;
    }

    public boolean undoMove(){
        if (cur_move.getParent() != null) {
            cur_move = cur_move.getParent();
            return true;
        }
        return false;
    }

    public void resetCurMove(){
        cur_move = root;
    }

    public MoveNode getCur_move() {
        return cur_move;
    }

}

