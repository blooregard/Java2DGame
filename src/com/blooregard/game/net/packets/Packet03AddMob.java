package com.blooregard.game.net.packets;

import com.blooregard.game.entities.Mob;
import com.blooregard.game.entities.mobs.Tonberry;
import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet03AddMob extends Packet {

	private Mob mob;

	public Packet03AddMob(byte[] data) {
		super(03);
		this.mob = (Mob) readData(data);
	}

	public Packet03AddMob(Mob mob) {
		super(03);
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
		Mob mob = new Tonberry(null, null, Integer.parseInt(msgBody[1]),
				Integer.parseInt(msgBody[2]));
		mob.setMovingDir(Integer.parseInt(msgBody[4]));
		return mob;
	}

	@Override
	public byte[] getData() {
		return ("03" + "|" + mob.x + "|" + mob.y + "|" + mob
				.getName()+ "|" + mob.getMovingDir()).getBytes();
	}
	
	public Mob getMob() {
		return this.mob;
	}

}
