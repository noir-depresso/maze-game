package fgp.game.bodies;

import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.game.constants.ZIndexes;

@LoadImage("door_32.png")
public class Door extends BodySimple {
	
	public Door(Layer l, int x, int y) {
		super(l, x, y, ZIndexes.DOOR);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return false;
	}

}
