package com.blooregard.game.entities.projectiles;

import java.awt.Rectangle;
import java.util.Random;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Entity;
import com.blooregard.game.entities.Mob;
import com.blooregard.game.entities.Projectile;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Font;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;
import com.blooregard.game.level.tile.Tile;

public class FireBall extends Projectile {
	
	private static final Random random = new Random();
	private int color = Colors.get(-1, 500, 530, 555);
	private int damage;
	private int xMin = 0, xMax = 7, yMin = 0, yMax = 7;

	public FireBall(Game game, Level level, Mob mob, int movingDir, int speed, int coolDown) {
		super(game, level, mob, movingDir, speed, coolDown);
		this.damage = (random.nextInt(6) + 1) * 3;
		this.x = mob.x;
		this.y = mob.y;
	}

	@Override
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 24;
		int flip = 0x00;
		
		if (this.movingDir>1) {
			xTile += 1;
		}
		if (this.movingDir % 2 == 0) {
			flip = 0x01;
		} else {
			flip = 0x02;
		}
	
		screen.render(x, y, xTile + yTile * 32, color, flip, 1);
		
		if(impact) {
			Font.render("Boom", screen, x, y - 10,
					Colors.get(-1, -1, -1, 500), 1);
		}
	}

	@Override
	public boolean hasCollided(int xa, int ya) {

		Rectangle ownHitBox = this.getHitBox();

		//Environment Check
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMin)) {
				return true;
			}
		}
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMax)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMin, y)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMax, y)) {
				return true;
			}
		}
		
		//Entity Check
		synchronized (level.entities) {
			for(Entity e: level.entities) {
				//Stop hitting yourself
				if (!e.equals(mob)) {
					impact = ownHitBox.intersects(e.getHitBox());
					if (impact) {
						
						if (e instanceof Mob) {
							((Mob)e).modifyHealth(-1 * this.damage);
						}
						return true;
					}
				}
			}
		}
		
		return false;
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

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x + xMin, y + yMin, xMax - xMin + 1, yMax - yMin
				+ 1);
	}

}
