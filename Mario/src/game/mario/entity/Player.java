package game.mario.entity;

import java.awt.Graphics;

import game.mario.Game;
import game.mario.Handler;
import game.mario.Id;
import game.mario.tile.Tile;

public class Player extends Entity {

	private int frame = 0;
	private int frameDelay = 0;
	private boolean animate = false;

	public Player(int x, int y, int width, int height, boolean solid, Id id, Handler handler) {
		super(x, y, width, height, solid, id, handler);
	}

	public void render(Graphics g) {
		if (facing == 0)
			g.drawImage(Game.player[frame + 2].getBufferedImage(), x, y, width, height, null);
		else if (facing == 1)
			g.drawImage(Game.player[frame + 4].getBufferedImage(), x, y, width, height, null);

	}

	public void tick() {
		x += velX;
		y += velY;


		if (y + height >= 771)
			y = 771 - height;

		if (velX != 0)
			animate = true;
		else
			animate = false;

		for (Tile t : handler.tile) {
			if (!t.solid)
				break;

			if (t.getId() == Id.wall) {
				if (getBoundsTop().intersects(t.getBounds())) {
					setVelY(0);
					if (jumping) {
						jumping = false;
						gravity = 0.8;
						falling = true;
					}
				}
			}

			if (t.getId() == Id.wall) {
				if (getBoundsBottom().intersects(t.getBounds())) {
					setVelY(0);
					if (falling)
						falling = false;
					else {
						if (!falling && !jumping) {
							gravity = 0.8;
							falling = true;
						}

					}
				}
			}

			if (t.getId() == Id.wall) {
				if (getBoundsLeft().intersects(t.getBounds())) {
					setVelX(0);
					x = t.getX() + t.width;
				}
			}

			if (t.getId() == Id.wall) {
				if (getBoundsRight().intersects(t.getBounds())) {
					setVelX(0);
					x = t.getX() - t.width;
				}
			}

		}

		// error when d(gravity) = 0.5, why?
		if (jumping) {
			gravity -= 0.1;
			setVelY((int) -gravity);

			if (gravity <= 0.0) {
				jumping = false;
				falling = true;
			}
		}

		if (falling) {
			gravity += 0.1;
			setVelY((int) gravity);
		}

		if (animate) {
			frameDelay++;
			if (frameDelay >= 12) {
				frame++;
				if (frame >= 2) {
					frame = 0;
				}
				frameDelay = 0;
			}
		}

	}

}
