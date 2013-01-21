package com.blooregard.game.entities.projectiles;

import java.util.UUID;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Entity;
import com.blooregard.game.level.Level;

public abstract class Projectile extends Entity {

	protected int movingDir = 1; // 0=up, 1=down, 2=left, 3=right
	protected boolean impact = false;

	public Projectile(Game game, Level level, UUID uuid) {
		super(game, level, uuid);
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

	public abstract void move(int xa, int ya);

	public abstract boolean hasCollided(int xa, int ya);

}
