package fgp.game.bodies;

import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.game.constants.ZIndexes;

@LoadImage("chest.png")
public class Grass extends BodySimple {
	
	public Grass(Layer l, int x, int y) {
		super(l, x, y, ZIndexes.CHEST);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return false;
	}

}
