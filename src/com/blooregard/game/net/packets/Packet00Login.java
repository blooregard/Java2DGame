package com.blooregard.game.net.packets;

import java.util.UUID;

import com.blooregard.game.entities.Player;
import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet00Login extends Packet {

	private Player player;

	public Packet00Login(byte[] data) {
		super(00);
		player = (Player) readData(data);
		this.uuid = this.player.getUUID();
	}

	public Packet00Login(Player player) {
		super(00);
		this.player = player;
		this.uuid = this.player.getUUID();
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
		return ("00" + "|" + player.getData()).getBytes();
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public Object readData(byte[] data) {
		String[] msgBody = new String(data).trim().split("\\|");
		//UUID, name, x, y, direction, health, mana
		Player player = new Player(null, null, UUID.fromString(msgBody[2]),
				msgBody[3], Integer.parseInt(msgBody[4]),
				Integer.parseInt(msgBody[5]), Integer.parseInt(msgBody[6]),
				Integer.parseInt(msgBody[7]), Integer.parseInt(msgBody[8]));
		return player;
	}

}
