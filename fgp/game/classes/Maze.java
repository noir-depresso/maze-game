package fgp.game.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import fgp.engine.GameEngine;
import fgp.engine.util.Helpers;
import fgp.game.layers.CharactersLayer;

public class Maze {
	private int h;
	private static int deadEndNum;
	private int w;
	public static List<int[]> endNodeList;
	private static List<int[]> backupList;
	public boolean[][] visited;
	public Node[][] gList;
	public String[] stringMaze;
	public static final String SPACE = "";
	public static int[] exit;
	public static double fw_rarity = 6d+GameEngine.difficulty;

	public static void setFw_rarity(double fw_rarity) {
		Maze.fw_rarity = fw_rarity;
	}


	public Maze(int a, int b) {
		deadEndNum = 0;
		this.h = a;
		this.w = b;
		endNodeList = new ArrayList<>();
		gList = new Node[h][w];
		visited = new boolean[h][w]; 
		stringMaze = new String[h*2+1];
		setUp();
		dfs();
		
		
		assemble();
		
		deadEndCheck();
		
		
	}
	
	public void dfs() {
		
		Node start = gList[0][0];
		System.out.println(start.getGridForm());
		Stack<Node> stack = new Stack<>();
		stack.push(start);
			
		while(!stack.empty()) {
			Node nd = stack.peek();
			int x = nd.getX();
			int y = nd.getY();
			
			
			//System.out.println("current " + nd.toString());
			visited[x][y] = true;
			
			List<Node> neighbor = new ArrayList<>();
			
			
			if(x+1 < h && !visited[x+1][y]) {
				neighbor.add(gList[x+1][y]);
				//System.out.println(gList[x+1][y]+"   "+visited[x+1][y]);
			}
			if(y+1 < w && !visited[x][y+1])
				neighbor.add(gList[x][y+1]);
			if(x-1 >= 0 && !visited[x-1][y])
				neighbor.add(gList[x-1][y]);
			if(y-1 >= 0 && !visited[x][y-1])
				neighbor.add(gList[x][y-1]);
			
			if(neighbor.size() > 0) {
				//System.out.println(neighbor);
				int random = (int)(neighbor.size()*Math.random());
				Node neigh = neighbor.remove(random);
				//visited[neigh.getX()][neigh.getY()] = true;
				nd.register(neigh);
				neigh.register(nd);
				//System.out.println(111);
				stack.push(neigh);
			}
			else {
				stack.pop();
			}
			
		}
	}
	public void assemble() {
		String s = "";
		for (int i = 0; i < h; i++) {
			s+="#"+SPACE+"#"+SPACE;
			String top = "";
			String btm = "";
			for (int j = 0; j < w; j++) {
				
				Node nd = gList[i][j];
				top+=nd.getTop()+SPACE;
				btm+=nd.getBtm()+SPACE;
			//	System.out.print(gList[i][j]+" ");
			}
			stringMaze[i*2]=top+"#\n";
			stringMaze[i*2+1]=btm+"#\n";
		}
		stringMaze[stringMaze.length-1]=(s+"#");
		//stringMaze[0] = "";
	}
	
	public void setUp(){
		for (boolean[] tempList : visited) {
			Arrays.fill(tempList, false);
		}
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < gList.length; j++) {
				//System.out.print(visited[i][j]+" ");
			}
			System.out.println();
		}
		
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				gList[i][j] = new Node(i,j);
				//System.out.print(gList[i][j]+" ");
			}
			System.out.println();
		}
	}
	public void deadEndCheck() {
		int len = SPACE.length();
		for (int i = 0; i < gList.length; i++) {
			for (int j = 0; j < gList[0].length; j++) {
				Node nd = gList[i][j];
				//System.out.println(nd);
				int x = (nd.getX()+len)*2+1;
				int y = (nd.getY()+len)*2+1;
				int wallNum = 0;
				//below
				if(stringMaze[x+1].charAt(y)==('#')) wallNum++;
				//above
				if(stringMaze[x-1].charAt(y)==('#')) wallNum++;
				//right
				if(stringMaze[x].charAt(y+1)==('#')) wallNum++;
				//left
				if(stringMaze[x].charAt(y-1)==('#')) wallNum++;
				
				if(wallNum==3) {
					nd.deadEnd();
					stringMaze[x]=stringMaze[x].substring(0,y)+"X"+stringMaze[x].substring(y+1);
					endNodeList.add(new int[] {y,x});
					deadEndNum++;
					System.out.println("DEAD "+deadEndNum);
				}
			}
			
		}
		backupList=new ArrayList<>(endNodeList);
	}
	public int endNum() {
		return deadEndNum;
	}
	
	public static int[] rollRandom(boolean remove) {
		if(endNodeList.isEmpty()) {
			endNodeList=new ArrayList<>(backupList);
		}
		int rnd = (int)(Math.random()*(deadEndNum-2))+1;
		//System.out.println(endNodeList.toString());
		int[] pickedEnd;
		if(remove) {
			pickedEnd = endNodeList.remove(rnd);
			exit=pickedEnd;
			System.out.println();
		}
		else
			pickedEnd = endNodeList.get(rnd);
		return new int[] {pickedEnd[0]+CharactersLayer.SIDE,pickedEnd[1]+CharactersLayer.VERTICAL};
	}

	public static String[] randomizeSplits(String[] stringMaze) {
		int splitsAdded = 0;		
			for (int i = 1; i < stringMaze.length-1; i++) {
				for (int j = 1; j < stringMaze[0].length()-2; j++) {
					if(stringMaze[i].charAt(j)==('#')) {
						int roll = (int) (Math.random()*(stringMaze.length)/fw_rarity );
						if(roll==0) {
							int roll2 = (int) (Math.random()*3);
							System.out.println("splits:         "+splitsAdded);
							if(roll2==0) {
								stringMaze[i] = stringMaze[i].substring(0,j)+"F"+stringMaze[i].substring(j+1);
							}
							else {
								stringMaze[i] = stringMaze[i].substring(0,j)+" "+stringMaze[i].substring(j+1);
							}
							splitsAdded++;
						}
					}
				}
			}
			System.out.println("splits:         "+splitsAdded);
		return stringMaze;
	}
	
}
