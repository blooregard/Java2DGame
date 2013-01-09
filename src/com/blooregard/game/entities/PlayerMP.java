package com.blooregard.game.entities;

import java.net.InetAddress;

import com.blooregard.game.Game;
import com.blooregard.game.InputHandler;
import com.blooregard.game.level.Level;

public class PlayerMP extends Player {

	public InetAddress ipAddress;
	public int port;

	public PlayerMP(Game game, Level level, int x, int y, InputHandler input,
			String username, InetAddress ipAddress, int port) {
		super(game, level, x, y, input, username);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public PlayerMP(Game game, Level level, int x, int y, String username,
			InetAddress ipAddress, int port) {
		super(game, level, x, y, null, username);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void tick() {
		super.tick();
	}

}
