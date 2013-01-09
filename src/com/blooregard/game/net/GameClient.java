package com.blooregard.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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

public class GameClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket socket;
	private Game game;

	public GameClient(Game game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
			// System.out.println("SERVER > " + new String(packet.getData()));
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
					+ "] " + ((Packet00Login) packet).getPlayer().getUsername()
					+ " has joined the game...");
			Player skeleton = ((Packet00Login) packet).getPlayer();
			PlayerMP player = new PlayerMP(game, game.level, skeleton.x, skeleton.y,
					skeleton.getUsername(), address, port);
			player.setMovingDir(skeleton.getMovingDir());
			game.level.addEntity(player);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet01Disconnect) packet).getUsername()
					+ " has left the world...");
			game.level.removePlayerMP(((Packet01Disconnect) packet)
					.getUsername());
			break;
		case MOVEMENT:
			packet = new Packet02Movement(data);
			Mob mob = ((Packet02Movement) packet).getMob();
			this.moveMob(mob);
			break;
		}
	}

	private void moveMob(Mob mob) {
		for (Entity e : game.level.entities) {
			if (e instanceof Mob && ((Mob)e).getName().equalsIgnoreCase(mob.getName())) {	
				e.x = mob.x;
				e.y = mob.y;
				((Mob)e).setMovingDir(mob.getMovingDir());
			}
		}
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length,
				ipAddress, 1972);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
