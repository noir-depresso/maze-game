package fgp.game.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;

import fgp.engine.GameEngine;
import fgp.engine.Layer;
import fgp.engine.Waiter;
import fgp.game.bodies.Door;
import fgp.game.bodies.Eyes;
import fgp.game.bodies.Grass;
import fgp.game.bodies.Mimic;
import fgp.game.MazeGame;
import fgp.game.bodies.Crawler;
import fgp.game.bodies.Runner;
import fgp.game.bodies.FakeWalls;
import fgp.game.bodies.Stalker;
import fgp.game.bodies.Wall;
import fgp.game.classes.Chaser;
import fgp.game.classes.Items;
import fgp.game.classes.Maze;
import fgp.game.classes.Node;
import fgp.game.bodies.Orb;


public class CharactersLayer extends Layer {
	private Runner m;
	private Orb mush;
	private Chaser chaser;
	private Door door;
	private int enemySpawnTime = 200;
	private static boolean squareMode = true;
	

	public static int SIDE;
	public static int VERTICAL;
	private static int boundary = 5;
	public static boolean setBoundary(int bound) {
		if((bound>=GameEngine.getInstance().getXWorldSize()/2)||(bound>=GameEngine.getInstance().getYWorldSize()/2)) {
			return false;
		}
		else
			CharactersLayer.boundary = bound;
		return true;
	}
	public static int getBoundary() {
		return boundary;
	}

	private String[] mazes;
	
	public CharactersLayer() {
		SIDE=(int)(game.getXViewSize()-3)/2;
		VERTICAL=(int)(game.getYViewSize()-3)/2;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void changeLevel(int level) {
		// TODO Auto-generated method stub
		
		int chaser_index = (int)(Math.random()*(Chaser.villainNum))+1;
		
		clearBodies();
		Layer layer = this;
		m = new Runner(this, 1+SIDE, 1+VERTICAL);
		addBody(m);
		boolean splits = false;
		if(chaser_index>1) {
			splits=true;
		}
		mazes = regenerate(game.getXWorldSize(),game.getYWorldSize(),splits);
		//addBody(new Stalker(this,1+SIDE,1+VERTICAL,m, maze));
		
		int maxX = game.getXWorldSize()*10;
		int maxY = game.getYWorldSize()*10;
		int r = Eyes.rarity;
		
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				int rnd = (int) (Math.random()*r);
				if(rnd==0) {
					addBody(new Eyes(this,i,j) );
				}
			}
		}
		int delay = 0;
			if(Runner.delays) {
				delay = 800;
			}
			Waiter w = new Waiter(enemySpawnTime+delay) {
				@Override
				protected boolean doAction() {
					// TODO Auto-generated method stub
					
					switch (chaser_index) {
					case 1:
						chaser = new Crawler(layer, 1+SIDE, 1+VERTICAL,m);
						break;
					case 2:
						chaser = new Stalker(layer,1+SIDE,1+VERTICAL,m, mazes);
						break;
					case 3:
						chaser = new Mimic(layer,1+SIDE,1+VERTICAL,m);
						break;
					default:
						break;
					}
					addBody(chaser);
					
					m.updateChaser(chaser);
					return true;
				}
			};
		}
	public String[] regenerate(int h, int w, boolean splits) {
		
		boolean doorAdded = false;
		int orbCount = 0;
		Maze m = new Maze(h,w);
		int ends = m.endNum();
		String[] stringMaze = m.stringMaze;
		if(splits) {
			stringMaze = Maze.randomizeSplits(stringMaze);
		}
		
		for (String ln : stringMaze) {
			System.out.print(ln);
			//System.out.println(ln.length());
		}
		
		for (int i = 0; i < stringMaze.length; i++) {
			for (int j = 0; j < stringMaze[0].length()-1; j++) {
				if(stringMaze[i].charAt(j)==('#')) {
					if(i > 0 && (stringMaze[i-1].charAt(j)==('#')|| stringMaze[i-1].charAt(j)==('F')))
						addBody(new Wall(this,j+SIDE,i+VERTICAL,1));
					else
						addBody(new Wall(this,j+SIDE,i+VERTICAL,0));
				}
				else if(stringMaze[i].charAt(j)==('F')) {
					if(i > 0 && (stringMaze[i-1].charAt(j)==('#')|| stringMaze[i-1].charAt(j)==('F')))
						addBody(new FakeWalls(this,j+SIDE,i+VERTICAL,1));
					else
						addBody(new FakeWalls(this,j+SIDE,i+VERTICAL,0));
				}
				else if(stringMaze[i].charAt(j)==('X')) {
					int roll = (int) (Math.random()*(ends));
					
					if(roll == 0 && (i != 1 && j!=1)&& !doorAdded) {
						int[] loc = Maze.rollRandom(true);
						int a = loc[0];
						int b = loc[1];
						doorAdded=true;
						addBody(new Door(this,a,b));
						//Maze.setExit(j,i);
					}
					else {
						int[] loc = Maze.rollRandom(false);
						int a = loc[0];
						int b = loc[1];
						addBody(new Grass(this,a,b));
						addBody(new Orb(this,a,b));
					}

					
					ends--;
				}
				
				else {
					int roll2 = (int) (Math.random()*(game.getXWorldSize())/2);
//					if(roll2==0)
//						addBody(new FakeWalls(this, j+SIDE, i+VERTICAL,1));
//					else
						addBody(new Orb(this, j+SIDE, i+VERTICAL));
					orbCount++;
				}
			}
		}
		
		System.out.println("\n\n"+orbCount);
		System.out.println("\n\n"+doorAdded);
		return stringMaze;
	}
	@Override
	protected int getAdvanceDelay() {
		// TODO Auto-generated method stub
		return 30;
	}

	@Override
	protected void paint2(Graphics gfx, Rectangle bounds) {
		// TODO Auto-generated method stub
		int mx = m.getX();
		int my = m.getY();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				int dx = Math.abs(mx-i);
				int dy = Math.abs(my-j);
				if((squareMode && (dx>boundary || dy > boundary)) || (!squareMode && dx+dy > boundary)) {
					gfx.setColor(Color.black);
					gfx.fillRect(game.convertXGridToPixel(i), game.convertYGridToPixel(j), game.getTileSize(), game.getTileSize());
				}
			}
		}
	}
	
	

	public static void setSquareMode(boolean sqr) {
		squareMode = sqr;
	}
	

}
