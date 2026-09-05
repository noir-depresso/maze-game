package fgp.game.bodies;

import fgp.engine.ImageTracker;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.BodySimple;
import fgp.engine.bodies.IBodyPart;
import fgp.game.constants.ZIndexes;

@LoadImage({"eyes0.png","eyes1.png","eyes2.png","eyes3.png","eyes4.png"})
public class Eyes extends BodySimple {
	
	private int spriteIndex;
	public static int rarity = 10;
	public Eyes(Layer l, int x, int y) {
		super(l, x, y, ZIndexes.BACKGROUND);
		// TODO Auto-generated constructor stub
		spriteIndex = (int) (Math.random()*5);
	}
	
	@Override
	protected boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void advance2() {
		// TODO Auto-generated method stub
		ImageTracker tracker = getImageTracker();
		
		tracker.setSpriteIndex(spriteIndex);
		spriteIndex++;
		if(spriteIndex>4) {
			spriteIndex=0;
		}
	}

	@Override
	protected int getAdvanceDelay() {
		// TODO Auto-generated method stub
		return 20;
	}

	
}
