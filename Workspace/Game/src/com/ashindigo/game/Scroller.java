
package com.ashindigo.game;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.Log;

public class Scroller extends BasicGame {
	private static final int TANK_SIZE = 32;
	private static final int TILE_SIZE = 32;
	private static final float TANK_MOVE_SPEED = 0.003F;
	private static final float TANK_ROTATE_SPEED = 0.2F;
	private float playerX = 15.0F;

	private float playerY = 16.0F;
	private int widthInTiles;
	private int heightInTiles;
	private int topOffsetInTiles;
	private int leftOffsetInTiles;
	private TiledMap map;
	private Animation player;
	private float ang;
	private float dirX;
	private float dirY;
	private boolean[][] blocked;

	public Scroller() {
		super("Scroller");
	}

	public void init(GameContainer container) throws SlickException {
		SpriteSheet sheet = new SpriteSheet("testdata/scroller/sprites.png", 32, 32);

		this.map = new TiledMap("testdata/scroller/map.tmx");

		this.blocked = new boolean[this.map.getWidth()][this.map.getHeight()];
		for (int x = 0; x < this.map.getWidth(); ++x) {
			for (int y = 0; y < this.map.getHeight(); ++y) {
				int tileID = this.map.getTileId(x, y, 0);
				String value = this.map.getTileProperty(tileID, "blocked", "false");
				if ("true".equals(value)) {
					this.blocked[x][y] = true;
				}

			}

		}

		this.widthInTiles = (container.getWidth() / 32);
		this.heightInTiles = (container.getHeight() / 32);
		this.topOffsetInTiles = (this.heightInTiles / 2);
		this.leftOffsetInTiles = (this.widthInTiles / 2);

		this.player = new Animation();
		for (int frame = 0; frame < 7; ++frame) {
			this.player.addFrame(sheet.getSprite(frame, 1), 150);
		}
		this.player.setAutoUpdate(false);

		updateMovementVector();

		Log.info("Window Dimensions in Tiles: " + this.widthInTiles + "x" + this.heightInTiles);
	}

	public void update(GameContainer container, int delta) throws SlickException {
		if (container.getInput().isKeyDown(203)) {
			this.ang -= delta * 0.2F;
			updateMovementVector();
		}
		if (container.getInput().isKeyDown(205)) {
			this.ang += delta * 0.2F;
			updateMovementVector();
		}
		if ((container.getInput().isKeyDown(200))
				&& (tryMove(this.dirX * delta * 0.003F, this.dirY * delta * 0.003F))) {
			this.player.update(delta);
		}

		if ((!(container.getInput().isKeyDown(208)))
				|| (!(tryMove(-this.dirX * delta * 0.003F, -this.dirY * delta * 0.003F))))
			return;
		this.player.update(delta);
	}

	private boolean blocked(float x, float y) {
		return this.blocked[(int) x][(int) y];
	}

	private boolean tryMove(float x, float y) {
		float newx = this.playerX + x;
		float newy = this.playerY + y;

		if (blocked(newx, newy)) {
			if (blocked(newx, this.playerY)) {
				if (blocked(this.playerX, newy)) {
					return false;
				}
				this.playerY = newy;
				return true;
			}

			this.playerX = newx;
			return true;
		}

		this.playerX = newx;
		this.playerY = newy;
		return true;
	}

	private void updateMovementVector() {
		this.dirX = (float) Math.sin(Math.toRadians(this.ang));
		this.dirY = (float) (-Math.cos(Math.toRadians(this.ang)));
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		int playerTileX = (int) this.playerX;
		int playerTileY = (int) this.playerY;

		int playerTileOffsetX = (int) ((playerTileX - this.playerX) * 32.0F);
		int playerTileOffsetY = (int) ((playerTileY - this.playerY) * 32.0F);

		this.map.render(playerTileOffsetX - 16, playerTileOffsetY - 16, playerTileX - this.leftOffsetInTiles - 1,
				playerTileY - this.topOffsetInTiles - 1, this.widthInTiles + 3, this.heightInTiles + 3);

		g.translate(400 - (int) (this.playerX * 32.0F), 300 - (int) (this.playerY * 32.0F));

		drawTank(g, this.playerX, this.playerY, this.ang);

		g.resetTransform();
	}

	public void drawTank(Graphics g, float xpos, float ypos, float rot) {
		int cx = (int) (xpos * 32.0F);
		int cy = (int) (ypos * 32.0F);
		g.rotate(cx, cy, rot);
		this.player.draw(cx - 16, cy - 16);
		g.rotate(cx, cy, -rot);
	}

	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new Scroller(), 800, 600, false);
			container.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}