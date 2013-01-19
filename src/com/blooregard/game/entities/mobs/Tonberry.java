package com.blooregard.game.entities.mobs;

import java.awt.Rectangle;
import java.util.Random;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Mob;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Font;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;

public class Tonberry extends Mob {

	private static final Random random = new Random();
	private int color = Colors.get(-1, 111, 421, 040);
	private int tickCount = 0;
	private int xa = 0, ya = 0;
	private int xMin = 0, xMax = 7, yMin = 3, yMax = 10;

	public Tonberry(Game game, Level level, int x, int y) {
		super(game, level, MobTypes.TONBERRY, "Tonberry", x, y, 1, 50, 50);
	}

	@Override
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
	public void tick() {

		// Only update is this is the game server
		if (this.game != null) {
			if (tickCount % 180 == 0) {
				xa = random.nextInt(3) - 1;
				ya = random.nextInt(3) - 1;
			}

			if ((tickCount % 5 == 0) && (xa != 0 || ya != 0)) {
				move(xa, ya);
				isMoving = true;
			} else {
				isMoving = false;
			}

			tickCount++;
		}
	}

	@Override
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 25;
		int walkingSpeed = 4;
		int flipTop = 0;
		int flipBottom = 0;

		if (movingDir == 1) {
			xTile += 2;
		} else if (movingDir > 1) {
			xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
			flipTop = (movingDir - 1) % 2;
			flipBottom = (movingDir - 1) % 2;
		}

		int modifier = 8 * scale;
		int xOffset = x - modifier / 2;
		int yOffset = y - modifier / 2 - 4;

		if (false) {
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

		if (!false) {
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
			Font.render(name, screen, xOffset - nameOffset, yOffset - 10,
					Colors.get(-1, -1, -1, 500), 1);
		}
		
		this.renderStatus(screen, xOffset, yOffset);
	}

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x + xMin, y + yMin, xMax - xMin + 1, yMax - yMin
				+ 1);
	}

}
