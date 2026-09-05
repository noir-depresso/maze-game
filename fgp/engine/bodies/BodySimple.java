package fgp.engine.bodies;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import fgp.engine.ImageTracker;
import fgp.engine.Layer;

/**
 * @author Nathan Hapke
 */
public abstract class BodySimple extends Body<BodySimple> implements IBodyPart {
	protected final ImageTracker imgTracker = new ImageTracker(getClass());
	private BodyPart delegate;

	public BodySimple(Layer l, int x, int y, int zIndex) {
		super(l, x, y, zIndex);
		delegate = new BodyPart(this, 0, 0);
	}

	@Override
	public void createSubItems(List<IBodyPart> list) {
		list.add(this);
	}

	@Override
	public int getDeltaX() {
		return delegate.getDeltaX();
	}

	@Override
	public int getDeltaY() {
		return delegate.getDeltaY();
	}

	public void setDeltas(int dx, int dy) {
		delegate.setDeltas(dx, dy);
	}

	@Override
	public void paint(Graphics gfx, Rectangle bounds) {
		delegate.paint(gfx, bounds);
	}

	@Override
	public Body<?> getParent() {
		// For efficiency, don't need to let the delegate re-direct back to this.
		return this;
	}

	public boolean thisPartShowsHealth(IBodyPart bodyPart) {
		return bodyPart == delegate;
	}

	@Override
	public ImageTracker getImageTracker() {
		return imgTracker;
	}
}
