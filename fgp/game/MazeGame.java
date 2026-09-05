package fgp.game;

import fgp.engine.GameEngine;
import fgp.game.layers.BackdropLayer;
import fgp.game.layers.BackgroundLayer;
import fgp.game.layers.CharactersLayer;
import fgp.game.layers.StatusLayer;
import fgp.ui.FrmLauncher;

public class MazeGame extends GameEngine {
	
	public static void main(String[] args) throws Exception {

		FrmLauncher.launch(new MazeGame());

		}
	public MazeGame() throws Exception {
		super();
		setDarkMode(true);
	}

	@Override
	public int getTargetFps() {

		return 100;
	}

	@Override
	public String getGameTitle() {
		return "Arena of Horror";
	}

	@Override
	protected int getLevels() {
		return 5;
	}

	@Override
	protected String getImagesFolder() {
		return "images";
	}

	@Override
	public void createLayers() {
		addLayer(new BackgroundLayer());
		addLayer(new CharactersLayer());
	}

	@Override
	//MAKE ODD NUMBER
	public int getXViewSize() {
		return 27;
	}

	@Override
	public int getYViewSize() {
		return 27;
	}
	//15 for actual game
	@Override
	public int getXWorldSize() {
		return 10;
	}
	@Override
	public int getYWorldSize() {
		return 10;
	}
	@Override
	public int getTileSize() {
		return 32;
	}

	@Override
	protected boolean checkGameOver2() {

		return false;
	}
	
	

}
