package com.blooregard.game.entities;

import com.blooregard.game.InputHandler;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Font;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;
import com.blooregard.game.level.tile.Tile;

public class Player extends Mob {

	private InputHandler input;
	private int color = Colors.get(-1, 111, 145, 543);
	private int scale = 1;
	private int tickCount = 0;
	private String username;
	private int health = 100;

	protected boolean isSwimming = false;

	public Player(Level level, int x, int y, InputHandler input, String username) {
		super(level, "Player", x, y, 1);
		this.input = input;
		this.username = username;
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

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

		checkHealth();
		tickCount++;
	}

	private void checkHealth() {
		if (tickCount % 60 == 0) {
			if (isSwimming) {
				health -= 5;
			} else {
				if (health < 100)
					health++;
			}
		}

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

		if (username != null) {
			int nameOffset = ((username.length() - 1) / 2) * 8
					- ((username.length() % 2) * 4);
			Font.render(username, screen, xOffset - nameOffset, yOffset - 10,
					Colors.get(-1, -1, -1, 555), 1);
		}

		Font.render("Health: " + health + "%", screen, screen.xOffset,
				screen.yOffset, Colors.get(-1, -1, -1, 550), 1);

	}

	public boolean hasCollided(int xa, int ya) {
		int xMin = 0;
		int xMax = 7;
		int yMin = 3;
		int yMax = 7;

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

}
