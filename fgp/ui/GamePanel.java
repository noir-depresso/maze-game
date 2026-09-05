package fgp.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.text.NumberFormat;

import javax.swing.JPanel;

import fgp.engine.GameEngine;
import fgp.engine.util.AdaptiveTimerThread;

/**
 * @author Mr. Hapke
 *
 */
public class GamePanel extends JPanel {
	private static final long serialVersionUID = -253320674008631948L;
	public static final int DEBUG_HEIGHT = 100;

	private FrameManager manager;
	private GameEngine game;
	private AdaptiveTimerThread repaintInvoker;
	private NumberFormat nf;

	public GamePanel(GameEngine game) {
		this.game = game;
		int w = Math.max(10, getWidth());
		int h = Math.max(10, getHeight());
		manager = new FrameManager(game, w, h);
		addComponentListener(manager);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		if (repaintInvoker == null) {
			repaintInvoker = new AdaptiveTimerThread("RepaintInvoker", game.getTargetFps()) {
				@Override
				protected void doWork() {
					repaint();
				}
			};
			repaintInvoker.start();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		double fps = repaintInvoker.getFps();
		Image i = manager.getCurrent();
		g.drawImage(i, 0, 0, null);
		g.setColor(Color.black);
		if (game.isDebugEnabled()) {
			int h = game.convertYGridToPixel(game.getYViewSize());
			g.drawString("FPS: " + nf.format(fps), 4, h + 32);
			g.drawString(manager.getDebugInfo(), 4, h + 48);
			// TODO #8 OPTIONAL: If you'd like to display something at the bottom of the
			// window, it goes here.
			 //g.drawString("", 4, h + 64);
			// g.drawString("", 4, h + 80);
		}
	}

	public FrameManager getManager() {
		return manager;
	}
}
