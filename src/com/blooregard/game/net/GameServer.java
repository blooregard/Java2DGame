package com.blooregard.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Player;
import com.blooregard.game.entities.PlayerMP;
import com.blooregard.game.net.packets.Packet;
import com.blooregard.game.net.packets.Packet.PacketTypes;
import com.blooregard.game.net.packets.Packet00Login;
import com.blooregard.game.net.packets.Packet01Disconnect;
import com.blooregard.game.net.packets.Packet02Movement;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private Game game;
	private List<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();

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
					+ "] " + ((Packet00Login) packet).getPlayer().getUsername()
					+ " has connected...");
			Player skeleton = ((Packet00Login) packet).getPlayer();
			PlayerMP player = new PlayerMP(game, game.level, skeleton.x, skeleton.y,
					skeleton.getUsername(), address, port);
			player.setMovingDir(skeleton.getMovingDir());
			this.addConnection(player, (Packet00Login) packet);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port
					+ "] " + ((Packet01Disconnect) packet).getUsername()
					+ " has left...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
		case MOVEMENT:
			packet = new Packet02Movement(data);
			packet.writeData(this);
			break;
		}
	}

	public void addConnection(PlayerMP player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (PlayerMP p : this.connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), p.ipAddress, p.port);

				packet = new Packet00Login(p);
				sendData(packet.getData(), player.ipAddress, player.port);
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(player);
		}
	}

	public void removeConnection(Packet01Disconnect packet) {
		Player player = getPlayerMP(packet.getUsername());
		this.connectedPlayers.remove(player);
		packet.writeData(this);
	}

	public PlayerMP getPlayerMP(String username) {
		for (PlayerMP player : this.connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(username)) {
				return player;
			}
		}
		return null;
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
		for (PlayerMP p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}
}
