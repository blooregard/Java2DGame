package com.blooregard.game.entities;

import com.blooregard.game.Game;
import com.blooregard.game.level.Level;

public abstract class Projectile extends Entity {

	protected Mob mob;
	protected int movingDir = 1; // 0=up, 1=down, 2=left, 3=right
	protected int speed;
	protected long coolDown;
	protected boolean impact = false;
	public boolean cleanUp = false;
	
	public Projectile(Game game, Level level, Mob mob, int movingDir, int speed, long coolDown) {
		super(game, level);
		this.mob = mob;
		this.movingDir = movingDir;
		this.speed = speed;
		this.coolDown = coolDown;
	}
	
	public long getCoolDown() {
		return this.coolDown;
	}

	public void tick() {
		switch (movingDir) {
		case 0:
			move(0, -1);
			break;
		case 1:
			move(0, 1);
			break;
		case 2:
			move(-1, 0);
			break;
		case 3:
			move(1, 0);
			break;
		}
	}

	public void move(int xa, int ya) {
		if (xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		
		this.cleanUp = hasCollided(xa, ya);
		
		if (!cleanUp) {
			x += xa * speed;
			y += ya * speed;
		} 
	}
	
	public abstract boolean hasCollided(int xa, int ya);

}
