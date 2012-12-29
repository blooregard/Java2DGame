package com.blooregard.game.level.tile;

import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;

public class BasicTile extends Tile {

	protected int tileId;
	protected int tileColor;
	
	public BasicTile(int id, int x, int y, int tileColor) {
		super(id, false, false);
		this.tileId = x + y;
		this.tileColor = tileColor;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, tileColor);
	}

}
