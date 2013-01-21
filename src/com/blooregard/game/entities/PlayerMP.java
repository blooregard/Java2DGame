package com.blooregard.game.entities;

import java.net.InetAddress;
import java.util.UUID;

import com.blooregard.game.Game;
import com.blooregard.game.level.Level;
import com.blooregard.game.listener.InputHandler;

public class PlayerMP extends Player {

	public InetAddress ipAddress;
	public int port;

	public PlayerMP(Game game, Level level, UUID uuid, String name, int x, int y, InputHandler input,
			InetAddress ipAddress, int port) {
		super(game, level, uuid, name, x, y, input);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public PlayerMP(Game game, Level level, UUID uuid, String name,int x, int y, 
			InetAddress ipAddress, int port) {
		super(game, level, uuid, name, x, y, null);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void tick() {
		super.tick();
	}

}
