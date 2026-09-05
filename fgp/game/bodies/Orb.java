package fgp.game.bodies;

import fgp.engine.Direction;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.engine.inputs.Keyboarder;
import fgp.game.constants.ZIndexes;

@LoadImage("orb_32.png")
public class Orb extends BodySimple {

	public Orb(Layer l, int x, int y) {
		super(l, x, y, ZIndexes.ITEM);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return false;
	}

}
