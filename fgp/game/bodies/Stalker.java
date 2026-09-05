package fgp.game.bodies;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import fgp.engine.Direction;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.IBodyPart;
import fgp.game.classes.Chaser;
import fgp.game.classes.Maze;
import fgp.game.constants.ZIndexes;
import fgp.game.layers.CharactersLayer;

@LoadImage("reaper2.png")
public class Stalker extends Chaser {
	
	private Point prev;
	private boolean stop = false;
	List<Point> walkinglist;


	public Stalker(Layer l, int x, int y, Runner m, String[] maze) {
		super(l, x, y, m, maze);
		
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return super.collision(myPart, otherPart, dx, dy);
	}

	@Override
	public void advance2() {
		walkinglist = bfs2(mario.getWorldX(),mario.getWorldY(),getMLoc());
		Point nextStep;
		
		if(frozen==0) {

			if(walkinglist.size()!=0) {
				nextStep = walkinglist.remove(0);
				forceLocation(nextStep.y+CharactersLayer.VERTICAL, nextStep.x+CharactersLayer.SIDE);
			}
		}
		else {
			frozen--;
		}
		
	}
	
	@Override
	protected int getAdvanceDelay() {
		// TODO Auto-generated method stub
		return speed - 2*Runner.speedfactor;
	}
	
	

}
