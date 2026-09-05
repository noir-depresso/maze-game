/**
 * 
 */
package fgp.game.bodies;

import java.awt.event.KeyEvent;

import fgp.engine.Direction;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.game.classes.Chaser;
import fgp.game.classes.Maze;
import fgp.game.constants.ZIndexes;
import fgp.game.layers.CharactersLayer;

/**
 * 
 */
@LoadImage("koopa_32.png")
public class Crawler extends Chaser {
	private Runner mario;
	
	public Crawler(Layer l, int x, int y, Runner m) {
		super(l, x, y, true);
		this.mario = m;
			
			
		// TODO Auto-generated constructor stub
	}


@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return super.collision(myPart, otherPart, dx, dy);
	}

	@Override
	public void advance2() {
		if(frozen==0) {
			int mx = mario.getX();
			int my= mario.getY();
			int x = getX();
			int y = getY();
			int a = (int)Math.signum(mx-x);
			int b = (int)Math.signum(my-y);
			move(a,b);
		}
		else {
			frozen--;
		}
	}

	@Override
	protected int getAdvanceDelay() {
		// frame would u like in between times the advance2 get activated
		return speed-3*Runner.speedfactor;
	}

}
