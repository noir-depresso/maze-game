package fgp.engine;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;

import org.reflections.Reflections;

import fgp.engine.LoadImage.ResizeMode;
import fgp.engine.bodies.Body;
import fgp.engine.bodies.IBodyPart;
import fgp.engine.inputs.Notification;
import fgp.engine.util.ImageCache;
import fgp.game.layers.BackdropLayer;
import fgp.game.layers.BackgroundLayer;
import fgp.game.layers.StatusLayer;
import fgp.ui.DisplayItem;
import fgp.ui.FrmDebug;
import fgp.ui.FrmLauncher;
import fgp.ui.HorizontalAlignment;

/**
 * TODO #1 extend this class. Put your class in the package fgp.game. Go read
 * the readme for instructions.
 *
 * @author Mr. Hapke
 */
public abstract class GameEngine {
	public enum OsDetected {
		//@formatter:off
		Mac,
		Windows,
		Default;
		//@formatter:on
	}

	private static GameEngine instance;
	protected int score = 0;
	protected int lives;
	public static int difficulty;
	private int currentLevel = 1;

	public static final int TOP_DISPLAY_ROW_HEIGHT = 20;
	private int topDisplayRows;
	private Map<HorizontalAlignment, List<DisplayItem<?>>> topDisplayItems = new HashMap<>();

	private final List<Layer> layers = new ArrayList<>();
//	protected final Map<Integer, Layer> layerMap = new HashMap<>();
	protected final Map<String, Sprite> spriteMap = new HashMap<>();
	protected final Map<String, Integer> spriteCounter = new HashMap<>();
	protected final List<String> bodyTypeNames = new ArrayList<>();
	private GameMode gameMode = GameMode.Play;
	private GameMode nextGameMode = GameMode.Play;
	private boolean debugEnabled = false;
	private ImageCache imgLoader;
	protected int advanceTick = 0;
	public final OsDetected os;

	private final Point offset = new Point(10, 10);
	private List<Waiter> waitingEvents = new ArrayList<>();

	// DARK/LIGHT MODE
	protected Color defaultBack = Color.WHITE;
	protected Color defaultFore = Color.BLACK;
	protected Color defaultShadow = Color.LIGHT_GRAY;

	private StatusLayer statusLayer;

	public static GameEngine getInstance() {
		return instance;
	}

	public GameEngine() throws Exception {
		if (getInstance() != null) {
			throw new Exception("Cannot create GameEngine twice");
		}
		instance = this;
		imgLoader = ImageCache.getInstance();

		String osName = System.getProperty("os.name");
		OsDetected os = OsDetected.Default;
		if (osName != null && osName.length() > 0) {
			String lowerCase = osName.toLowerCase();
			if (lowerCase.startsWith("mac")) {
				os = OsDetected.Mac;
			} else if (lowerCase.startsWith("windows")) {
				os = OsDetected.Windows;
			}
		}
		this.os = os;
	}

	public void init() {
		if (shouldCreateBackdropLayer()) {
			layers.add(new BackdropLayer());
		}
		createLayers();
		if (shouldCreateStatusLayer()) {
			statusLayer = new StatusLayer();
			layers.add(statusLayer);
		}
		loadSprites();
		
		lives = 5-difficulty;
	}

	protected boolean shouldCreateBackdropLayer() {
		return true;
	}

	protected boolean shouldCreateStatusLayer() {
		return true;
	}

	/**
	 * How many FPS would you like?
	 */
	public abstract int getTargetFps();

	/**
	 * Will be placed in the Frame's Titlebar automatically
	 */
	public abstract String getGameTitle();

	/**
	 * How many levels does your game support?
	 */
	protected abstract int getLevels();

	public final void loadSprites() {
		Class<? extends GameEngine> thisClass = getClass();
		String thisPackage = thisClass.getPackageName();
		int i = thisPackage.indexOf('.');
		if (i >= 1) {
			thisPackage = thisPackage.substring(0, i);
		}
		Reflections reflections = new Reflections(thisPackage);

		Set<Class<?>> subBodyTypes = reflections.get(SubTypes.with(Body.class).asClass());
		for (Class<?> b : subBodyTypes) {
			String className = KeyUtil.convertClassToName(b);
			bodyTypeNames.add(className);
		}

		String folderName = getImagesFolder();
		if (folderName == null)
			return;
		bodyTypeNames.clear();
		Set<Class<?>> hasLoadImageTypes = reflections.get(TypesAnnotated.with(LoadImage.class).asClass());
		for (Class<?> c : hasLoadImageTypes) {
			LoadImage a = c.getAnnotation(LoadImage.class);
			String className = KeyUtil.convertClassToName(c);
			if (a != null) {
				String[] files = a.value();
				for (int j = 0, k = 0; j < files.length; j++) {
					String s = files[j];
					boolean found = addSprite(className, k, folderName, s, a.resizeMode(), a.showErrors());
					if (a.keepIndex() || found) {
						k++;
						spriteCounter.put(className, k);
					}
				}
			}
		}
	}

	protected abstract String getImagesFolder();

	/**
	 * Create the layers for your game. Strongly recommend putting them into a
	 * private field for yourself. Add them to the List called layers. They will be
	 * rendered in (first = bottom)-->(last = top) order. Strongly recommend adding
	 * the StatusLayer last. It will add Pause support to your game with the P-key.
	 */
	public abstract void createLayers();

	protected void addLayer(Layer l) {
		layers.add(l);
	}

	/**
	 * How many grid boxes horizontally?
	 */
	public abstract int getXViewSize();

	/**
	 * How many grid boxes vertically?
	 */
	public abstract int getYViewSize();

	/*-
	 * How many grid boxes horizontally in the world?
	 * By default, the same as the view.
	 */
	public int getXWorldSize() {
		return getXViewSize();
	}

	/*-
	 * How many grid boxes vertically in the world?
	 * By default, the same as the view.
	 */
	public int getYWorldSize() {
		return getYViewSize();
	}

	/**
	 * What size are your images? They must be square. ie: 16x16/32x32, etc.
	 */
	public abstract int getTileSize();

	public Sprite getSprite(ISupportsImage b) {
		Sprite sprite = spriteMap.get(b.getImageTracker().getSpriteKey());
		return sprite;
	}

	public void checkGameOver() {
		boolean over = checkGameOver2();
		if (over) {
			gameOver();
		}
	}

	protected abstract boolean checkGameOver2();

	public void gameOver() {
		setGameMode(GameMode.Over);
	}

	public void newGame(int diff) {
		difficulty = diff;
		score = 0;
		lives = 5-difficulty;
		changeLevel(1);
		nextGameMode = GameMode.Play;
	}

	public void restartLevel() {
		changeLevel(currentLevel);
	}

	public void nextLevel() {
		changeLevel(currentLevel + 1);
	}

	public void changeLevel(int level) {
		setOffset(0, 0);
		currentLevel = level;
		boolean over = false;
		if (currentLevel > getLevels()) {
			currentLevel = 0;
			setGameMode(GameMode.Over);
			over = true;
		}
		int i = 0;
		while (i < layers.size()) {
			Layer l = layers.get(i);
			l.changeLevel(level);
			i++;
		}
		if (!over) {
			setGameMode(GameMode.Play);
		}
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public int convertXGridToPixel(int x) {
		return x * getTileSize();
	}

	public int convertYGridToPixel(int y) {
		return y * getTileSize() + (TOP_DISPLAY_ROW_HEIGHT * topDisplayRows);
	}

	public int convertXPixelToGrid(int x) {
		return x / getTileSize();
	}

	public int convertYPixelToGrid(int y) {
		return (y - (TOP_DISPLAY_ROW_HEIGHT * topDisplayRows)) / getTileSize();
	}

	public void advance() {
		gameMode = nextGameMode;
		int delay = getAdvanceDelay();
		if (delay >= 0) {
			advanceTick++;
			if (advanceTick > delay) {
				advanceTick = 0;
				advance2();
			}
		}
		for (Layer layer : layers) {
			if (!layer.disabledDuring(gameMode))
				layer.advance();
		}

		int i = 0;
		while (i < waitingEvents.size()) {
			Waiter w = waitingEvents.get(i);
			boolean done = w.frame();
			if (done) {
				waitingEvents.remove(w);
			} else {
				i++;
			}
		}
	}

	public void addWaiter(Waiter waiter) {
		waitingEvents.add(waiter);
	}

	protected int getAdvanceDelay() {
		return 0;
	}

	protected void advance2() {
	}

	/**
	 * TODO #6 Search through your layers, in order from top to bottom, and return
	 * all of the Bodies that you find at that (x,y) location
	 */
	public List<IBodyPart> search(int x, int y) {
		List<IBodyPart> result = new ArrayList<IBodyPart>();
		for (Layer l : layers) {
			List<IBodyPart> searched = l.searchParts(x,y);
			result.addAll(searched);
		}
		return result;
	}

	public <T extends IBodyPart> List<T> searchParts(Class<T> cls) {
		List<T> found = new ArrayList<>();
		for (Layer l : layers) {
			List<T> fromLayer = l.searchParts(cls);
			found.addAll(fromLayer);
		}
		return found;
	}

	public <T extends Body<?>> List<T> searchBodies(Class<T> cls) {
		List<T> found = new ArrayList<>();
		for (Layer l : layers) {
			List<T> fromLayer = l.searchBodies(cls);
			found.addAll(fromLayer);
		}
		return found;
	}

	public List<IBodyPart> searchParts(Predicate<IBodyPart> p) {
		return findParts(p).collect(Collectors.toList());
	}

	public Stream<IBodyPart> findParts(Predicate<IBodyPart> p) {
		return layers.stream().flatMap(l -> l.getBodyItemsStream()).filter(p);
	}

	public List<Body<?>> searchBodies(Predicate<Body<?>> p) {
		return findBodies(p).collect(Collectors.toList());
	}

	public Stream<Body<?>> findBodies(Predicate<Body<?>> p) {
		return layers.stream().flatMap(l -> l.getBodiesStream()).filter(p);
	}

	protected boolean addSprite(String id, int i, String folder, String filename, ResizeMode resize,
			boolean showErrors) {
		return addSprite(KeyUtil.createKey(id, i), folder, filename, resize, showErrors);
	}

	private boolean addSprite(String key, String folder, String filename, ResizeMode resize, boolean showErrors) {
		if (!spriteMap.containsKey(key)) {
			Sprite img = imgLoader.getImage(folder, filename, resize, showErrors);
			if (img != null) {
				spriteMap.put(key, img);
				return true;
			}
		}
		return false;
	}

	public void remove(Collection<Body<?>> c) {
		for (Body<?> b : c) {
			remove(b);
		}
	}

	public void remove(Body<?> b) {
		for (Layer layer : layers) {
			layer.removeBody(b);
		}
	}

	@SuppressWarnings("rawtypes")
	public void notify(Notification notification) {
		for (Layer layer : layers) {
			layer.notify(notification);
		}
	}

	public int getScore() {
		return score;
	}

	public void addScore(int points) {
		score += points;
	}

	public int getLives() {
		return lives;
	}

	public void addLife() {
		lives++;
	}
	
	public void lifeLost() {
		lives--;
		if (lives > 0) {
			restartLevel();
			
		} else {
			lives = 5-difficulty;
			gameOver();
		}
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public void setGameMode(GameMode gameMode) {
		this.nextGameMode = gameMode;
		for (Layer layer : layers) {
			layer.gameModeChanged(gameMode);
		}
	}

	public void setDebugEnabled(boolean enabled) {
		this.debugEnabled = enabled;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public int getXOffset() {
		return offset.x;
	}

	public int getYOffset() {
		return offset.y;
	}

	public void moveOffset(int dx, int dy) {
		offset.x += dx;
		offset.y += dy;
	}

	public void setOffset(int x, int y) {
		offset.x = x;
		offset.y = y;
	}
	public void addOffset(int x, int y) {
		offset.x +=x;
		offset.y +=y;
	}

	public List<String> getBodyTypeNames() {
		return bodyTypeNames;
	}

	public void addDisplayItem(HorizontalAlignment align, DisplayItem<?> value) {
		List<DisplayItem<?>> list = getDisplayItemList(align);
		list.add(value);
		int qty = list.size();
		if (qty > topDisplayRows)
			topDisplayRows = qty;
	}

	public List<DisplayItem<?>> getDisplayItemList(HorizontalAlignment align) {
		List<DisplayItem<?>> list = topDisplayItems.get(align);
		if (list == null) {
			list = new ArrayList<>();
			topDisplayItems.put(align, list);
		}
		return list;
	}

	public void setDarkMode(boolean dark) {
		if (dark) {
			defaultBack = Color.BLACK;
			defaultFore = Color.WHITE;
			defaultShadow = Color.GRAY;
		} else {
			defaultBack = Color.WHITE;
			defaultFore = Color.BLACK;
			defaultShadow = Color.LIGHT_GRAY;
		}
	}

	public Color getDefaultBack() {
		return defaultBack;
	}

	public Color getDefaultFore() {
		return defaultFore;
	}

	public Color getDefaultShadow() {
		return defaultShadow;
	}

	public JFrame createDebugFrame() {
		return new FrmDebug(this);
	}

	public int getSpriteCount(Class<?> cls) {
		String clsName = KeyUtil.convertClassToName(cls);
		return spriteCounter.getOrDefault(clsName, 0);
	}
}
