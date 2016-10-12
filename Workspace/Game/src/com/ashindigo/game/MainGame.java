package com.ashindigo.game;

import java.util.HashMap;

import javax.management.timer.Timer;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.tiled.TiledMap;

/**  
*  It's a game!
* @author Asher 
*
*/
/*
 * Credits:
 * http://opengameart.org/content/pixel-worker-sprite-fukushima
 * http://www.rpgmakercentral.com/topic/17819-critique-on-unique-trees-and-tiles/	
 * http://opengameart.org/content/basic-door-32x32
 */
public class MainGame extends BasicGame {
    private TiledMap grassMap;
    private Animation sprite, up, down, left, right;
    private float x = 116, y = 153;
	private HashMap<String, String> dialogue = new HashMap<String, String>();
	private GameContainer gc;
	private static AppGameContainer app;
	// Zero-Indexed Monday to Friday
	int day = 0;
	// Story counters!
	int jobMistakes = 0;
	int addiction = 0;

    public MainGame() {
        super("Design Tech Game");
    }

    public static void main(String[] args) {
        try {
            app = new AppGameContainer(new MainGame());
            app.setDisplayMode(480, 720, false);
            app.setShowFPS(false);
            app.start();
        }
        catch (SlickException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(GameContainer container) throws SlickException {
   
        Image [] movementUp = {new Image("data/manUpRightStill.png"), new Image("data/manUpMove.png")};
        Image [] movementDown = {new Image("data/manLeftStill.png"), new Image("data/manLeftMove.png")};
        Image [] movementLeft = {new Image("data/manLeftStill.png"), new Image("data/manLeftMove.png")};
        Image [] movementRight = {new Image("data/manRightStill.png"), new Image("data/manRightMove.png")};
        int [] duration = {300, 300};         
        grassMap = new TiledMap("data/house.tmx");
        gc = container;
        up = new Animation(movementUp, duration, false);
        down = new Animation(movementDown, duration, false);
        left = new Animation(movementLeft, duration, false);
        right = new Animation(movementRight, duration, false); 
        sprite = right;
        dialogue.put("", "");
        dialogue.put("playComp", "Play a round of your favorite RTS? \n\n Press P to skip work");
        dialogue.put("roadFlower", "A red flower.\nIt reminds you of that rpg game in\n Guertena's Works.");
    }

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
	      grassMap.render(0, 0);
	      sprite.draw(x, y);
	      triggerDialogue();
	}

	@Override
	public void update(GameContainer paramGameContainer, int paramInt) throws SlickException {
		// Manages movement
		Input input = paramGameContainer.getInput();
		if (input.isKeyDown(Input.KEY_UP)) {
			if (this.isCollidable() == false) {
				sprite = up;
				sprite.update(paramInt);
				y -= paramInt * 0.1f;
			} else {
				sprite = down;
		    	sprite.update(paramInt);
		    	y += paramInt * 0.1f;
			}
		}
		else if (input.isKeyDown(Input.KEY_DOWN)) {
			if (this.isCollidable() == false) {
				sprite = down;
		    	sprite.update(paramInt);
		    	y += paramInt * 0.1f;
			} else {
				sprite = up;
		    	sprite.update(paramInt);
		    	y -= paramInt * 0.1f;
			}
		}
		else if (input.isKeyDown(Input.KEY_LEFT)) {
			if (this.isCollidable() == false) {
				sprite = left;
				sprite.update(paramInt);
				x -= paramInt * 0.1f;
			} else {
				sprite = right;
				sprite.update(paramInt);
				x += paramInt * 0.1f;
			}
		}
		else if (input.isKeyDown(Input.KEY_RIGHT)) {
			if (this.isCollidable() == false) {
				sprite = right;
				sprite.update(paramInt);
				x += paramInt * 0.1f;
			} else {
				sprite = left;
				sprite.update(paramInt);
				x -= paramInt * 0.1f;
			}
		}
		if (input.isKeyPressed(Input.KEY_P)) {
			int x = (int) (this.x / grassMap.getTileWidth());
			int y = (int) (this.y / grassMap.getTileHeight());
				if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", "").equals("playComp")) {
					System.out.println("You skipped Work!");
					addiction++;
					jobMistakes++;
					System.out.println("Addiction: " + addiction);
					System.out.println("Job Mistakes: " + jobMistakes);
					day++;
					this.triggerMapChange("house");
			}
		}
		//System.out.println("Jack X: " + x);
	//	System.out.println("Jack Y: " + y);
	}
	
	/**
	 * Triggers a dialogue box
	 * @param string Selects which dialogue to load
	 */
	private void triggerDialogue() {
		int x = (int) (this.x / grassMap.getTileWidth());
		int y = (int) (this.y / grassMap.getTileHeight());
		TextField txtf = new TextField(gc, gc.getDefaultFont(), 0, 520, 520, 100);
		txtf.setTextColor(Color.white);
		if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", "").equals("playComp")) {
			if (day <= 5) {
				txtf.setText(dialogue.get("playComp"));
			} else {
				txtf.setText("You are completely addicted to the machine\nYou cant stop \nYou severed connections with all your friends \nThis is your fate");
			}
		} else {
			String str = grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", "");
			txtf.setText(dialogue.get(str));
		}
		txtf.setBorderColor(Color.black);
		txtf.setBackgroundColor(Color.black);
		txtf.render(gc, gc.getGraphics());
		txtf.deactivate();
	}

	/**
	 * Loads a map and sets Jack to 34, 34
	 * @param string The map file name
	 * @throws SlickException
	 */
	private void triggerMapChange(String string) throws SlickException {
		grassMap = new TiledMap("data/" + string + ".tmx");
		grassMap.render(0, 0);
		x = Integer.parseInt(grassMap.getMapProperty("x", "0"));
		y = Integer.parseInt(grassMap.getMapProperty("y", "0"));
	}
	
	/**
	 * Checks collision for tiles with the "Collision" property
	 * @return If the tile is collidable
	 * @throws SlickException
	 */
	private boolean isCollidable() throws SlickException {
	int x = (int) (this.x / grassMap.getTileWidth());
	int y = (int) (this.y / grassMap.getTileHeight());
	//System.out.println("Map X: " + x);
	//System.out.println("Map Y: " + y);
	try {
		if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("doors")), "mapName", "") != "") {
			this.triggerMapChange(grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("doors")), "mapName", ""));
		}
	if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "Collision", "false").equals("true")) {
			return true;
		}
		return false;
	} catch (IndexOutOfBoundsException e) {
		e.printStackTrace();
		this.triggerMapChange("void");
		TimerThread tt = new TimerThread(Timer.ONE_SECOND * 10);
		tt.start();
		app.setTitle("W̟͎̫͟é͕͕l̡͖̯̠͖c̱̲͖̼o̱̮̯̦̻̪͠m̧ẹ̝̦̯͍̱̠́ ̠̺̮̭̤́t͉̖̻͖͔̀ọ ͚͚̝t̴̩h̤̀e ͈͜v̞̳͟ǫ͔͖̰̥̯͍̣id");
		this.x = 230;
		this.y = 200;
	}
	return false;
}
	/**
	 * A timer thread to wait for a specified time
	 * @author Asher
	 *
	 */
	private class TimerThread extends Thread {
		
		long waitTime2;
		
		public TimerThread(long waitTime) {
			waitTime2 = waitTime;
		}
		 @Override
		 public void run() {
			 try {
				TimerThread.sleep(waitTime2);
			} catch (InterruptedException e) {
			}
				System.exit(8643);
				
		 }
	}
}
