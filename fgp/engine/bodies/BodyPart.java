package fgp.engine.bodies;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import fgp.engine.Direction;
import fgp.engine.ISupportsImage;
import fgp.engine.ImageTracker;

/**
 * @author Nathan Hapke
 */
public class BodyPart implements IBodyPart, ISupportsImage {

	protected final ImageTracker imgTracker = new ImageTracker(getClass());
	protected final Body<?> parent;
	private Point delta;

	public BodyPart(Body<?> parent, int dx, int dy) {
		this.parent = parent;
		this.delta = new Point(dx, dy);
	}

	@Override
	public ImageTracker getImageTracker() {
		return imgTracker;
	}

	@Override
	public int getDeltaX() {
		return delta.x;
	}

	@Override
	public int getDeltaY() {
		return delta.y;
	}

	@Override
	public void setDeltas(int dx, int dy) {
		delta.x = dx;
		delta.y = dy;
	}

	@Override
	public final int getX() {
		return parent.getX() + getDeltaX();
	}

	@Override
	public final int getY() {
		return parent.getY() + getDeltaY();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Body.getPrettyCoordinate(getX(), getY());
	}

	@Override
	public Body<?> getParent() {
		return parent;
	}

	@Override
	public void paint(Graphics gfx, Rectangle bounds) {
		if (parent.thisPartShowsHealth(this)) {
			int max = parent.getHealthMax();
			int current = parent.getHealthCurrent();
			boolean shouldShowFull = parent.shouldShowFullHealthBar();
			int barWidth = parent.getHealthBarWidth();
			Direction location = parent.getHealthLocation();
			boolean showBorder = parent.shouldShowHealthBorder();

			drawProgressBar(gfx, bounds, max, current, shouldShowFull, barWidth, location, showBorder);
		}
	}

	protected static final int PROGRESS_BAR_MARGIN = 1;

	protected void drawProgressBar(Graphics gfx, Rectangle bounds, int max, int current, boolean shouldShowFull,
			int barWidth, Direction location, boolean showBorder) {
		double value = Math.min(max, current);
		double pct = value / max;
		if (shouldShowFull || pct < 1.0) {
			int full = barWidth;

			int w = Math.max(PROGRESS_BAR_MARGIN, (int) (pct * full));
			int h = 6;
			int x, y;
			if (location == null) {
				location = Direction.UpLeft;
			}
			if (location.dx == -1) {
				// left
				x = bounds.x + PROGRESS_BAR_MARGIN;
			} else if (location.dx == 0) {
				// centre
				x = bounds.x + (bounds.width - full) / 2;
			} else {
				// right
				x = bounds.x + bounds.width - full - PROGRESS_BAR_MARGIN;
			}
			if (location.dy == -1) {
				// top
				y = bounds.y + PROGRESS_BAR_MARGIN;
			} else if (location.dy == 0) {
				// centre
				y = bounds.y + (bounds.height - h) / 2;
			} else {
				// bottom
				y = bounds.y + bounds.height - h - PROGRESS_BAR_MARGIN;
			}

			Color c;
			if (pct <= 0.3)
				c = Color.RED;
			else if (pct <= 0.67)
				c = Color.YELLOW;
			else
				c = Color.GREEN;
			gfx.setColor(c);
			gfx.fillRect(x, y, w, h);
			if (showBorder) {
				gfx.setColor(Color.BLACK);
				gfx.drawRect(x, y, full, h);
			}
		}
	}
}