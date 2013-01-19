package com.blooregard.game.entities;

import com.blooregard.game.Game;
import com.blooregard.game.gfx.Colors;
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

	public Mob(Game game, Level level, MobTypes type, String name, int x,
			int y, int speed, int health, int mana) {
		super(game, level);
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
	}

	public void modifyMana(int delta) {
		this.mana += delta;
	}

	public void setMovingDir(int movingDir) {
		this.movingDir = movingDir;
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
				status[x +10] = blue;
			} else {
				status[x + 10] = empty;
			}
		}
		screen.render(xOffset + 3, yOffset - 2, 10, 2, status, 1);
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
}
