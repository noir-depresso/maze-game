package fgp.engine.bodies;

import java.awt.Point;
import java.util.List;

import ca.odell.glazedlists.EventList;
import fgp.engine.Direction;
import fgp.engine.GameEngine;
import fgp.engine.GameMode;
import fgp.engine.Layer;
import fgp.game.bodies.Door;
import fgp.game.bodies.Grass;
import fgp.game.bodies.Orb;
import fgp.game.bodies.Runner;
import fgp.game.classes.Chaser;
import fgp.ui.FrmShop;

/**
 * To tell the engine which sprite you want, add the @LoadImages annotation, and
 * specify the filename
 * 
 * @author Mr. Hapke
 */
public abstract class Body<T extends IBodyPart> {
	private final Point location;
	private boolean remove = false;
	// managed by Layer
	private boolean inGame = false;

	protected final GameEngine game = GameEngine.getInstance();
	protected final Layer layer;
	protected int tick = 0;
	protected final EventList<IBodyPart> subItems;
	protected final int zIndex;

	/**
	 * Package access so that you only inherit from BodySimple or BodyMultiple
	 */
	Body(Layer l, int x, int y, int zIndex) {
		this.layer = l;
		location = new Point(x, y);
		this.zIndex = zIndex;
		subItems = layer.createBodyList();
		createSubItems(subItems);
	}
	

	public abstract void createSubItems(List<IBodyPart> list);

	public final EventList<IBodyPart> getSubItems() {
		return subItems;
	}

	public int getX() {
		return location.x - game.getXOffset();
	}

	public int getY() {
		return location.y - game.getYOffset();
	}

	public int getZIndex() {
		return zIndex;
	}

	public int getWorldX() {
		return location.x;
	}

	public int getWorldY() {
		return location.y;
	}

	public final void advance() {
		int delay = getAdvanceDelay();
		if (delay >= 0) {
			tick++;
			if (tick > delay) {
				tick = 0;
				advance2();
			}
		}
	}

	public void advance2() {
	}

	/**
	 * How many frames would you like to wait before advance2() is called? 0 means
	 * every frame, -1 means to not animate at all
	 */
	protected int getAdvanceDelay() {
		return -1;
	}

	/**
	 * Not recommended for general use. This bypasses the collision detection
	 * system.
	 */
	public void forceLocation(int x, int y) {
		location.x = x;
		location.y = y;
	}

	public void forceLocation(Direction d) {
		forceLocation(location.x + d.dx, location.y + d.dy);
	}

	public boolean move(Direction d) {
		return move(d.dx, d.dy);
	}

	public boolean move(int dx, int dy) {
		boolean allowed = true;
		EventList<IBodyPart> myParts = getSubItems();

		outer:
		for (IBodyPart self : myParts) {
			int fromX = self.getX();
			int fromY = self.getY();
			
			int targetX = fromX + dx;
			
			int targetY = fromY + dy;
			List<IBodyPart> ahead = game.search(targetX, targetY);
			if (ahead != null) {
				for (IBodyPart bp : ahead) {
					boolean c = collision(self, bp, dx, dy);
					if (!c) {
						// hit something we aren't allowed to go through
						allowed = false;
						break outer;
					}
				}
			}
		}
		if (allowed) {
			location.x += dx;
			location.y += dy;
		}

		return allowed;
	}

	/**
	 * Gets called before you move() into a location that has a Body there already.
	 * In this project, we'll use Look-Ahead collision detection. The Body that is
	 * moving is responsible for not ruining the playability of your game.
	 * 
	 * @return False if the move should be blocked, True if it should be allowed
	 */
	protected abstract boolean collision(IBodyPart myPart, IBodyPart otherPart, int dx, int dy);
	
	//only for Runner lol
	

	public boolean isMarkedForRemoval() {
		return remove;
	}

	public void markForRemoval() {
		this.remove = true;
	}

	public void clearRemovalMark() {
		this.remove = false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + getPrettyLocation();
	}

	public String getPrettyLocation() {
		return getPrettyCoordinate(location.x, location.y);
	}

	public static String getPrettyCoordinate(int x, int y) {
		return "(" + x + "," + y + ")";
	}

	// Health Bar

	protected int hp = getHealthMax();

	public int getHealthMax() {
		return 1;
	}

	public int getHealthCurrent() {
		return hp;
	}

	/**
	 * @return true if runs out of life
	 */
	public boolean hit() {
		hp--;
		if (hp <= 0) {
			markForRemoval();
			return true;
		} else {
			return false;
		}
	}

	public Direction getHealthLocation() {
		return Direction.UpLeft;
	}

	public int getHealthBarWidth() {
		return 12;
	}

	public boolean shouldShowFullHealthBar() {
		if (getHealthMax() == 1)
			return false;
		else
			return true;
	}

	public boolean shouldShowHealthBorder() {
		return true;
	}

	/**
	 * By default, the first body part shows the health bar
	 */
	public boolean thisPartShowsHealth(IBodyPart bodyPart) {
		return bodyPart == getSubItems().get(0);
	}

	public void setInGame(boolean b) {
		this.inGame = b;
	}

	public boolean isInGame() {
		return inGame;
	}
}
