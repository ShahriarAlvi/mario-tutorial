package game.mario;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import game.mario.entity.Entity;
import game.mario.entity.Player;
import game.mario.gfx.Sprite;
import game.mario.gfx.SpriteSheet;
import game.mario.input.KeyInput;

public class Game extends Canvas implements Runnable {

	public static final int WIDTH = 270;
	public static final int HEIGHT = WIDTH / 14 * 10;
	public static final int SCALE = 4;
	public static final String TITLE = "Mario";

	private Thread thread;
	private boolean running = false;
	private BufferedImage image;

	public static Handler handler;
	public static SpriteSheet sheet1[] = new SpriteSheet[8];
	public static SpriteSheet sheet2;
	
	public static Sprite grass;
	public static Sprite player[] = new Sprite[8];
	
	public static Camera cam;

	public Game() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE); // 1080, 1512
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);

	}

	private void init() {
		handler = new Handler();
		cam = new Camera();
		sheet1[0] = new SpriteSheet("/boy_down_1.png");
		sheet1[1] = new SpriteSheet("/boy_down_2.png");
		sheet1[2] = new SpriteSheet("/boy_left_1.png");
		sheet1[3] = new SpriteSheet("/boy_left_2.png");
		sheet1[4] = new SpriteSheet("/boy_right_1.png");
		sheet1[5] = new SpriteSheet("/boy_right_2.png");
		sheet1[6] = new SpriteSheet("/boy_up_1.png");
		sheet1[7] = new SpriteSheet("/boy_up_2.png");
		
		sheet2 = new SpriteSheet("/grass00.png");

		//handler.addEntity(new Player(312, 500, 64, 64, true, Id.player, handler));
		
		addKeyListener(new KeyInput());
		grass = new Sprite (sheet2, 1, 1);
		
		for(int i = 0; i < 8; i++)
		{
			player[i] = new Sprite(sheet1[i], 1, 1);
		}
		
		try {
			image = ImageIO.read(getClass().getResource("/level.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		handler.createLevel(image);
		
	}

	private synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this, "Thread");
		thread.start(); // creates a game loop
	}

	private synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();
		requestFocus();
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		int frames = 0;
		int ticks = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				ticks++;
				delta--;
			}
			render();
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(frames + " FPS " + ticks + " Updates");
				frames = 0;
				ticks = 0;
			}
		}
		stop();

	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3); // buffer containers for images/screens
			return;
		}
		Graphics g = bs.getDrawGraphics();

		// g.setColor(Color.BLACK);
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.translate(cam.getX(), cam.getY());

		handler.render(g);

		g.dispose(); // puts everything we have drawn on bs
		bs.show(); // shows bs
	}

	public void tick() {
		handler.tick();
		
		for(Entity e:handler.entity) {
			if(e.getId() == Id.player) {
				cam.tick(e);
			}
		}
	}
	
	public static int getFrameWidth() {
		return WIDTH*SCALE;
	}
	
	public static int getFrameHeight() {
		return HEIGHT*SCALE;
	}

	public static void main(String args[]) {
		Game game = new Game();
		JFrame frame = new JFrame(TITLE);
		frame.add(game);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes if you click x button
		// frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // do nothing if
		// you click x button
		frame.setVisible(true);
		game.start();

	}

}
