package fgp.game.classes;

import java.awt.Color;

public class Node {
	private int x;
	private int y;
	private boolean up = true;
	private boolean left = true;
	private boolean deadEnd = false;
	private String sp = Maze.SPACE;
	private static final String wall = "#";
	private String dead = " ";
	
	public Node(int a, int b) {
		// TODO Auto-generated constructor stub
		x = a;
		y = b;
	}
	public void register(Node nd) {
		int px = nd.getX();
		int py = nd.getY();
		//System.out.println(222);

		 if (py == y-1){
			left = false;

		}

		else if (px == x-1){
			up = false;

		}
	}
	
	public void deadEnd() {
		dead = "X";
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public String getTop() {
		String north = wall;
		if(!up) {
			north = " ";
		}
		return wall+sp+north;
	}
	public String getBtm() {
		String west = wall;
		if(!left) west = " ";
		return west+sp+dead;
	}
	
	public String toString() {
		return "[" + x + " , " + y + "]";
	}
	
	public String getGridForm() {
		return getTop()+"\n"+getBtm();
	}
}
