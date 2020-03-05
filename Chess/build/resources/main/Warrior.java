import aomidi.chess.model.Tile;

public class Warrior extends MilitaryUnit {
	
	public Warrior(Tile tile, double hp, String faction) {
		super(tile, hp, 1, faction, 20.0, 1, 25);
	}
	
	@Override public boolean equals(Object obj) {
		if (obj instanceof Warrior) {
			return super.equals(obj);
		}else {
			return false;
		}
    }
	
}
