package fgp.engine;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Nathan Hapke
 */
public final class ImageTracker {

	private Class<?> targetClass;
	public int spriteIndex = 0;
	private int frameTime = 0;

	public ImageTracker(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public int getSpriteIndex() {
		return spriteIndex;
	}

	public void setSpriteIndex(int spriteIndex) {
		this.spriteIndex = spriteIndex;
	}

	public boolean shouldDrawSprite() {
		return true;
	}

	public double getFrameTime() {
		return frameTime;
	}

	public void advanceFrameTime(double x, int max) {
		frameTime += x;
		while (frameTime > max) {
			frameTime -= max;
		}
	}

	public String getSpriteKey() {
		String shortName = KeyUtil.convertClassToName(targetClass);
		return KeyUtil.createKey(shortName, spriteIndex);
	}

	public void paint(Graphics gfx, Rectangle bounds) {
	}
}
