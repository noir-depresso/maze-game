package fgp.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;
import java.util.stream.Stream;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.CollectionList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import fgp.engine.Transaction.Mode;
import fgp.engine.bodies.Body;
import fgp.engine.bodies.IBodyPart;
import fgp.engine.inputs.KeyNotification;
import fgp.engine.inputs.Keyboarder;
import fgp.engine.inputs.MouseNotification;
import fgp.engine.inputs.Mouser;
import fgp.engine.inputs.MouserEvent;
import fgp.engine.inputs.Notification;
import fgp.engine.util.Helpers;

/**
 * @author Mr. Hapke
 */
public abstract class Layer implements ISupportsImage {
	private static final int MISSING_IMAGE_THICKNESS = 3;
	private static final BasicStroke MISSING_IMAGE_STROKE = new BasicStroke(MISSING_IMAGE_THICKNESS);
	public List<IBodyPart>[][] map;
	private final EventList<Body<?>> bodyOrigins = new BasicEventList<>();
	private final List<Body<?>> allBodies = GlazedLists.threadSafeList(bodyOrigins);
	private final SortedList<Body<?>> bodyByZ = new SortedList<Body<?>>(bodyOrigins, new Comparator<Body<?>>() {
		@Override
		public int compare(Body<?> a, Body<?> b) {
			int zComparison = b.getZIndex() - a.getZIndex();
			if (zComparison != 0) {
				return zComparison;
			} else {
				return b.toString().compareTo(a.toString());
			}
		}
	});
	private final CollectionList<Body<?>, IBodyPart> bodiesExpanded = new CollectionList<>(bodyByZ,
			(Body<?> b) -> b.getSubItems());
	private final List<IBodyPart> allParts = GlazedLists.threadSafeList(bodiesExpanded);
	private final Set<Keyboarder> keyboarders = new HashSet<>();
	private final Set<Mouser> mousers = new HashSet<>();
	protected final GameEngine game = GameEngine.getInstance();
	protected int advanceTick = 0;

	protected final ImageTracker imgTracker = new ImageTracker(getClass());

	private AbstractQueue<Transaction<Body<?>>> transactions = new ConcurrentLinkedQueue<>();
	@SuppressWarnings("rawtypes")
	private Queue<Notification> notifications = new LinkedBlockingQueue<>();

	protected abstract void changeLevel(int level);

	protected boolean disabledDuring(GameMode gameMode) {
		if (gameMode == GameMode.Play)
			return false;
		else
			return true;
	}

	/**
	 * TODO #4 Create the map, representing where all of the bodies are, using their
	 * location.x and location.y
	 */
	public final void createMap() {
		int xSize = game.getXViewSize();
		int ySize = game.getYViewSize();

		@SuppressWarnings("unchecked")
		List<IBodyPart>[][] nextMap = new LinkedList[xSize][ySize];

		for (IBodyPart part : allParts) {
			int x = part.getX();
			int y = part.getY();

			if (Helpers.inside(0, xSize, x) && Helpers.inside(0, ySize, y)) {
//	    nextMap[x][y] = body;
				List<IBodyPart> list = nextMap[x][y];
				if (list == null) {
					list = new LinkedList<IBodyPart>();
					nextMap[x][y] = list;
				}
				list.add(part);
			}
		}

		this.map = nextMap;
	}

	public final EventList<IBodyPart> createBodyList() {
		EventList<IBodyPart> result = new BasicEventList<IBodyPart>(bodiesExpanded.getPublisher(),
				bodiesExpanded.getReadWriteLock());
		return result;
	}

	public final void paint(Graphics gfx, Rectangle bounds) {
		if (map == null)
			return;

		gfx.setClip(bounds);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				List<IBodyPart> partsList = map[i][j];
				if (partsList != null) {
					for (IBodyPart part : partsList) {
						paintCell(gfx, i, j, part);
					}
				}
			}
		}
		
//		int boundsX = 1;
//		int boundsY = 1;
//		for (int i = 0 + boundsX; i < map.length - boundsX; i++) {
//			for (int j = 0 + boundsY; j < map[i].length - boundsY; j++) {
//				List<IBodyPart> partsList = map[i][j];
//				if (partsList != null) {
//					for (IBodyPart part : partsList) {
//						paintCell(gfx, i, j, part);
//					}
//				}
//			}
//		}
//		

		paint2(gfx, bounds);
	}

	/**
	 * you can override this to add extra painting special code
	 */
	protected void paint2(Graphics gfx, Rectangle bounds) {
	}

	/**
	 * Paint the cell at the coordinate (i,j). If there's a Body there, you're given
	 * it as well.
	 */
	protected void paintCell(Graphics gfx, int i, int j, ISupportsImage b) {
		if (b != null) {
			int x = game.convertXGridToPixel(i);
			int y = game.convertYGridToPixel(j);
			int tileSize = game.getTileSize();

			Image img = null;

			Graphics2D g2 = null;
			boolean clipping = gfx instanceof Graphics2D;
			Rectangle bounds = new Rectangle(x, y, tileSize, tileSize);
			Shape oldClip = null;
			if (clipping) {
				oldClip = gfx.getClip();
				gfx.setClip(bounds);
			}

			Sprite sprite = game.getSprite(b);
			ImageTracker bTracker = b.getImageTracker();
			if (sprite != null) {
				if (sprite.isAnimated()) {
					bTracker.advanceFrameTime(1, sprite.getTotalDuration());
				}
				img = sprite.getFrame(bTracker.getFrameTime());
			}
			if (bTracker.shouldDrawSprite()) {
				if (img != null) {

					gfx.drawImage(img, x, y, null);
				} else {
					// can't find the image, so draw a box to represent that something's there, with
					// a colour based on the ID so we can tell the difference between types

					Stroke oldStroke = null;

					if (clipping) {
						g2 = (Graphics2D) gfx;
						oldStroke = g2.getStroke();
						g2.setStroke(MISSING_IMAGE_STROKE);
					}

					String bodyClassname = KeyUtil.getName(b);
					// use the list of the Body types to distribute evenly.
					List<String> types = game.getBodyTypeNames();
					int a = types.indexOf(bodyClassname);
					int qty = types.size();
					float hue = (float) a / qty;

					Color c = Color.getHSBColor(hue, 1.0f, 0.8f);
					Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 40);
					gfx.setColor(Color.RED);
					gfx.fillRect(x, y, tileSize, tileSize);

					gfx.setColor(c);
					gfx.drawRect(x + MISSING_IMAGE_THICKNESS / 2 + 1, y + MISSING_IMAGE_THICKNESS / 2 + 1,
							tileSize - MISSING_IMAGE_THICKNESS - 1, tileSize - MISSING_IMAGE_THICKNESS - 1);

					Color fore = game.getDefaultFore();
					gfx.setColor(fore);

					int down = tileSize / 2 + 1;
					int fontSize = gfx.getFont().getSize();

					String bodyShortClassname = KeyUtil.getShortName(b);
					gfx.drawString(bodyShortClassname, x + MISSING_IMAGE_THICKNESS, y + down);
					down += fontSize;
					gfx.drawString("" + bTracker.getSpriteIndex(), x + MISSING_IMAGE_THICKNESS, y + down);

					if (oldStroke != null && clipping)
						g2.setStroke(oldStroke);
				}
			}

			b.paint(gfx, bounds);

			if (clipping) {
				gfx.setClip(oldClip);
			}
		}
	}

	/**
	 * TODO #5 search the list of Bodies, and see if you can find one at that (x,y)
	 * coordinate. If you find one, return it.
	 */
	public List<IBodyPart> searchParts(int i, int j) {
		List<IBodyPart> result = new ArrayList<IBodyPart>();
		for (IBodyPart bp : allParts) {
			if (bp.getX() == i && bp.getY() == j) {
				result.add(bp);
			}
		}
		return result;
	}

	public <T extends IBodyPart> List<T> searchParts(Class<T> cls) {
		List<T> found = new ArrayList<>();
		for (IBodyPart part : allParts) {
			if (cls.isInstance(part)) {
				@SuppressWarnings("unchecked")
				T t = (T) part;
				found.add(t);
			}
		}
		return found;
	}

	public <T extends Body<?>> List<T> searchBodies(Class<T> cls) {
		List<T> found = new ArrayList<>();
		for (Body<?> body : allBodies) {
			if (cls.isInstance(body)) {
				@SuppressWarnings("unchecked")
				T t = (T) body;
				found.add(t);
			}
		}
		return found;
	}

	/**
	 * This is final so you don't accidentally override it, and add/removal of the
	 * bodies, as well as kill the keyboard/mouse. If you'd like to do something
	 * after this completes, then override advance2(), which is called at the end of
	 * this method.
	 */
	@SuppressWarnings("rawtypes")
	public final void advance() {
		while (notifications.size() > 0) {
			Notification n = notifications.poll();
			if (n instanceof KeyNotification) {
				KeyNotification kn = (KeyNotification) n;

				for (Keyboarder kl : keyboarders) {
					int value = kn.value;
					int modifiers = kn.modifiers;
					switch (n.type) {
					case KeyNotification.KEY_DOWN:
						kl.keyDown(value, modifiers);
						break;
					case KeyNotification.KEY_UP:
						kl.keyUp(value, modifiers);
						break;
					}
				}
			} else if (n instanceof MouseNotification) {
				MouseNotification mn = (MouseNotification) n;
				for (Mouser ml : mousers) {
					MouserEvent value = mn.value;
					switch (mn.type) {
					case MouseNotification.MOUSE_DOWN:
						ml.mouseDown(value);
						break;
					case MouseNotification.MOUSE_UP:
						ml.mouseUp(value);
						break;
					case MouseNotification.MOUSE_MOVE:
						ml.mouseMove(value);
						break;
					case MouseNotification.MOUSE_ENTER:
						ml.mouseEnter(value);
						break;
					case MouseNotification.MOUSE_EXIT:
						ml.mouseExit(value);
						break;
					}
				}
			}
		}
		Transaction<Body<?>> t;
		while ((t = transactions.poll()) != null) {
			Body b = t.value;
			switch (t.mode) {
			case Add:
				bodyOrigins.add(b);
				if (b instanceof Keyboarder) {
					Keyboarder kl = (Keyboarder) b;
					addKeyboarder(kl);
				}
				if (b instanceof Mouser) {
					Mouser ml = (Mouser) b;
					addMouser(ml);
				}
				break;
			}
		}

		for (Iterator iterator = bodyOrigins.iterator(); iterator.hasNext();) {
			Body b = (Body) iterator.next();
			if (b.isMarkedForRemoval()) {
				iterator.remove();
				b.setInGame(false);
				if (b instanceof Keyboarder) {
					Keyboarder kl = (Keyboarder) b;
					removeKeyboarder(kl);
				}
				if (b instanceof Mouser) {
					Mouser ml = (Mouser) b;
					removeMouser(ml);
				}
			}
			b.clearRemovalMark();
		}

		int delay = getAdvanceDelay();
		if (delay >= 0) {
			advanceTick++;
			if (advanceTick > delay) {
				advanceTick = 0;
				advance2();
			}
		}
		for (Body body : bodyOrigins) {
			body.advance();
		}
	}

	protected int getAdvanceDelay() {
		return 0;
	}

	protected void advance2() {
	}

	protected void addKeyboarder(Keyboarder kl) {
		if (kl != null)
			keyboarders.add(kl);
	}

	protected void addMouser(Mouser ml) {
		if (ml != null)
			mousers.add(ml);
	}

	protected void removeKeyboarder(Keyboarder kl) {
		if (kl != null)
			keyboarders.remove(kl);
	}

	protected void removeMouser(Mouser ml) {
		if (ml != null)
			mousers.remove(ml);
	}

	public Set<Body<?>> clearBodies() {
		Set<Body<?>> result = new HashSet<Body<?>>();
		result.addAll(bodyOrigins);
		for (Body<?> b : bodyOrigins) {
			b.markForRemoval();
		}
		return result;
	}

	public Set<Body<?>> removeBodies(Predicate<Body<?>> p) {
		Set<Body<?>> result = new HashSet<Body<?>>();
		for (Body<?> b : bodyOrigins) {
			if (p.test(b)) {
				b.markForRemoval();
				result.add(b);
			}
		}
		return result;
	}

	public void removeBody(Body<?> b) {
		if (b != null) {
			b.markForRemoval();
		}
	}

	public void addBody(Body<?> b) {
		if (b != null) {
			transactions.add(new Transaction<>(Mode.Add, b));
			b.setInGame(true);
		}
	}

	@SuppressWarnings("rawtypes")
	public void notify(Notification n) {
		if (n != null && !disabledDuring(game.getGameMode()))
			notifications.add(n);
	}

	public Stream<Body<?>> getBodiesStream() {
		return allBodies.stream();
	}

	public Stream<IBodyPart> getBodyItemsStream() {
		return allParts.stream();
	}

	public void gameModeChanged(GameMode gameMode) {
	}

	@Override
	public ImageTracker getImageTracker() {
		return imgTracker;
	}
}
