package fgp.game.classes;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import fgp.engine.Layer;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.game.bodies.Door;
import fgp.game.bodies.Eyes;
import fgp.game.bodies.FakeWalls;
import fgp.game.bodies.Grass;
import fgp.game.bodies.Orb;
import fgp.game.bodies.Runner;
import fgp.game.bodies.Wall;
import fgp.game.constants.ZIndexes;
import fgp.game.layers.CharactersLayer;

public class Chaser extends BodySimple {
	private boolean ignoreWall = false;
	protected Runner mario;
	private String[] maze;
	public int frozen;
	public static int villainNum = 3;
	private int difficulty = 1; //3 = easy, 2 = hard, 1 = normal, 0 = extreme
	protected int speed;
	public Chaser(Layer l, int x, int y, Runner m, String[] maze) {
		super(l, x, y, ZIndexes.ENEMY);
		this.mario = m;
		this.maze = maze;
		speed = 24+difficulty*2;
		//STALKER
	}
	public Chaser(Layer l, int x, int y, boolean igwall) {
		super(l, x, y, ZIndexes.ENEMY);
		ignoreWall = igwall;
		speed = 38+difficulty;
		//CRAWLER
	}
	public Chaser(Layer l, int x, int y, Runner m) {
		super(l, x, y, ZIndexes.ENEMY);
		this.mario = m;
		speed = 14+difficulty;
		//MIMIC
	}
	
	protected Runner getRunner() {
		return mario;
	}
	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		if (otherPart instanceof Runner) {
			Runner m = (Runner) otherPart;
			if(frozen!=0) {
				return true;
			}
			m.hitChaser(this);
			return true;
		}
		if (otherPart instanceof Grass) {
			Grass g = (Grass) otherPart;
			return true;
		}
		if (otherPart instanceof Orb) {
			Orb g = (Orb) otherPart;
			return true;
		}
		if (otherPart instanceof Door) {
			Door door = (Door) otherPart;
			return true;
		}
		if (otherPart instanceof Wall) {
			Wall w = (Wall) otherPart;
			if(ignoreWall)
				return true;
			else
				return false;
		}
		if (otherPart instanceof Eyes) {
			Eyes new_name = (Eyes) otherPart;
			return true;
		}
		if (otherPart instanceof FakeWalls) {
			FakeWalls fW = (FakeWalls) otherPart;
			return true;
		} 
		return false;
	}
	
	public char getMaze(int x, int y) {
		//System.out.println(maze[x].charAt(y));
	//	System.out.println((x)+"   "+(y));
		return maze[x].charAt(y);
	}
	
	//duration as in every move opportunity aka after {speed} frames
	public void slowDown(int duration) {
		frozen = duration;
	}

	protected List<Point> bfs2(int h, int w, int[] target) {
		List<Point> path = new ArrayList<>();;
		h = game.getXWorldSize()*2+1;
		w = game.getYWorldSize()*2+1;
		Point prev;
		
		System.out.println("bfsing");
		Point start = new Point(this.getWorldY()-CharactersLayer.VERTICAL,this.getWorldX()-CharactersLayer.SIDE);
		Queue<Point> queue = new ArrayDeque<>();
		queue.add(start);
		boolean[][] visited = new boolean[h][w];
		HashMap<Point, Point> dictionary = new HashMap<Point,Point>();
		
		while(!queue.isEmpty()) {
			int[] m = target;
			//System.out.println("target location: "+Arrays.toString(m));
			Point nd = queue.remove();
			int x = (int)nd.getX();
			int y = (int)nd.getY();
			//System.out.println("current searching: "+ x+"     "+y);

			if(x==m[1] && y==m[0]) {
				//System.out.println("========================================FOUND===============================================");
				
				prev = nd;
				//System.out.println("Starat " + start);
				while(prev!=start) {
					path.add(0, prev);
					System.out.println(path.size()+"            "+prev.toString());
					prev=dictionary.get(prev);
					
				}
				//System.out.println("path:" + path.toString());
				//stop=true;
				break;
			}
			
			visited[x][y]=true;
			
			if(x-1 >= 0 && x-1 < h && !visited[x-1][y] && getMaze(x-1,y)!='#') {
				Point a = new Point(x-1,y);
				queue.add(a);
				//dictionary.put(nd, a);
				dictionary.put(a,nd);
			}
			if(x+1 >= 0 && x+1 < h && !visited[x+1][y] && getMaze(x+1,y)!='#') {
				Point b = new Point(x+1,y);	
				queue.add(b);
				//dictionary.put(nd, b);
				dictionary.put(b,nd);
			}
			if(y+1 >= 0 && y+1 < w && !visited[x][y+1] && getMaze(x,y+1)!='#') {
				Point c = new Point(x,y+1);
				queue.add(c);
				//dictionary.put(nd, c);
				dictionary.put(c,nd);
			}
			if(y-1 >= 0 && y-1 < w && !visited[x][y-1] && getMaze(x,y-1)!='#') {
				Point d = new Point(x,y-1);
				queue.add(d);
				//dictionary.put(nd, d);
				dictionary.put(d,nd);
			}
		}
		return path;
	}

	
	
	protected int[] getMLoc() {
		//System.out.println("MARION LOCATION    "+(mario.getWorldX())+"            "+(mario.getWorldY()));
		return new int[] {mario.getWorldX()-CharactersLayer.SIDE,mario.getWorldY()-CharactersLayer.VERTICAL};
	}
	
	protected boolean inVicinity(Runner m) {
		if(((Math.abs(m.getWorldX()-this.getWorldX())==1 && Math.abs(m.getWorldY()-this.getWorldY())==0) 
				|| (Math.abs(m.getWorldX()-this.getWorldX())==0 && Math.abs(m.getWorldY()-this.getWorldY())==1))
				&& !Runner.devmode
				)
		return true;
		return false;
	}

}
