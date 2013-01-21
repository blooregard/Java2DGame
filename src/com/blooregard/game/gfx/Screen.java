package com.blooregard.game.gfx;

import com.blooregard.game.entities.Player;
import com.blooregard.game.entities.projectiles.Spell;
import com.blooregard.game.entities.projectiles.SpellTypes;
import com.blooregard.game.level.Level;

public class Screen {

	public static final int MAP_WIDTH = 64;
	public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

	public static final byte BIT_MIRROR_X = 0x01;
	public static final byte BIT_MIRROR_Y = 0x02;

	public int[] pixels;

	public int xOffset = 0;
	public int yOffset = 0;

	public int width;
	public int height;

	public SpriteSheet sheet;

	public Screen(int width, int height, SpriteSheet sheet) {
		this.width = width;
		this.height = height;
		this.sheet = sheet;

		pixels = new int[width * height];

	}

	public void render(int xPos, int yPos, int tileIndex, int color) {
		render(xPos, yPos, tileIndex, color, 0x00, 1);
	}

	public void render(int xPos, int yPos, int width, int height, int[] pixels,
			int scale) {
		xPos -= xOffset;
		yPos -= yOffset;
		int scaleMap = scale - 1;

		for (int y = 0; y < height; y++) {
			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << 3) / 2);
			if (yPixel < 0 || yPixel >= this.height)
				continue;

			for (int x = 0; x < width; x++) {
				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << 3) / 2);
				if (xPixel < 0 || xPixel >= this.width)
					continue;
				this.pixels[xPixel + yPixel * this.width] = pixels[x + y
						* width];
			}
		}
	}

	public void render(int xPos, int yPos, int tileIndex, int color,
			int mirrorDir, int scale) {
		xPos -= xOffset;
		yPos -= yOffset;

		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;

		int scaleMap = scale - 1;
		int xTile = tileIndex % 32;
		int yTile = tileIndex / 32;
		int tileOffset = (xTile << 3) + (yTile << 3) * sheet.width;
		for (int y = 0; y < 8; y++) {

			int ySheet = y;
			if (mirrorY)
				ySheet = 7 - y;

			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << 3) / 2);
			for (int x = 0; x < 8; x++) {

				int xSheet = x;
				if (mirrorX)
					xSheet = 7 - x;
				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << 3) / 2);

				int col = (color >> (sheet.pixels[xSheet + ySheet * sheet.width
						+ tileOffset] * 8)) & 255;
				if (col < 255) {
					for (int yScale = 0; yScale < scale; yScale++) {
						if (yPixel + yScale < 0 || yPixel + yScale >= height)
							continue;
						for (int xScale = 0; xScale < scale; xScale++) {
							if (xPixel + xScale < 0 || xPixel + xScale >= width)
								continue;
							pixels[(xPixel + xScale) + (yPixel + yScale)
									* width] = col;
						}
					}
				}
			}
		}
	}

	public void renderHud() {
		for (int i = 0; i < SpellTypes.spellTypes.length; i++) {
			SpellTypes s = SpellTypes.spellTypes[i];
			Spell spell = s.get();
			Sprite spellSprite = spell.getCurrentSprite();
			Sprite hudSprite = (i == SpellTypes.spellTypes.length - 1) ? Sprite.SELECTOR_BG_LEFT
					: (i != 0) ? Sprite.SELECTOR_BG_MID
							: Sprite.SELECTOR_BG_RIGHT;

			int spellColor = spell.getColor();
			int bgColor = Colors.get(-1, 530, 222, 300);

			int xp = (width / 2 - ((SpellTypes.spellTypes.length * hudSprite.width) / 2))
					+ ((SpellTypes.spellTypes.length - i - 1) << 4);
			int yp = height - hudSprite.height;

			int xs = hudSprite.width / 2 - spellSprite.width / 2;
			int ysOffset = spellSprite.height;

			for (int y = 0; y < hudSprite.height; y++) {
				int yt = y + yp;
				for (int x = 0; x < hudSprite.width; x++) {
					int xt = x + xp;
					if (0 > xt || xt >= width || 0 > yt || yt >= height) {
						break;
					}
					if (xt < 0) {
						xt = 0;
					}
					if (yt < 0) {
						yt = 0;
					}
					int col = 0;
					if (x >= xs && x < hudSprite.width - xs && y >= ysOffset
							&& y < hudSprite.height) {

						col = (spellColor >> (spellSprite.pixels[(x - xs)
								+ (y - ysOffset) * spellSprite.width]) * 8) & 255;
						if (col == 255) {
							col = (bgColor >> (hudSprite.pixels[x + y
									* hudSprite.width]) * 8) & 255;

						}
					} else {
						col = (bgColor >> (hudSprite.pixels[x + y
								* hudSprite.width]) * 8) & 255;
					}
					if (col != 255) {
						pixels[xt + yt * width] = col;
					}
				}
			}
		}
	}

	public void onScreenClick(Level level, Player player, int x, int y) {
		for (int i = 0; i < SpellTypes.spellTypes.length; i++) {
			SpellTypes s = SpellTypes.spellTypes[i];
			Sprite hudSprite = (i == SpellTypes.spellTypes.length - 1) ? Sprite.SELECTOR_BG_LEFT
					: Sprite.SELECTOR_BG_MID;

			int xMin = (width / 2 - ((SpellTypes.spellTypes.length * hudSprite.width) / 2))
					+ ((SpellTypes.spellTypes.length - i - 1 ) << 4);
			int yMin = height - hudSprite.height;
			int xMax = xMin + hudSprite.width;
			int yMax = yMin + hudSprite.height;

			if (xMin <= x && x <= xMax && yMin <= y && y <= yMax) {
				player.cast(s.getType());
				return;
			}
		}
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
