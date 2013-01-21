package com.blooregard.game.entities;

import java.awt.Rectangle;
import java.util.UUID;

import com.blooregard.game.Game;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Font;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;
import com.blooregard.game.level.tile.Tile;
import com.blooregard.game.listener.InputHandler;

public class Player extends Mob {

	private InputHandler input;
	private int color = Colors.get(-1, 111, 145, 543);
	// private int color = Colors.get(-1, 111, 550, 543);
	private int scale = 1;
	private int tickCount = 0;
	private int xMin = 0, xMax = 7, yMin = 3, yMax = 7;

	protected boolean isSwimming = false;

	public Player(Game game, Level level, UUID uuid, String name, int x, int y,
			InputHandler input) {
		super(game, level, uuid, MobTypes.PLAYER, name, x, y, 1, 100, 100);
		this.input = input;
	}
	
	// Constructor used by the client
	public Player(Game game, Level level, UUID uuid, String name, int x, int y, int movingDir, int health, int mana){
		super(game, level, uuid, MobTypes.PLAYER, name, x, y, 1, health, mana);
		this.movingDir = movingDir;
		this.input = null;
	}

	public void tick() {
		int xa = 0;
		int ya = 0;
		if (input != null) {
			if (input.up.isPressed()) {
				ya--;
			}
			if (input.down.isPressed()) {
				ya++;
			}
			if (input.left.isPressed()) {
				xa--;
			}
			if (input.right.isPressed()) {
				xa++;
			}
//			if (input.fire.isPressed()) {
//				// TODO fix this
//				Spell spell = new Icelance(this);
//				if (System.currentTimeMillis() - this.lastFired >= spell.getCoolDown()) {
//					this.lastFired = System.currentTimeMillis();
//					if (spell.getManaCost() <= this.mana) {
//						this.mana -= spell.getManaCost();
//						Ball ball = new Ball(game, level, UUID.randomUUID(), spell, this.movingDir);
//						level.addEntity(ball);
//					}
//				}
//			}
		}

		if (xa != 0 || ya != 0) {
			move(xa, ya);
			isMoving = true;
		} else {
			isMoving = false;
		}

		if (level.getTile(this.x >> 3, this.y >> 3).getId() == Tile.WATER
				.getId()) {
			isSwimming = true;
		} else {
			isSwimming = false;
		}
		
		if (tickCount % 30 == 0) {
			this.modifyHealth(1);
			this.modifyMana(1);
		}

		tickCount++;
	}

	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 28;
		int walkingSpeed = 4;
		int flipTop = (numSteps >> walkingSpeed) & 1;
		int flipBottom = (numSteps >> walkingSpeed) & 1;

		if (movingDir == 1) {
			xTile += 2;
		} else if (movingDir > 1) {
			xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
			flipTop = (movingDir - 1) % 2;
		}

		int modifier = 8 * scale;
		int xOffset = x - modifier / 2;
		int yOffset = y - modifier / 2 - 4;

		if (isSwimming) {
			int waterColor = 0;
			yOffset += 4;
			if (tickCount % 60 < 15) {
				waterColor = Colors.get(-1, -1, 225, -1);
			} else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
				yOffset -= 1;
				waterColor = Colors.get(-1, 225, 115, -1);
			} else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
				waterColor = Colors.get(-1, 115, -1, 225);
			} else {
				yOffset -= 1;
				waterColor = Colors.get(-1, 225, 115, -1);
			}
			screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColor, 0x00,
					1);
			screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColor,
					0x01, 1);
		}

		screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile
				* 32, color, flipTop, scale);
		screen.render(xOffset + modifier - (modifier * flipTop), yOffset,
				(xTile + 1) + yTile * 32, color, flipTop, scale);

		if (!isSwimming) {
			screen.render(xOffset + (modifier * flipBottom),
					yOffset + modifier, xTile + (yTile + 1) * 32, color,
					flipBottom, scale);
			screen.render(xOffset + modifier - (modifier * flipBottom), yOffset
					+ modifier, (xTile + 1) + (yTile + 1) * 32, color,
					flipBottom, scale);
		}

		if (name != null) {
			int nameOffset = ((name.length() - 1) / 2) * 8
					- ((name.length() % 2) * 4);
			Font.render(name, screen, xOffset - nameOffset, yOffset - 12,
					Colors.get(-1, -1, -1, 555), 1);
		}

		this.renderStatus(screen, xOffset, yOffset);

	}

	public boolean hasCollided(int xa, int ya) {

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
		return false;
	}

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x + xMin, y + yMin, xMax - xMin + 1, yMax - yMin
				+ 1);
	}

}
