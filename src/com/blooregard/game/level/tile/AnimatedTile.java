package com.blooregard.game.level.tile;

import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;

public class AnimatedTile extends BasicTile {

	private int[][] animationTileCoords;
	private int currentAnimationIndex;
	private long lastIterationTime;
	private int animationSwitchDelay;
	
	public AnimatedTile(int id, int[][] animationCoords, int tileColor, int levelColor, int animationSwitchDelay) {
		super(id, animationCoords[0][0], animationCoords[0][1], tileColor, levelColor);
		this.animationTileCoords = animationCoords;
		this.currentAnimationIndex = 0;
		this.lastIterationTime = System.currentTimeMillis();
		this.animationSwitchDelay = animationSwitchDelay;
	}
	
	public void tick() {	
		if((System.currentTimeMillis() - lastIterationTime) >= (animationSwitchDelay)) {
			lastIterationTime = System.currentTimeMillis();
			currentAnimationIndex = ( currentAnimationIndex + 1 ) % animationTileCoords.length;
			tileId = (animationTileCoords[currentAnimationIndex][0] + (animationTileCoords[currentAnimationIndex][1] * 32));
		}
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, tileColor);
	}


}
