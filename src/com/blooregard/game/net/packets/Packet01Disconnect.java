package com.blooregard.game.net.packets;

import java.util.UUID;

import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet01Disconnect extends Packet {

	public Packet01Disconnect(byte[] data) {
		super(01);
		this.uuid = (UUID)readData(data);
	}

	public Packet01Disconnect(UUID uuid) {
		super(01);
		this.uuid = uuid;
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
		return ("01" + this.uuid).getBytes();
	}

	@Override
	public Object readData(byte[] data) {
		UUID uuid = UUID.fromString(new String(data).trim().substring(2));
		return uuid;
	}

}
