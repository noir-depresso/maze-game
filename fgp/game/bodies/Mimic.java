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

@LoadImage("green_mushroom_32.png")
public class Mimic extends Chaser {
	
	private Point prev;
	private boolean stop = false;
	List<Point> walkinglist;


	public Mimic(Layer l, int x, int y, Runner m) {
		super(l, x, y, m);
		
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return super.collision(myPart, otherPart, dx, dy);
	}

	@Override
	public void advance2() {

		Point nextStep;
		if(frozen==0) {
			if(inVicinity(mario))
				mario.hitChaser(this);
			
			//System.out.println(move);
			Direction d = mario.removePath();
			if(d!=null)
				move(d);
		}
		else {
			frozen--;
		}
		
	}
	
	@Override
	protected int getAdvanceDelay() {
		// TODO Auto-generated method stub
		return speed - Runner.speedfactor/2;
	}
	
	
	
}
