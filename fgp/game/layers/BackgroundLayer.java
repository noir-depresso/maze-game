package fgp.game.layers;

import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.bodies.Body;
import fgp.game.bodies.Grass;
import fgp.game.bodies.Wall;
import fgp.game.classes.Maze;
import fgp.game.classes.Node;
import fgp.game.bodies.Orb;
import fgp.game.bodies.Stalker;
import fgp.game.bodies.Door;
import fgp.game.bodies.Eyes;
public class BackgroundLayer extends Layer {
	
	public BackgroundLayer() {
		// TODO Auto-generated constructor stub
//		for(int i = 3; i < 8; i++) {
//			for(int j = 4; j < 7; j++) {
//				grs = new Grass(this, i, j);
//				addBody(grs);
//			}
		//}
		
		//m.printMaze();
		
	}

	@Override
	protected void changeLevel(int level) {
		// TODO Auto-generated method stub
		clearBodies();
		
	}


	

}

