package fgp.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

import fgp.engine.GameEngine;
import fgp.engine.Layer;
import fgp.engine.util.AdaptiveTimerThread;

/**
 * @author Mr. Hapke
 */
public class FrameManager extends ComponentAdapter {
	private static int DEBUG_I = 0;

	private static final int MAX_TO_KEEP = 10;
	private int width;
	private int height;
	private AdaptiveTimerThread renderWorker;
	private BufferedImage current;

	private final AbstractQueue<BufferedImage> readyFrames = new LinkedTransferQueue<BufferedImage>();
	private final AbstractQueue<BufferedImage> availableFrames = new LinkedTransferQueue<BufferedImage>();
	private final Map<BufferedImage, Graphics> graphicsStorage = new HashMap<>();
	private GameEngine game;

	public FrameManager(GameEngine game, int width, int height) {
		this.game = game;
		this.width = width;
		this.height = height;

	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void start() {
		if (renderWorker == null || renderWorker.isKill()) {
			renderWorker = new AdaptiveTimerThread("RenderWorker", game.getTargetFps()) {
				@Override
				protected void doWork() {
					renderNextFrame();
				}
			};
			renderWorker.start();
		}
	}

	@Override
	public void componentResized(ComponentEvent ce) {
		readyFrames.clear();
		availableFrames.clear();
		Component component = ce.getComponent();
		setSize(component.getWidth(), component.getHeight());
	}

	public Image getCurrent() {
		BufferedImage target = null;
		BufferedImage oldCurrent = current;

		while (readyFrames.size() > 0) {
			if (target != null) {
				if (readyFrames.size() > 3)
					graphicsStorage.remove(target);
				else
					availableFrames.add(target);
			}
			target = readyFrames.poll();
		}
		if (target != null) {
			current = target;
			if (oldCurrent != null)
				availableFrames.add(oldCurrent);
		}
		return current;
	}

	public BufferedImage createFrame() {
		BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics gfx = target.createGraphics();

		graphicsStorage.put(target, gfx);
		gfx.setColor(Color.white);
		gfx.fillRect(0, 0, width, height);

		return target;
	}

	protected void renderNextFrame() {
		BufferedImage nextFrame = null;
		Graphics gfx = null;

		if (availableFrames.size() > 0) {
			nextFrame = availableFrames.poll();

			while (availableFrames.size() > MAX_TO_KEEP) {
				Image remove = availableFrames.poll();
				if (remove != null)
					graphicsStorage.remove(remove);
			}
		}

		if (nextFrame == null) {
			nextFrame = createFrame();
		}
		gfx = getGraphics(nextFrame);
		Color gameBack = game.getDefaultBack();
		Color gameFore = game.getDefaultFore();
		Color gameShadow = game.getDefaultShadow();
		gfx.setColor(gameBack);

		gfx.fillRect(0, 0, width, height);
		int startX = game.convertXGridToPixel(0);
		int startY = game.convertYGridToPixel(0);
		int endX = game.convertXGridToPixel(game.getXViewSize());
		int endY = game.convertYGridToPixel(game.getYViewSize());
		Rectangle bounds = new Rectangle(startX, startY, endX - startX, endY - startY);

		if (game != null) {
			game.advance();
			List<Layer> layers = game.getLayers();
			for (Layer l : layers) {
				l.createMap();
				l.paint(gfx, bounds);
			}
		}

		int w = game.convertXGridToPixel(game.getXViewSize());
		int yBottom = game.convertYGridToPixel(game.getYViewSize());
		int yTop = game.convertYGridToPixel(0);

		gfx.setColor(DEBUG_BACKGROUND);
		if (game.isDebugEnabled()) {
			gfx.fillRect(0, yBottom, w, GamePanel.DEBUG_HEIGHT);
			gfx.setColor(Color.BLACK);
			gfx.drawString("Frame: " + DEBUG_I++, 4, yBottom + 16);
		}
		gfx.setColor(gameShadow);
		gfx.drawLine(0, yTop, w, yTop);
		gfx.drawLine(0, yBottom, w, yBottom);
		readyFrames.add(nextFrame);
	}

	public Graphics getGraphics(BufferedImage nextFrame) {
		Graphics gfx = graphicsStorage.get(nextFrame);
		if (gfx == null) {
			nextFrame.createGraphics();
			graphicsStorage.put(nextFrame, gfx);
		}
		return gfx;
	}

	private static final Color DEBUG_BACKGROUND = new Color(235, 255, 235);

	public String getDebugInfo() {
		return "Frames Buffered: " + readyFrames.size() + "\nFrames Available: " + availableFrames.size()
				+ "\n   Gfx Available: " + graphicsStorage.size();
	}
}
