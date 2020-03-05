import aomidi.chess.model.Tile;

public class Worker extends Unit {
	
	private int num_of_jobs;
	
	public Worker(Tile tile, double hp, String faction) {
		super(tile, hp, 2, faction);
		this.num_of_jobs = 0;
	}
	
	@Override public boolean equals(Object obj) {
		// if Object is a Worker and has the same number of jobs and Unit.equals is true they are equal
		if (obj instanceof Worker && ((Worker) obj).num_of_jobs == this.num_of_jobs) {        
	        return super.equals(obj);
		}else {
        	return false;
        }
    }
	
	public void takeAction(Tile tile) {
		if (this.getPosition().equals(tile) && !this.getPosition().isImproved()) {
			this.getPosition().buildImprovement();
			this.num_of_jobs += 1;
			if (this.num_of_jobs == 10) {
				this.getPosition().removeUnit(this);
			}
		}
	}
}
