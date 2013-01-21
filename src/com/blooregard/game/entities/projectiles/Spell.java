package com.blooregard.game.entities.projectiles;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Entity;
import com.blooregard.game.entities.Mob;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.gfx.Sprite;
import com.blooregard.game.level.Level;
import com.blooregard.game.level.tile.Tile;

public class Spell extends Entity {

	protected Mob caster;
	protected Sprite[] sprites;
	protected String name;
	protected int rolls;
	protected int sides;
	protected int manaCost;
	protected int speed;
	protected int coolDown;
	protected int color;
	protected int movingDir = 1; // 0=up, 1=down, 2=left, 3=right
	protected boolean impact = false;

	private int xMin = 0, xMax = 7, yMin = 0, yMax = 7;

	private static final Random random = new Random();

	public Spell(Game game, Level level, Mob mob, String name,
			Sprite[] sprites, int rolls, int sides, int manaCost, int speed,
			int coolDown, int color) {
		super(game, level, UUID.randomUUID());

		if (mob != null) {
			this.caster = mob;
			this.x = mob.x;
			this.y = mob.y;
			this.movingDir = mob.getMovingDir();
		}

		this.name = name;
		this.sprites = sprites;
		this.rolls = rolls;
		this.sides = sides;
		this.manaCost = manaCost;
		this.speed = speed;
		this.coolDown = coolDown;
		this.color = color;
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

	public void setCaster(Mob caster) {
		this.caster = caster;
		this.x = caster.x;
		this.y = caster.y;
		this.movingDir = this.caster.getMovingDir();
	}

	public int calcDamage() {
		int damage = 0;
		for (int i = 0; i < rolls; i++) {
			damage += random.nextInt(this.sides) + 1;
		}

		return damage;
	}

	public String getName() {
		return name;
	}

	public int getManaCost() {
		return manaCost;
	}

	public Mob getCaster() {
		return caster;
	}

	public int getRolls() {
		return rolls;
	}

	public int getSides() {
		return sides;
	}

	public int getSpeed() {
		return speed;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public int getColor() {
		return color;
	}

	public boolean hasCollided(int xa, int ya) {

		Rectangle ownHitBox = this.getHitBox();

		// Environment Check
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

		// Entity Check
		Iterator<Entity> iter = level.getEntities().values().iterator();
		while (iter.hasNext()) {
			Entity e = iter.next();
			// Stop hitting yourself & caster
			if (!e.equals(this) && !e.equals(caster)) {
				impact = ownHitBox.intersects(e.getHitBox());
				if (impact) {
					if (e instanceof Mob) {
						((Mob) e).modifyHealth(-1 * this.calcDamage());
					}
					return true;
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
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 24;
		int flip = 0x00;

		if (this.movingDir > 1) {
			xTile += 1;
		}
		if (this.movingDir % 2 == 0) {
			flip = 0x01;
		} else {
			flip = 0x02;
		}

		screen.render(x, y, xTile + yTile * 32, color, flip, 1);
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x + xMin, y + yMin, xMax - xMin + 1, yMax - yMin
				+ 1);
	}

	@Override
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

	public Sprite getCurrentSprite() {
		return this.sprites[this.movingDir];
	}
}
