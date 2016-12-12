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
* It's a game!
* @author Asher 
*
*/
/*
 * Credits:
 * http://opengameart.org/content/pixel-worker-sprite-fukushima
 * http://www.rpgmakercentral.com/topic/17819-critique-on-unique-trees-and-tiles/	
 * http://opengameart.org/content/basic-door-32x32
 * http://opengameart.org/content/48-animated-old-school-rpg-characters-16x16
 * http://gaurav.munjal.us/Universal-LPC-Spritesheet-Character-Generatord
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
	Input input;
	private boolean ending = false;
	private String currentEnding;

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
    	input = container.getInput();
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
        dialogue.put("useWorkComp", "Do your job?\n\nPress P to work");
        dialogue.put("playComp", "Play a round of your favorite RTS? \n\n Press P to skip work");
        dialogue.put("roadFlower", "A red flower.\nIt reminds you of that rpg game in\n Guertena's Works.");
        dialogue.put("neutralGame", "You went to work at times\nYou managed to keep your job despite you not showing up\nIn the end you chose the game over your job.");
        dialogue.put("neutralWork", "You skipped work at times\nBut you refused to let the game take over your life\nThis is ok.");
        dialogue.put("noAddiction", "You decided to go to work\nYour life is stable\nThis feels like the best route for your life\nYour not addicted");
        dialogue.put("addictionEnding", "You are completely addicted to the machine\nYou cant stop \nYou severed connections with all your friends \nThis is your fate");
    }

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
	      grassMap.render(0, 0);
	      if (ending == false) {
	    	  sprite.draw(x, y);
	      }
	      triggerDialogue();
	      if (ending == true) {
	    	TextField txtf = new TextField(gc, gc.getDefaultFont(), 0, 0, 480, 720);
	  		txtf.setTextColor(Color.white);
	  		txtf.setBorderColor(Color.black);
			txtf.setBackgroundColor(Color.black);
			txtf.setText(dialogue.get(currentEnding));
			txtf.render(gc, gc.getGraphics());
			txtf.deactivate();
	      }
	}

	@Override
	public void update(GameContainer paramGameContainer, int paramInt) throws SlickException {
		if (input.isKeyDown(Input.KEY_UP)) { // Controls collision for going up
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
		else if (input.isKeyDown(Input.KEY_DOWN)) { // Controls for going down
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
		else if (input.isKeyDown(Input.KEY_LEFT)) { // Controls for going left
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
		else if (input.isKeyDown(Input.KEY_RIGHT)) { // Controls for going right
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
		if (input.isKeyPressed(Input.KEY_P)) { // Controls the variables for interacting with objects
			int x = (int) (this.x / grassMap.getTileWidth());
			int y = (int) (this.y / grassMap.getTileHeight());
				if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", "").equals("playComp")) {
					addiction++;
					jobMistakes++;
					day++;
					this.triggerMapChange("house");
			} else {
				if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 1")), "dialogue", "").equals("useWorkComp")) {
					if (addiction != 0) addiction--;
					day++;
					this.triggerMapChange("house");
			}
		}
		}
	}
	
	/**
	 * Triggers a dialogue box
	 * @throws SlickException 
	 */
	private void triggerDialogue() throws SlickException {
		try {
		int x = (int) (this.x / grassMap.getTileWidth());
		int y = (int) (this.y / grassMap.getTileHeight());
		TextField txtf = new TextField(gc, gc.getDefaultFont(), 0, 520, 520, 100);
		txtf.setTextColor(Color.white);
		if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", "").equals("playComp")) { // Jack's computer triggers
			if (day <= 5) {
				txtf.setText(dialogue.get("playComp"));
			} else if (addiction < 5 && addiction != 0) {
				this.triggerEnding("neutralGame");
			} else { 
				this.triggerEnding("addictionEnding");
			} 
		} else if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", "").equals(dialogue.get(grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", ""))) == false) {
			txtf.setText(dialogue.get(grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 2")), "dialogue", ""))); // For dialogue outside of main story objects
		}
		if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 1")), "dialogue", "").equals("useWorkComp")) { // Work Computer triggers
			if (day <= 5) {
				txtf.setText(dialogue.get("useWorkComp")); 
			} else if (addiction == 0) {
				this.triggerEnding("noAddiction");
			} else {
				this.triggerEnding("neutralWork");
			}
		}
			if (grassMap.getTileProperty(grassMap.getTileId(x, y, grassMap.getLayerIndex("Tile Layer 1")), "character", "").equals("lisa")) { // Decides Lisa's dialogue
				switch(jobMistakes) {
					default: txtf.setText("Hi Jack!\nNice day outside right?"); break;
					case 0: txtf.setText("Hi Jack!\n Nice day outside right?"); break;
					case 1: txtf.setText("Try to make it to work Jack\nI know you need this job"); break;
					case 2: txtf.setText("The boss is worried about\nyour work habits\nPlease dont keep this up"); break;
					case 3: txtf.setText("Your skipping work because of that game right?\nPlease dont do this to yourself"); break;
					case 4: txtf.setText("The boss wants to see you..."); break;
					case 5: txtf.setText("Good luck Jack"); break;
					case 6: txtf.setText("Why are you here?"); break;
					case 8643: txtf.setText(". . ."); break; 
				}
			} 
		txtf.setBorderColor(Color.black); // Sets standard text stuff
		txtf.setBackgroundColor(Color.black);
		txtf.render(gc, gc.getGraphics());
		txtf.deactivate();
		} catch (IndexOutOfBoundsException e) { // I wasted time making this
			e.printStackTrace();
			this.triggerMapChange("void");
			TimerThread tt = new TimerThread(Timer.ONE_SECOND * 10); // Multithreading just for a timer
			tt.start();
			app.setTitle("W̟͎̫͟é͕͕l̡͖̯̠͖c̱̲͖̼o̱̮̯̦̻̪͠m̧ẹ̝̦̯͍̱̠́ ̠̺̮̭̤́t͉̖̻͖͔̀ọ ͚͚̝t̴̩h̤̀e ͈͜v̞̳͟ǫ͔͖̰̥̯͍̣id");
			this.x = 230;
			this.y = 200;
		}
	}

	/**
	 * Loads a map and sets position based on map values
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
	 * Triggers a full screen ending
	 * @param ending The ending that will be started
	 * @throws SlickException 
	 */
	private void triggerEnding(String ending) throws SlickException {
		this.ending = true;
  	    this.gc.getGraphics().clear();
		this.triggerMapChange("void");
		sprite = null;
		switch (ending) {
		default: System.out.println("No Ending"); break;
		case "neutralGame": System.out.println("Neutral Game Ending"); currentEnding = ending; 	app.setTitle("his"); break;
		case "neutralWork": System.out.println("Neutral Work Ending"); currentEnding = ending; 	app.setTitle("Under"); break;
		case "addictionEnding": System.out.println("Addiction Ending"); currentEnding = ending; app.setTitle("8643"); break;
		case "noAddiction": System.out.println("No Addiction Ending"); currentEnding = ending; 	app.setTitle("Desk"); break;
		}
		
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

