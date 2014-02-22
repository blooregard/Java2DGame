package com.blooregard.game.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.blooregard.game.Game;
import com.blooregard.game.entities.projectiles.Spell;
import com.blooregard.game.entities.projectiles.SpellTypes;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Font;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;
import com.blooregard.game.level.tile.Tile;
import com.blooregard.game.net.packets.Packet02Movement;

public abstract class Mob extends Entity {

	public static enum MobTypes {
		INVALID(-1), PLAYER(00), TONBERRY(01);

		private int mobId;

		private MobTypes(int mobId) {
			this.mobId = mobId;
		}

		public int getId() {
			return mobId;
		}
	}
	
	public class Coordinate {
		private int x, y;
		
		public Coordinate (int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coordinate other = (Coordinate) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private Mob getOuterType() {
			return Mob.this;
		}	
	}

	protected MobTypes type;
	protected String name;
	protected int speed;
	public int numSteps;
	protected boolean isMoving;
	protected int movingDir = 1; // 0=up, 1=down, 2=left, 3=right
	protected int scale = 1;
	protected int health, maxHealth;
	protected int mana, maxMana;
	protected long lastFired;
	protected Map<Long, Integer> damageToShow = new ConcurrentHashMap<Long, Integer>();

	public Mob(Game game, Level level, UUID uuid, MobTypes type, String name,
			int x, int y, int speed, int health, int mana) {
		super(game, level, uuid);
		this.type = type;
		this.name = name;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.health = this.maxHealth = health;
		this.mana = this.maxMana = mana;
	}

	public void move(int xa, int ya) {

		if (xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			numSteps--;
			return;
		}
		numSteps++;
		if (!hasCollided(xa, ya)) {
			if (ya < 0)
				movingDir = 0;
			if (ya > 0)
				movingDir = 1;
			if (xa < 0)
				movingDir = 2;
			if (xa > 0)
				movingDir = 3;
			x += xa * speed;
			y += ya * speed;
		}

		Packet02Movement packet = new Packet02Movement(this);
		packet.writeData(this.game.socketClient);
	}

	public MobTypes getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public int getMovingDir() {
		return this.movingDir;
	}

	public void modifyHealth(int delta) {
		this.health += delta;
		if (delta < 0) {
			damageToShow.put(System.currentTimeMillis(), delta);
		}
		if (this.health > this.maxHealth)
			this.health = this.maxHealth;
		checkHealth();
	}

	public void modifyMana(int delta) {
		this.mana += delta;
		if (this.mana > this.maxMana)
			mana = this.maxMana;
	}

	public void setMovingDir(int movingDir) {
		this.movingDir = movingDir;
	}

	protected void checkHealth() {
		if (this.health <= 0) {
			// TODO create loot

			this.cleanUp = true;
		}
	}

	public abstract boolean hasCollided(int xa, int ya);

	protected void renderStatus(Screen screen, int xOffset, int yOffset) {
		int green = Colors.get(50);
		int blue = Colors.get(5);
		int empty = Colors.get(555);
		int healthPercent = (int) (((float) this.health / (float) this.maxHealth) * 10.0);
		int manaPercent = (int) (((float) this.mana / (float) this.maxMana) * 10.0);
		int[] status = new int[20];
		for (int x = 0; x < 10; x++) {
			if (x <= healthPercent) {
				status[x] = green;
			} else {
				status[x] = empty;
			}
		}
		for (int x = 0; x < 10; x++) {
			if (x <= manaPercent) {
				status[x + 10] = blue;
			} else {
				status[x + 10] = empty;
			}
		}
		screen.render(xOffset + 3, yOffset - 4, 10, 2, status, 1);
	}

	protected void renderDamage(Screen screen, int xOffset, int yOffset) {
		int idx = 0;
		Iterator<Long> iter = this.damageToShow.keySet().iterator();
		while (iter.hasNext()) {
			Long id = iter.next();
			Integer damage = this.damageToShow.get(id);
			long diff = System.currentTimeMillis() - id.longValue();
			if (diff >= 2000) {
				iter.remove();
			} else {
				Font.render(damage.toString(), screen, xOffset,
						(int) (yOffset - diff / 20),
						Colors.get(-1, -1, -1, 500), 1);
			}
			idx++;
		}

	}

	protected boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null) {
			return false;
		}
		Tile lastTile = level.getTile((this.x + x) >> 3, (this.y + y) >> 3);
		Tile newTile = level.getTile((this.x + x + xa) >> 3,
				(this.y + y + ya) >> 3);
		if (!lastTile.equals(newTile) && newTile.isSolid()) {
			return true;
		}
		return false;
	}

	// Tiles apart
	protected double distanceApart(Mob other) {
		int x = Math.abs((this.x >> 3) - (other.x >> 3));
		int y = Math.abs((this.y >> 3) - (other.y >> 3));
		return Math.sqrt(x * x + y * y);
	}
	
	protected void pathfinding(Mob other) {
		boolean found = false;
		int x, y, xa, ya, step =0 ;
		Coordinate location = new Coordinate(this.x >> 3, this.y >> 3);
		Map<Coordinate, Integer> path = new HashMap<Coordinate, Integer>();
		
		// Add first coordinate
		x = other.x >> 3;
		y = other.y >> 3;
		path.put(new Coordinate(x,y), step);
		
		while(!found) {
			step++;
			Map<Coordinate, Integer> path1 = new HashMap<Coordinate, Integer>(path);
			Iterator<Coordinate> iter = path1.keySet().iterator();
			
			while( iter.hasNext()) {
				Coordinate c = iter.next();
				if (!level.getTile(c.x-1, c.y).isSolid()) {
					Coordinate c1 = new Coordinate(c.x-1, c.y);
					if (!path.containsKey(c1))
						path.put(c1, step);
				}
				if (!level.getTile(c.x-1, c.y-1).isSolid()) {
					Coordinate c1 = new Coordinate(c.x-1, c.y-1);
					if (!path.containsKey(c1))
						path.put(c1, step);
				}
				if (!level.getTile(c.x+1, c.y).isSolid()) {
					Coordinate c1 = new Coordinate(c.x+1, c.y);
					if (!path.containsKey(c1))
						path.put(c1, step);
				}
				if (!level.getTile(c.x+1, c.y+1).isSolid()) {
					Coordinate c1 = new Coordinate(c.x+1, c.y+1);
					if (!path.containsKey(c1))
						path.put(c1, step);
				}
			}
			found = path.containsKey(location);
		}
		
		Coordinate m = location;
		while ( step > 0) {
		
			Coordinate c1 = new Coordinate(m.x-1, m.y);
			if (path.containsKey(c1)) {
				if (path.get(c1) < step) {
					move(-1, 0);
					m = c1;
				}
			}
			
			Coordinate c2 = new Coordinate(m.x-1, m.y-1);
			if (path.containsKey(c2)) {
				if (path.get(c2) < step) {
					move(-1, -1);
					m = c2;
				}
			}
			
			Coordinate c3 = new Coordinate(m.x+1, m.y);
			if (path.containsKey(c3)) {
				if (path.get(c3) < step) {
					move(1, 0);
					m = c3;
				}
			}
			
			Coordinate c4 = new Coordinate(m.x+1, m.y+1);
			if (path.containsKey(c4)) {
				if (path.get(c4) < step) {
					move(1, 1);
					m = c4;
				}
			}

			break;
		}
		
	}

	public static MobTypes lookupMob(String id) {
		try {
			return lookupMob(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			return MobTypes.INVALID;
		}
	}

	public static MobTypes lookupMob(int id) {
		for (MobTypes m : MobTypes.values()) {
			if (m.getId() == id) {
				return m;
			}
		}
		return MobTypes.INVALID;
	}

	@Override
	public String getData() {
		return (this.type.mobId + "|" + this.uuid + "|" + this.name + "|"
				+ this.x + "|" + this.y + "|" + this.getMovingDir() + "|"
				+ this.health + "|" + this.mana);
	}

	public void cast(String spellType) {
		SpellTypes type = SpellTypes.get(spellType);
		Spell spell = type.get(game, level, this);
		if (System.currentTimeMillis() - this.lastFired >= spell.getCoolDown()) {
			this.lastFired = System.currentTimeMillis();
			if (spell.getManaCost() <= this.mana) {
				this.mana -= spell.getManaCost();
				level.addEntity(spell);
			}
		}
	}

}
