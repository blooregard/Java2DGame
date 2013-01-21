package com.blooregard.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Entity;
import com.blooregard.game.entities.Mob;
import com.blooregard.game.entities.Player;
import com.blooregard.game.entities.PlayerMP;
import com.blooregard.game.net.packets.Packet;
import com.blooregard.game.net.packets.Packet.PacketTypes;
import com.blooregard.game.net.packets.Packet00Login;
import com.blooregard.game.net.packets.Packet01Disconnect;
import com.blooregard.game.net.packets.Packet02Movement;
import com.blooregard.game.net.packets.Packet03AddMob;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private Game game;
	private Map<UUID, PlayerMP> connectedPlayers = new ConcurrentHashMap<UUID, PlayerMP>();
	private Map<UUID, Mob> connectedMobs = new ConcurrentHashMap<UUID, Mob>();

	public GameServer(Game game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(1972);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.parsePacket(packet.getData(), packet.getAddress(),
					packet.getPort());
			// String message = new String(packet.getData());
			// if(message.trim().equalsIgnoreCase("ping")) {
			// System.out.println("CLIENT [" +
			// packet.getAddress().getHostAddress() + ":" + packet.getPort() +
			// "]> " + message);
			// sendData("pong".getBytes(), packet.getAddress(),
			// packet.getPort());
			// }
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet00Login) packet).getPlayer().getUUID()
					+ " has connected...");
			Player skeleton = ((Packet00Login) packet).getPlayer();
			PlayerMP player = new PlayerMP(game, game.level,
					skeleton.getUUID(), skeleton.getName(), skeleton.x,
					skeleton.y, address, port);
			player.setMovingDir(skeleton.getMovingDir());
			this.addConnection(player, (Packet00Login) packet);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet01Disconnect) packet).uuid
					+ " has left...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
		case MOVEMENT:
			packet = new Packet02Movement(data);
			packet.writeData(this);
			break;
		case ADD_MOB:
			packet = new Packet03AddMob(data);
			this.addMob(((Packet03AddMob) packet).getMob());
			break;
		}
	}

	public void addMob(Mob mob) {
		synchronized (this.connectedMobs) {
			this.connectedMobs.put(mob.getUUID(), mob);
		}
	}

	public void addConnection(PlayerMP player, Packet00Login packet) {
		boolean alreadyConnected = false;
		//TODO fix this
		for (PlayerMP p : this.connectedPlayers.values()) {
			if (player.getName().equalsIgnoreCase(p.getName())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), p.ipAddress, p.port);

				Packet otherGuyPacket = new Packet00Login(p);
				sendData(otherGuyPacket.getData(), player.ipAddress,
						player.port);

				for (Mob m : this.connectedMobs.values()) {
					Packet mobPacket = new Packet03AddMob(m);
					sendData(mobPacket.getData(), player.ipAddress, player.port);
				}
			}
		}

		if (!alreadyConnected) {
			this.connectedPlayers.put(player.getUUID(), player);
		}
	}

	public void removeConnection(Packet01Disconnect packet) {
		Player player = getPlayerMP(packet.uuid);
		this.connectedPlayers.remove(player);
		packet.writeData(this);
	}

	public PlayerMP getPlayerMP(UUID uuid) {
		return this.connectedPlayers.get(uuid);
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length,
				ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (PlayerMP p : connectedPlayers.values()) {
			sendData(data, p.ipAddress, p.port);
		}
	}
}
