public class Tile {
	private int x_coordinate, y_coordinate;
	private boolean city_built, improvements;
	private ListOfUnits units;
	
	public Tile(int x_coordinate, int y_coordinate){
		this.x_coordinate = x_coordinate;
		this.y_coordinate = y_coordinate;
		this.city_built = false;
		this.improvements = false;
		this.units = new ListOfUnits();
	}
	
	public int getX() {
		return this.x_coordinate;
	}
	
	public int getY() {
		return this.y_coordinate;
	}
	
	public boolean isCity() {
		return this.city_built;
	}
	
	public boolean isImproved() {
		return this.improvements;
	}
	
	public void foundCity() {
		this.city_built = true;
	}
	
	public void buildImprovement() {
		this.improvements = true;
	}
	
	public boolean addUnit(Unit unit) {
		MilitaryUnit[] army_list = this.units.getArmy();
		// check if army list has any units and if not add the unit
		if (army_list.length == 0){
			this.units.add(unit);
			return true;
		}
		// checks if factions are the same and adds unit if true or if its a non-military unit
		if (army_list[0].getFaction().equals(unit.getFaction()) || !(unit instanceof MilitaryUnit) ) {
			this.units.add(unit);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeUnit(Unit remove_unit) {
		return this.units.remove(remove_unit);
	}
	
	public Unit selectWeakEnemy(String faction) {
		int weakest_unit_index = -1;
		// run through all the units on the tile
		for (int i=0; i < this.units.size(); i++) {
			// if the factions are different, look for the smallest value of hit
			if (!faction.equals(this.units.get(i).getFaction())) {
				if (i == 0 || this.units.get(weakest_unit_index).getHP() > this.units.get(i).getHP()) {
					weakest_unit_index = i;
				}
			}
		}
		
		// check to see if index value is valid (null means no enemy units)
		if (weakest_unit_index >= 0) {
			return this.units.get(weakest_unit_index);
		} else {
			return null;
		}
	}
	
	public static double getDistance(aomidi.chess.model.Tile tile1, aomidi.chess.model.Tile tile2) {
		double distance = Math.sqrt(Math.pow(tile1.x_coordinate - tile2.x_coordinate, 2) + Math.pow(tile1.y_coordinate - tile2.y_coordinate, 2));
		return distance;
	}
	
	
	
}

