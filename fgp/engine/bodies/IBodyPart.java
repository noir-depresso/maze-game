package fgp.engine.bodies;

import fgp.engine.ISupportsImage;

/**
 * @author Nathan Hapke
 */
public interface IBodyPart extends ISupportsImage {

	public Body<?> getParent();

	public int getDeltaX();

	public int getDeltaY();

	public void setDeltas(int dx, int dy);

	public int getX();

	public int getY();

	default public int getZIndex() {
		return getParent().getZIndex();
	}

	default public void markForRemoval() {
		getParent().markForRemoval();
	}

	default public boolean isInGame() {
		return getParent().isInGame();
	}

	default public boolean move(int dx, int dy) {
		return getParent().move(dx, dy);
	}
}