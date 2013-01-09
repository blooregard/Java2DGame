package com.blooregard.game.entities;

import com.blooregard.game.Game;
import com.blooregard.game.level.Level;
import com.blooregard.game.level.tile.Tile;
import com.blooregard.game.net.packets.Packet02Movement;

public abstract class Mob extends Entity {

	protected String name;
	protected int speed;
	protected int numSteps;
	protected boolean isMoving;
	protected int movingDir = 1; // 0=up, 1=down, 2=left, 3=right
	protected int scale = 1;

	public Mob(Game game, Level level, String name, int x, int y, int speed) {
		super(game, level);
		this.name = name;
		this.x = x;
		this.y = y;
		this.speed = speed;
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

	public String getName() {
		return name;
	}
	
	public int getMovingDir() {
		return this.movingDir;
	}
	
	public void setMovingDir(int movingDir) {
		this.movingDir = movingDir;
	}

	public abstract boolean hasCollided(int xa, int ya);

	protected boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null) {
			return false;
		}
		Tile lastTile = level.getTile((this.x + x) >> 3, (this.y + y) >> 3);
		Tile newTile = level.getTile((this.x + x + xa) >> 3, (this.y + y + ya) >> 3);
		if (!lastTile.equals(newTile) && newTile.isSolid()) {
			return true;
		}
		return false;
	}

}
