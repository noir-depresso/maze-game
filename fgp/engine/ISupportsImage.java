package fgp.engine;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Nathan Hapke
 */
public interface ISupportsImage {

	public ImageTracker getImageTracker();

	public void paint(Graphics gfx, Rectangle bounds);
}