package com.blooregard.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.blooregard.game.entities.Player;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Font;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.gfx.SpriteSheet;
import com.blooregard.game.level.Level;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH/12*9;
	public static final int SCALE = 3;
	public static final String NAME = "Game";
	public static final int COLOR_SHADES = 6;
	
	private JFrame frame;
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	private int[] colors = new int[COLOR_SHADES*COLOR_SHADES*COLOR_SHADES];
	
	private int ticks = 0;
	private int frames = 0;
	private Screen screen;
	private InputHandler input;
	public Level level;
	public Player player;
	
	public Game() {
		this.setMinimumSize(new Dimension (WIDTH* SCALE, HEIGHT * SCALE));
		this.setMaximumSize(new Dimension (WIDTH* SCALE, HEIGHT * SCALE));
		this.setPreferredSize(new Dimension (WIDTH* SCALE, HEIGHT * SCALE));
		
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
		for (int r =0; r < COLOR_SHADES; r++) {
			for (int g =0; g < COLOR_SHADES; g++) {
				for (int b =0; b < COLOR_SHADES; b++) {
					int rr = (r * 255 /5);
					int gg = (g * 255 /5);
					int bb = (b * 255 /5);
					
					colors[index++] = rr << 16 | gg << 8 | bb; 
				}
			}
		}
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		level = new Level("/levels/water_test_level.png");
		player = new Player(level, 0, 0, input);
		level.addEntity(player);
	}
	
	public synchronized void start() {
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop() {
		running = false;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		requestFocus();
		
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime)/nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while(delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			if(shouldRender) {
				frames++;
				render();
			}
			
			if (System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				frame.setTitle( NAME + " " + ticks + " ticks, " + frames + " frames");
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
		
		int xOffset = player.x - (screen.width/2);
		int yOffset = player.y - (screen.height/2);
		
		level.renderTiles(screen, xOffset, yOffset);		
		level.renderEntities(screen);
				
		for ( int y = 0; y < screen.height; y++) {
			for ( int x = 0; x < screen.width; x++) {
				int colorCode = screen.pixels[x + y * screen.width];
				if ( colorCode < 255) pixels[x + y * WIDTH] = colors[colorCode];
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

}
