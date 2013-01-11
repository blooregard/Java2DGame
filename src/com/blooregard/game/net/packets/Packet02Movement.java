package com.blooregard.game.net.packets;

import com.blooregard.game.entities.Mob;
import com.blooregard.game.entities.Mob.MobTypes;
import com.blooregard.game.entities.Player;
import com.blooregard.game.entities.mobs.Tonberry;
import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;

public class Packet02Movement extends Packet {

	private Mob mob;

	public Packet02Movement(byte[] data) {
		super(02);
		mob = (Mob) readData(data);
	}

	public Packet02Movement(Mob mob) {
		super(02);
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
		MobTypes type = Mob.lookupMob(msgBody[1]);
		Mob mob = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case PLAYER:
			mob = new Player(null, null, Integer.parseInt(msgBody[2]),
				Integer.parseInt(msgBody[3]), null, msgBody[4]);
			mob.setMovingDir(Integer.parseInt(msgBody[5]));
			break;
		case TONBERRY:
			mob = new Tonberry(null, null, Integer.parseInt(msgBody[2]),
				Integer.parseInt(msgBody[3]));
			mob.setMovingDir(Integer.parseInt(msgBody[5]));
			break;
		}
		return mob;
	}


	public Mob getMob() {
		return mob;
	}

	@Override
	public byte[] getData() {
		return ("02" + "|" + mob.getType().getId() + "|" + mob.x + "|" + mob.y + "|" + mob
				.getName()+ "|" + mob.getMovingDir()).getBytes();		
	}

}
