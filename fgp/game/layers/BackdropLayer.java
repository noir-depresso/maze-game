package fgp.game.layers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import fgp.engine.GameMode;
import fgp.engine.Layer;
import fgp.engine.LoadImage;
import fgp.engine.LoadImage.ResizeMode;
import fgp.engine.Sprite;

/**
 * This file attempts to load an image called bg, then if it can, it'll draw it
 * in the back. Setting keepIndex to false means just load them in order, and
 * ignore the ones that the files don't exist.
 * 
 * @author Mr. Hapke
 */
//@LoadImage(value = { "bg.jpg", "sky.png", "bg.gif" }, keepIndex = false, resizeMode = ResizeMode.Off, showErrors = false)
public class BackdropLayer extends Layer {

	@Override
	protected void changeLevel(int level) {

	}

	@Override
	protected boolean disabledDuring(GameMode gameMode) {
		return false;
	}

	@Override
	public void paint2(Graphics gfx, Rectangle bounds) {
		Sprite s = game.getSprite(this);
		if (s != null) {
			Image f = s.getFrame(0);

			if (f != null) {
				gfx.drawImage(f, 0, 0, null);
			}
		}
	}
}
