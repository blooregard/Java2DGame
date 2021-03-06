package com.blooregard.game.level.tile;

import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;

public abstract class Tile {

	public static final Tile[] tiles = new Tile[256];
	public static Tile VOID = new BasicSolidTile(0, 0, 0, Colors.get(000, -1,
			-1, -1), 0xff000000);
	public static Tile STONE = new BasicSolidTile(1, 1, 0, Colors.get(-1, 333,
			-1, -1), 0xff555555);;
	public static Tile GRASS = new BasicTile(2, 2, 0, Colors.get(-1, 131, 141,
			-1), 0xff00ff00);;
	public static Tile WATER = new AnimatedTile(3, new int[][] { { 0, 5 },
			{ 1, 5 }, { 2, 5 } }, Colors.get(-1, 004, 115, -1), 0xff0000ff,
			1000);

	protected byte id;
	protected boolean solid;
	protected boolean emitter;
	private int levelColor;

	public Tile(int id, boolean isSolid, boolean isEmitter, int levelColor) {
		this.id = (byte) id;
		if (tiles[id] != null)
			throw new RuntimeException("Duplicate tile id on " + id);
		this.solid = isSolid;
		this.emitter = isEmitter;
		this.levelColor = levelColor;
		tiles[id] = this;
	}

	public byte getId() {
		return id;
	}

	public boolean isSolid() {
		return solid;
	}

	public boolean isEmitter() {
		return emitter;
	}

	public int getLevelColor() {
		return levelColor;
	}

	public abstract void tick();

	public abstract void render(Screen screen, Level level, int x, int y);
}
