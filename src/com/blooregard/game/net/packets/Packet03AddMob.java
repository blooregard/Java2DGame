package com.blooregard.game.net.packets;

import java.util.UUID;

import com.blooregard.game.entities.Mob;
import com.blooregard.game.entities.mobs.Tonberry;
import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet03AddMob extends Packet {

	private Mob mob;

	public Packet03AddMob(byte[] data) {
		super(03);
		this.mob = (Mob) readData(data);
		this.uuid = this.mob.getUUID();
	}

	public Packet03AddMob(Mob mob) {
		super(03);
		this.uuid = mob.getUUID();
		this.mob = mob;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());

	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public Object readData(byte[] data) {
		String[] msgBody = new String(data).trim().split("\\|");
		// UUID, name, x, y, direction, health, mana
		Tonberry mob = new Tonberry(null, null, UUID.fromString(msgBody[2]),
				msgBody[3], Integer.parseInt(msgBody[4]),
				Integer.parseInt(msgBody[5]), Integer.parseInt(msgBody[6]),
				Integer.parseInt(msgBody[7]), Integer.parseInt(msgBody[8]));
		return mob;
	}

	@Override
	public byte[] getData() {
		return ("03" + "|" + mob.getData()).getBytes();
	}

	public Mob getMob() {
		return this.mob;
	}

}
