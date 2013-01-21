package com.blooregard.game.gfx;

public class Sprite {
	public static Sprite FIREBALL_UP = new Sprite("FIREBALL", 0, 24, 8, 8);
	public static Sprite FIREBALL_RIGHT = new Sprite("FIREBALL", 1, 24, 8, 8);
	public static Sprite FIREBALL_DOWN = new Sprite("FIREBALL", 2, 24, 8, 8);
	public static Sprite FIREBALL_LEFT = new Sprite("FIREBALL", 3, 24, 8, 8);

	public static Sprite ICELANCE_UP = new Sprite("ICELANCE", 0, 24, 8, 8);
	public static Sprite ICELANCE_RIGHT = new Sprite("ICELANCE", 1, 24, 8, 8);
	public static Sprite ICELANCE_DOWN = new Sprite("ICELANCE", 2, 24, 8, 8);
	public static Sprite ICELANCE_LEFT = new Sprite("ICELANCE", 3, 24, 8, 8);
	
	public static Sprite SOULSTEAL_UP = new Sprite("SOULSTEAL", 0, 24, 8, 8);
	public static Sprite SOULSTEAL_RIGHT = new Sprite("SOULSTEAL", 1, 24, 8, 8);
	public static Sprite SOULSTEAL_DOWN = new Sprite("SOULSTEAL", 2, 24, 8, 8);
	public static Sprite SOULSTEAL_LEFT = new Sprite("SOULSTEAL", 3, 24, 8, 8);
	
	public static Sprite HOLYFIRE_UP = new Sprite("HOLYFIRE", 0, 24, 8, 8);
	public static Sprite HOLYFIRE_RIGHT = new Sprite("HOLYFIRE", 1, 24, 8, 8);
	public static Sprite HOLYFIRE_DOWN = new Sprite("HOLYFIRE", 2, 24, 8, 8);
	public static Sprite HOLYFIRE_LEFT = new Sprite("HOLYFIRE", 3, 24, 8, 8);
	
	public static Sprite SELECTOR_BG_LEFT = new Sprite("SELECTOR_BG_LEFT", 0,
			11, 16, 16);
	public static Sprite SELECTOR_BG_MID = new Sprite("SELECTOR_BG_MID", 1, 11,
			16, 16);
	public static Sprite SELECTOR_BG_RIGHT = new Sprite("SELECTOR_BG_RIGHT", 2,
			11, 16, 16);

	private SpriteSheet sheet = new SpriteSheet("/sprite_sheet.png");

	public String name;
	public int x, y;
	public int width, height;
	public int[] pixels;

	public Sprite(String name, int x, int y, int width, int height) {
		this.name = name;
		this.x = x * width;
		this.y = y * height;
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
		this.create();
	}

	private void create() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixels[x + y * width] = sheet.pixels[(x + this.x)
						+ (y + this.y) * sheet.width];
			}
		}
	}
}
