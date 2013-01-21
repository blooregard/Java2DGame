package com.blooregard.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.blooregard.game.entities.Player;
import com.blooregard.game.entities.PlayerMP;
import com.blooregard.game.entities.loot.Loot;
import com.blooregard.game.entities.loot.LootTypes;
import com.blooregard.game.entities.mobs.Tonberry;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.gfx.SpriteSheet;
import com.blooregard.game.level.Level;
import com.blooregard.game.listener.InputHandler;
import com.blooregard.game.listener.MouseHandler;
import com.blooregard.game.listener.WindowHandler;
import com.blooregard.game.net.GameClient;
import com.blooregard.game.net.GameServer;
import com.blooregard.game.net.packets.Packet00Login;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 300;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Game";
	public static final int COLOR_SHADES = 6;
	public JFrame frame;

	public boolean running = false;
	public int tickCount = 0;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
			.getData();
	private int[] colors = new int[COLOR_SHADES * COLOR_SHADES * COLOR_SHADES];

	private Screen screen;
	public InputHandler input;
	public WindowHandler windowHandler;
	public MouseHandler mouseHandler;
	public Level level;
	public Player player;

	public GameClient socketClient;
	public GameServer socketServer;

	public Game() {
		this.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		this.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		frame = new JFrame(NAME);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);		
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	public void init() {
		int index = 0;
		for (int r = 0; r < COLOR_SHADES; r++) {
			for (int g = 0; g < COLOR_SHADES; g++) {
				for (int b = 0; b < COLOR_SHADES; b++) {
					int rr = (r * 255 / 5);
					int gg = (g * 255 / 5);
					int bb = (b * 255 / 5);

					colors[index++] = rr << 16 | gg << 8 | bb;
				}
			}
		}

		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		windowHandler = new WindowHandler(this);
		mouseHandler = new MouseHandler(this);
		level = new Level("/levels/Level1.png");
		player = new PlayerMP(this, level, UUID.randomUUID(),
				JOptionPane.showInputDialog(this, "Please enter a username"),
				20, 100, input,	null, -1);
		Packet00Login loginPacket = new Packet00Login(player);
		if (socketServer != null) {
			// TODO move this to the game loop and replace with mob generator
			Tonberry tonberry = new Tonberry(this, level, UUID.randomUUID(),
					80, 300);
			socketServer.addMob(tonberry);
			level.addEntity(tonberry);
			
			// TODO remove this later
			LootTypes lt = LootTypes.get("MANA_POTION");
			Loot mp = lt.get(this, level, 100, 200);
			level.addEntity(mp);

			socketServer.addConnection((PlayerMP) player, loginPacket);
		}
		loginPacket.writeData(socketClient);

		level.addEntity(player);
	}

	public synchronized void start() {
		running = true;
		new Thread(this).start();

		if (JOptionPane
				.showConfirmDialog(this, "Do you want to run the server") == 0) {
			socketServer = new GameServer(this);
			socketServer.start();

		}

		socketClient = new GameClient(this, "localhost");
		socketClient.start();
	}

	public synchronized void stop() {
		running = false;
	}

	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D / 60D;

		int ticks = 0;
		int frames = 0;

		long lastTimer = System.currentTimeMillis();
		double delta = 0;

		init();
		requestFocus();

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;

			while (delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			if (shouldRender) {
				frames++;
				render();
			}

			if (System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				frame.setTitle(NAME + " " + ticks + " ticks, " + frames
						+ " frames");
				frames = 0;
				ticks = 0;
			}
		}

	}

	public void tick() {
		tickCount++;
		level.tick();
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		int xOffset = player.x - (screen.width / 2);
		int yOffset = player.y - (screen.height / 2);

		level.renderTiles(screen, xOffset, yOffset);
		level.renderEntities(screen);
		level.cleanUp();
		screen.renderHud();

		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				int colorCode = screen.pixels[x + y * screen.width];
				if (colorCode < 255)
					pixels[x + y * WIDTH] = colors[colorCode];
			}
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		new Game().start();
	}

	public void click(int x, int y) {
		this.screen.onScreenClick(level, player, x / SCALE, y / SCALE);
	}

}
