import aomidi.chess.model.Tile;

public abstract class MilitaryUnit extends Unit {
	private double attack_damage;
	private int attack_range;
	private int armour;
	
	public MilitaryUnit(Tile tile, double hp, int moving_range, String faction, double attack_damage, int attack_range, int armour) {
		super(tile, hp, moving_range, faction);
		this.attack_damage = attack_damage;
		this.attack_range = attack_range;
		this.armour = armour;
	}
	
	public void takeAction(Tile tile) {
		if (Tile.getDistance(tile, this.getPosition()) < this.attack_range + 1) {
			// if unit on a improved tile increase damage by 5%
			double damage_multiplier = 1;
			if (this.getPosition().isImproved()) {
				damage_multiplier = 1.05;
			}
			// check for enemy and attack
			if (tile.selectWeakEnemy(this.getFaction()) != null) {
				tile.selectWeakEnemy(this.getFaction()).receiveDamage(this.attack_damage * damage_multiplier);
			}
		}
	}
	
	@Override public void receiveDamage(double damage_received) {
		double multiplier = 100 / (100 + this.armour);
		
		super.receiveDamage(damage_received * multiplier);
	}
}
