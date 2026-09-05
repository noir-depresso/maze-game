/**
 * 
 */
package fgp.game.bodies;

import java.awt.event.KeyEvent;

import fgp.engine.Direction;
import fgp.engine.GameEngine;
import fgp.engine.GameMode;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.engine.inputs.Keyboarder;
import fgp.game.constants.ZIndexes;
import fgp.game.layers.CharactersLayer;
import fgp.ui.FrmSkills;
import fgp.ui.FrmShop;
import fgp.game.classes.Chaser;
import fgp.game.classes.Coordinate;
import fgp.game.classes.Items;
import fgp.game.classes.Maze;
import fgp.game.classes.Node;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * 
 */
@LoadImage("mario_32.png")
public class Runner extends BodySimple implements Keyboarder{
	private static int orbsCollected;
	private Direction d = Direction.Right;
	private Direction prevD = Direction.Right;
	public List<Direction> pathway = new ArrayList<>();
	public final Coordinate origin = new Coordinate(1+CharactersLayer.SIDE,1+CharactersLayer.VERTICAL);
	private boolean commence_move = false;
	public static final boolean devmode = true;

	public static int speedfactor = 0;
	public static void faster_Speedfactor() {
		Runner.speedfactor++;
		System.err.println("speed increaed");
	}

	private Chaser c;
	private int moving = 0;
	private boolean orbShield = false;
	
	private static boolean compass = false;
	private static int slowing_chance = 0;
	public static boolean delays = false;
	private static int tp = 0;
	
	
	public void bought(String name) {
		switch(name) {
		case "Heart":
			game.addLife();
			break;
		case "Compass":
			compass=true;
			Items.removeItem("Compass");
			break;
		case "Qliphoth":
			//random 2 SP
			break;
		case "Magic Wand":
			delays = true;
			break;
		case "Dusty Seashell":
			Items.removeItem("Dusty Seashell");
			FrmSkills.unlock("Dusty Seashell");
			//Chaser goes to wrong location
			break;
		case "Repeller":
			Items.removeItem("Repeller");
			FrmSkills.unlock("Repeller");
			break;
		case "Monkey Paw":{
			Items.removeItem("Monkey Paw");
			FrmSkills.unlock("Monkey Paw");
			break;
		}
		case "Money Bag":
			//collect from further distance??? make money limit go up?
			Items.removeItem("Money Bag");
			FrmSkills.unlock("Money Bag");
			break;
		case "Lantern Fuse":
			if(!CharactersLayer.setBoundary(CharactersLayer.getBoundary()+1)) {
				Items.removeItem("Lantern Fuse");
			}
			break;
		default:
			break;
		}
	}



	public Runner(Layer l, int x, int y) {
		super(l, x, y, ZIndexes.PLAYER);
		if(devmode) {
			CharactersLayer.setBoundary(10000);
		}
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		if (otherPart instanceof Grass) {
			Grass g = (Grass) otherPart;
			g.markForRemoval();
			game.addScore(50);
			orbsCollected+=50;
			return true;	
		}
		if (otherPart instanceof Orb) {
			Orb m = (Orb) otherPart;
			m.markForRemoval();
			orbsCollected++;
			game.addScore(1);
			return true;
		}
		if (otherPart instanceof Chaser) {
			Chaser ch = (Chaser) otherPart;
			if(ch.frozen==0) {
				hitChaser(ch);
			}
			return true;
		}
		if (otherPart instanceof Door) {
			Door d = (Door) otherPart;
			FrmShop s = new FrmShop(this);
			System.out.println(orbsCollected);
			s.setVisible(true);
			game.setGameMode(GameMode.Pause);
			return true;
		}
		if (otherPart instanceof Wall) {
			Wall w = (Wall) otherPart;
			commence_move=false;
			game.addOffset(-d.getX(),-d.getY());
			pathway.remove(pathway.size()-1);
			return false;
		}
		if (otherPart instanceof Eyes) {
			Eyes eye = (Eyes) otherPart;
			return true;
		}
		if (otherPart instanceof FakeWalls) {
			FakeWalls fW = (FakeWalls) otherPart;
			return true;
		}
		return false;
	}

	@Override
	public void keyDown(int keycode, int modifiers) {
		// TODO Auto-generated method stub
		Direction tempd = null;
		if (keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_A) {
			commence_move = true;
			tempd=(Direction.Left);
		}
		else if (keycode == KeyEvent.VK_UP || keycode == KeyEvent.VK_W) {
			commence_move = true;
			tempd=(Direction.Up);
		}
		else if (keycode == KeyEvent.VK_DOWN || keycode == KeyEvent.VK_S) {
			commence_move = true;
			tempd=(Direction.Down);
		}
		else if (keycode == KeyEvent.VK_RIGHT || keycode == KeyEvent.VK_D) {
			commence_move = true;
			tempd=(Direction.Right);
		}
		else if (keycode == KeyEvent.VK_1) {
			if(devmode) {
			//dev hotkey
				commence_move=false;
				int ex = Maze.exit[0]+CharactersLayer.SIDE;
				int ey = Maze.exit[1]+CharactersLayer.VERTICAL;
				this.forceLocation(ex, ey);
				checkSpot();
				game.addOffset(ex-game.getXOffset()-CharactersLayer.SIDE,ey-game.getYOffset()-CharactersLayer.VERTICAL);		
			}
			else {
				//manget
			}
		}
		else if (keycode == KeyEvent.VK_2&&c!=null) {
			//freezing hotkey
			if(devmode || slowing_chance>0) {
				c.slowDown(3);
				slowing_chance--;
			}
		}
		else if (keycode == KeyEvent.VK_3) {
			if(devmode) {
				if(c!=null)
					c.markForRemoval();
				c=null;
			}
			else {
				//trap
			}
		}
		else if (keycode == KeyEvent.VK_4) {
			int[] loc = Maze.rollRandom(false);
			//System.out.println(Arrays.toString(loc));
			int x = loc[0];
			int y = loc[1];
			
			if((int)(Math.random()*2)==1) {
				//chaser_tp
				if(c!=null) {
					c.forceLocation(x, y);
				}
				else {
					//runner tp
					commence_move=false;
					forceLocation(x, y);
					checkSpot();
					game.addOffset(x-game.getXOffset()-CharactersLayer.SIDE,y-game.getYOffset()-CharactersLayer.VERTICAL);
					}
			}
			else {
				//runner tp
				commence_move=false;
				forceLocation(x, y);
				checkSpot();
				game.addOffset(x-game.getXOffset()-CharactersLayer.SIDE,y-game.getYOffset()-CharactersLayer.VERTICAL);
			}

			tp--;
		}
		
		
		if(tempd!=null) {
			prevD=d;
			d=tempd;
			moving=1;

		}
//		if(tempd!=null && tempd!=d) {
//			prevD=d;
//			d=tempd;
//			System.out.println("ITERATED");
//			System.out.println(prevD+"          "+d);
//		}
		
	}

	@Override
	public void advance2() {
		// TODO Auto-generated method stub
		if(commence_move ) {
			if(pathway.size() > 0 && pathway.get(pathway.size()-1) == d.opposite()) {
				//System.out.println("SAMEEEEE");
				pathway.remove(pathway.size()-1);
				//pathway.remove(pathway.size()-1);
			}
			else
				pathway.add(d);
			game.addOffset(d.getX(),d.getY());
			move(d);
			
		}
		
	}



	@Override
	protected int getAdvanceDelay() {
		// TODO Auto-generated method stub
		return 10-speedfactor;
	}

	public void updateChaser(Chaser c) {
		this.c=c;
		//System.out.println(c.getPrettyLocation());
	}

	@Override
	public void keyUp(int keycode, int modifiers) {
		// TODO Auto-generated method stub
		
	}
	public void hitChaser(Chaser ch) {	
		if(!devmode && !orbShieldBlock()) {
			System.out.println("HIT");
			if(Grace()) {
				System.out.println("GRACE!!!!");
			}
			else {
				game.lifeLost();
			}
		}
	}

	private boolean Grace() {
		// TODO Auto-generated method stub
		return Math.random()<=0.05;
	}



	public int getOrbs() {
		// TODO Auto-generated method stub
		return orbsCollected;
	}
	
	public void setOrbs(int value) {
		// TODO Auto-generated method stub
		orbsCollected+=value;
	}
	
	public Direction removePath() {
		if(pathway.size()>1)
			return pathway.remove(0);
		return null;
	}
	
	private boolean orbShieldBlock() {
		if(orbShield) {
			if(orbsCollected>100) {
				orbsCollected-=100;
				return true;
			}
		}
		return false;
	}
	private void checkSpot() {
		int x = getX();
		int y = getY();
		List<IBodyPart> ahead = game.search(x, y);
		System.out.println(ahead);
		if (ahead != null) {
			for (IBodyPart bp : ahead) {
				
				
				if (bp instanceof Grass) {
					Grass g = (Grass) bp;
					g.markForRemoval();
					game.addScore(50);
					orbsCollected+=50;
				}
				if (bp instanceof Orb) {
					Orb m = (Orb) bp;
					m.markForRemoval();
					orbsCollected+=1;
					game.addScore(1);
				}
				if (bp instanceof Chaser) {
					Chaser ch = (Chaser) bp;
					if(ch.frozen==0) {
						hitChaser(ch);
					}
				}
				if (bp instanceof Door) {
					Door d = (Door) bp;
					FrmShop s = new FrmShop(this);
					System.out.println(orbsCollected);
					s.setVisible(true);
					game.setGameMode(GameMode.Pause);
				}
			}
		}
	}


}
