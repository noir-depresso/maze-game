package fgp.game.bodies;

import fgp.engine.ImageTracker;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.game.constants.ZIndexes;

@LoadImage({"floor_bright01.png", "floor_bright02.png"})
public class Wall extends BodySimple{

	private int x;
	private int y;

	public Wall(Layer l, int x, int y, int look) {
		super(l, x, y, ZIndexes.WALL);
		// TODO Auto-generated constructor stub
		ImageTracker tracker = getImageTracker();
		tracker.setSpriteIndex(look);
		
	}

	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return false;
	}



}
