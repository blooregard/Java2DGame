package com.blooregard.game.net.packets;

import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet01Disconnect extends Packet {
	
	private String username;
	
	public Packet01Disconnect(byte[] data) {
		super(01);
		this.username = (String)readData(data);
	}
	
	public Packet01Disconnect(String username) {
		super(01);
		this.username = username;
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
		return ("01" + this.username).getBytes();
	}

	public String getUsername() {
		return username;
	}

	@Override
	public Object readData(byte[] data) {
		String message = new String(data).trim();
        return message.substring(2);
	}

}
