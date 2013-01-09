package com.blooregard.game.net.packets;

import com.blooregard.game.entities.Player;
import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet00Login extends Packet {

	private Player player;

	public Packet00Login(byte[] data) {
		super(00);
		player = (Player) readData(data);
	}

	public Packet00Login(Player player) {
		super(00);
		this.player = player;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("00" + "|" + player.x + "|" + player.y + "|" + player
				.getUsername()+ "|" + player.getMovingDir()).getBytes();
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public Object readData(byte[] data) {
		String[] msgBody = new String(data).trim().split("\\|");
		Player player = new Player(null, null, Integer.parseInt(msgBody[1]),
				Integer.parseInt(msgBody[2]), null, msgBody[3]);
		player.setMovingDir(Integer.parseInt(msgBody[4]));
		return player;
	}

}
