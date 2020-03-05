import aomidi.chess.model.Tile;

public abstract class Unit {
	private Tile tile;
	private double hp;
	private int moving_range;
	private String faction;
	
	public Unit() {
		
	}
	
	public Unit(Tile tile, double hp, int moving_range, String faction) {
		this.tile = tile;
		this.hp = hp;
		this.moving_range = moving_range;
		this.faction = faction; 
		
		if (!this.tile.addUnit(this)) {
			throw new IllegalArgumentException("IllegalArgumentException");
		}
	}
	
	public final Tile getPosition() {
		return this.tile;
	}
	
	public final double getHP() {
		return this.hp;
	}
	
	public final String getFaction() {
		return this.faction;
	}
	
	public boolean moveTo(Tile move_to_tile) {
		double distance = Tile.getDistance(this.tile, move_to_tile);
		// checks to see if unit is in range
		if (distance < this.moving_range +1) {
			// add to tile, if it doesn't work don't remove unit from this.tile and vice-versa
			if (move_to_tile.addUnit(this)) { 
				// remove unit from current tile
				this.tile.removeUnit(this);
				// change unit tile to new tile and add it to its list 
				this.tile = move_to_tile;
			
				return true;
			} 
		}
		return false;
	}
	
	public void receiveDamage(double damage_received) {
		if (this.tile.isCity()) {
			damage_received *= 0.9;
		}
		
		this.hp -= damage_received;
		
		if (this.hp <= 0) {
			this.tile.removeUnit(this);
		}
	}
	
	public boolean equals(Object obj) {
        if ( obj instanceof Unit && ((Unit) obj).getPosition().equals(this.tile) && ((Unit) obj).getHP() == this.hp && ((Unit) obj).getFaction().equals(this.faction)) {
        	return true;
        } else {
        	return false;
        }
    }
	
	public abstract void takeAction(Tile tile);
	
}

