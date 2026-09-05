package fgp.game.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import fgp.engine.GameEngine;
import fgp.engine.GameMode;
import fgp.engine.Layer;
import fgp.engine.highscore.HighScore;
import fgp.engine.highscore.HighScoreManager;
import fgp.engine.inputs.Keyboarder;
import fgp.ui.DisplayItem;
import fgp.ui.HorizontalAlignment;

/**
 * @author Mr. Hapke
 */
public class StatusLayer extends Layer {

	private NumberFormat nf = NumberFormat.getInstance();

	private final ShadowedFont bannerFont = new ShadowedFont("Courier New", Font.BOLD, 36, 5);
	private final ShadowedFont messageFont = new ShadowedFont("Courier New", Font.PLAIN, 20, 4);

	private static final int BOX_HALF_HEIGHT = 55;
	private static final int BOX_HALF_WIDTH = 100;
	private int boxStep = 0;
	private final int boxSteps;
	private float boxColor = 0;

	private GameOverMode gameOverMode = GameOverMode.Wait;
	private boolean highScoresEnabled = true;
	private HighScoreManager highScoreManager;
	private String highScoreName = "";

	private final int xCen;
	private final int xRt;
	private final int yCen;

	private DisplayItem<Integer> scoreTracker = new DisplayItem<>("Orbs ", game::getScore);
	private DisplayItem<Integer> livesTracker = new DisplayItem<>("Lives", game::getLives);
	private DisplayItem<Integer> levelTracker = new DisplayItem<>("Level", game::getCurrentLevel);
	private DisplayItem<GameMode> modeTracker = new DisplayItem<>("Mode", game::getGameMode);

	public StatusLayer() {
		highScoreManager = new HighScoreManager();
		boxSteps = game.getTargetFps() / 2;
		addKeyboarder(new StatusMenuKeyboarder());
		int halfTile = game.getTileSize() / 2;
		int xViewSize = game.getXViewSize();
		xCen = game.convertXGridToPixel(xViewSize / 2) + halfTile;
		yCen = game.convertYGridToPixel(game.getYViewSize() / 2) + halfTile;
		xRt = game.convertXGridToPixel(xViewSize);

		nf.setMaximumFractionDigits(2);

		game.addDisplayItem(HorizontalAlignment.Left, scoreTracker);
		game.addDisplayItem(HorizontalAlignment.Left, livesTracker);
		game.addDisplayItem(HorizontalAlignment.Left, levelTracker);
		game.addDisplayItem(HorizontalAlignment.Right, modeTracker);
	}

	@Override
	public void paint2(Graphics gfx, Rectangle bounds) {
		gfx.setClip(null);
		int width = game.getXViewSize();
		int height = game.getYViewSize();

		Color gameShadow = game.getDefaultShadow();
		Color gameFore = game.getDefaultFore();
		Color gameBack = game.getDefaultBack();
		if (game.isDebugEnabled()) {
			gfx.setColor(gameShadow);
			// vertical lines
			for (int i = 0; i <= width; i++) {
				int x = game.convertXGridToPixel(i);
				int y1 = game.convertYGridToPixel(0);
				int y2 = game.convertYGridToPixel(height);
				gfx.drawLine(x, y1, x, y2);
			}
			// horizontal lines
			for (int j = 0; j <= height; j++) {
				int x1 = game.convertXGridToPixel(0);
				int x2 = game.convertXGridToPixel(width);
				int y = game.convertYGridToPixel(j);
				gfx.drawLine(x1, y, x2, y);
			}
			// TODO #2 Write the (x,y) coordinate in each of the cells on the grid
			for (int i = 0; i < width; i++) {
				for (int j = 0; j <= height; j++) {
					int x = game.convertXGridToPixel(i);
					int y = game.convertYGridToPixel(j)+12;
					gfx.drawString(""+i+","+j+"", x, y);
				
				}
			}
		}
		FontMetrics fm = gfx.getFontMetrics();
		{
			// Left side
			int y = 15;
			List<DisplayItem<?>> leftLabels = game.getDisplayItemList(HorizontalAlignment.Left);
			int longestLabelLen = 0;
			for (DisplayItem<?> di : leftLabels) {
				int w = fm.stringWidth(di.getName());
				if (w > longestLabelLen)
					longestLabelLen = w;
			}
			for (DisplayItem<?> di : leftLabels) {
				drawStringShadowed(gfx, di.getName() + ": ", 12 + longestLabelLen, y, gameFore, gameShadow, 2, 1);
				drawStringShadowed(gfx, di.getValueAsString(), longestLabelLen + 12, y, gameFore, gameShadow, 2, -1);
				y += GameEngine.TOP_DISPLAY_ROW_HEIGHT;
			}
		}
		{
			// Right side
			int y = 15;
			List<DisplayItem<?>> rightLabels = game.getDisplayItemList(HorizontalAlignment.Right);
			int longestValueLen = 0;
			for (DisplayItem<?> di : rightLabels) {
				int w = fm.stringWidth(di.getValueAsString());
				if (w > longestValueLen)
					longestValueLen = w;
			}
			for (DisplayItem<?> di : rightLabels) {
				int x = xRt - 5 - longestValueLen;
				drawStringShadowed(gfx, di.getName() + ": ", x, y, gameFore, gameShadow, 2, 1);
				drawStringShadowed(gfx, di.getValueAsString(), x, y, gameFore, gameShadow, 2, -1);
				y += GameEngine.TOP_DISPLAY_ROW_HEIGHT;
			}
		}

		GameMode gameMode = game.getGameMode();
		switch (gameMode) {
		case Over:
			if (boxStep == 0)
				boxStep++;
			drawGameOverBanner(gfx);
			break;
		case Pause:
			drawPauseBanner(gfx);
			break;
		case Play:
			break;
		}
	
	}

	public int getMaxWindowWidth() {
		return game.getXViewSize() * game.getTileSize() - 30;
	}

	private class BoxStruct {
		public boolean completed = false;
		public double pct;
	}

	private enum GameOverMode {
		EnterHighScore,
		Wait;
	}

	private class ShadowedFont {
		public final int border;
		public final Font font;

		public ShadowedFont(String string, int bold, int size, int border) {
			font = new Font(string, bold, size);
			this.border = border;
		}
	}

	private class StatusMenuKeyboarder implements Keyboarder {
		@Override
		public void keyDown(int keycode, int modifiers) {
			GameMode gameMode = game.getGameMode();
			if (gameMode == GameMode.Play) {
				if (keycode == 'p' || keycode == 'P') {
					// pause now, and open the box
					game.setGameMode(GameMode.Pause);
					boxStep = 1;
				}
			}

			else if (gameMode == GameMode.Pause) {
				if (keycode == 'p' || keycode == 'P') {
					// start closing the box, which will later un-pause the game
					boxStep++;
				}
			}

			else if (gameMode == GameMode.Over) {
				if ((modifiers & KeyEvent.CTRL_DOWN_MASK) > 0 && (keycode == 'n' || keycode == 'N')) {
					// start closing the box, which will later restart the game
					boxStep++;
					gameOverShouldNewGame = true;
				} else if (keycode == KeyEvent.VK_ESCAPE) {
					// start closing the box
					boxStep++;
				}
				if (gameOverMode == GameOverMode.EnterHighScore) {
					if (keycode == KeyEvent.VK_ENTER) {
						highScoreManager.add(new HighScore(highScoreName, game.getScore()));
						gameOverMode = GameOverMode.Wait;
						highScoreName = "";
					} else if (keycode == KeyEvent.VK_BACK_SPACE) {
						if (highScoreName.length() > 0) {
							highScoreName = highScoreName.substring(0, highScoreName.length() - 1);
						}
					} else if (highScoreName.length() < HighScoreManager.MAX_USERNAME_LENGTH) {
						if (keycode >= 32 && keycode <= 105) {
							if (keycode >= KeyEvent.VK_NUMPAD0 && keycode <= KeyEvent.VK_NUMPAD9) {
								keycode -= KeyEvent.VK_NUMPAD0;
								keycode += KeyEvent.VK_0;
							}
							char ch = (char) keycode;
							highScoreName = highScoreName + ch;
						}
					}

				}
			}
		}

		@Override
		public void keyUp(int keycode, int modifiers) {
		}
	}

	@Override
	protected void changeLevel(int level) {
	}

	@Override
	protected boolean disabledDuring(GameMode gameMode) {
		return false;
	}

	protected BoxStruct computeBoxParams() {
		BoxStruct result = new BoxStruct();
		if (boxStep == 0) {
			result.pct = 0;
			// not paused
		} else if (boxStep < boxSteps) {
			// opening
			result.pct = ((double) boxStep) / boxSteps;
			boxStep++;
		} else if (boxStep == boxSteps) {
			// hold open
			result.pct = 1;
		} else {
			// closing
			result.pct = ((double) (2 * boxSteps - boxStep)) / boxSteps;

			boxStep++;
			if (boxStep >= 2 * boxSteps) {
				result.completed = true;
				boxStep = 0;
			}
		}
		result.pct = easeInOut(result.pct);
		return result;
	}

	public void drawBox(Graphics gfx, double pct, int alignment, String... textlines) {
		if (gfx instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) gfx;
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		}
		int longestText = 0;
		{
			boolean first = true;
			ShadowedFont sf = bannerFont;
			for (String s : textlines) {
				if (s == null)
					continue;
				Font font = sf.font;
				FontMetrics fm = gfx.getFontMetrics(font);
				int len = fm.stringWidth(s);
				if (len > longestText) {
					longestText = len;
				}

				if (first) {
					sf = messageFont;
					first = false;
				}
			}
		}

		int hw = Math.max(longestText, BOX_HALF_WIDTH);
		int halfWindowWidth = getMaxWindowWidth() / 2;
		hw = Math.min(halfWindowWidth, hw);
		int textHeight = ((textlines.length - 1) * messageFont.font.getSize() + bannerFont.font.getSize()) / 2;
		int hh = Math.max(BOX_HALF_HEIGHT, textHeight);
		double r = getBoxRadius(hw, hh) * pct;

		hw = Math.min(hw, (int) r);
		hh = Math.min(hh, (int) r);

		Color colTop = Color.getHSBColor(boxColor, 0.4f, 1.0f);
		Color colText = Color.getHSBColor(boxColor, 1.0f, 0.9f);
		Color colShadow = Color.getHSBColor(boxColor, 0.85f, .48f);

		// makes the shadow boxes collapse when at the very open / very close
		int dist = Math.min(6, (int) r);
		gfx.setColor(colTop);
		gfx.fillRect(xCen - hw - dist, yCen - hh - dist, hw * 2, hh * 2);
		gfx.setColor(colShadow);
		gfx.fillRect(xCen - hw + dist, yCen - hh + dist, hw * 2, hh * 2);
		gfx.setColor(Color.blue);
		gfx.fillRect(xCen - hw, yCen - hh, hw * 2, hh * 2);

		// only draw text inside the center blue box
		gfx.setClip(xCen - hw, yCen - hh, hw * 2, hh * 2);

		Font oldFont = gfx.getFont();
		// First font is the banner style
		ShadowedFont sf = bannerFont;
		int yLoc = yCen - hh + 2;
		boolean first = true;
		for (String s : textlines) {
			Font font = sf.font;
			FontMetrics metrics = gfx.getFontMetrics(font);
			yLoc += metrics.getAscent();
			gfx.setFont(font);
			Color c;
			int xLoc;
			int align;
			if (first || alignment == 0) {
				c = colText;
				xLoc = xCen;
				align = 0;
			} else {
				c = Color.white;
				if (alignment < 0) {
					xLoc = xCen - hw + 5;
				} else {
					xLoc = xCen + hw - 5;
				}
				align = alignment;
			}

			drawStringShadowed(gfx, s, xLoc, yLoc, c, Color.black, sf.border, align);

			if (first) {
				// rest of the lines are using message font... smaller.
				sf = messageFont;
				yLoc += 6;
				first = false;
			}
		}

		// cycle the color for funsies
		boxColor += 0.005;

		gfx.setFont(oldFont);
		gfx.setClip(null);
	}

	private boolean gameOverShouldNewGame = false;

	private void drawGameOverBanner(Graphics gfx) {
		BoxStruct result = computeBoxParams();
		if (result.completed && gameOverShouldNewGame) {
			game.newGame(2);
		}
		List<String> strings = new ArrayList<>();
		strings.add("GAME OVER!");
		switch (gameOverMode) {
		case EnterHighScore:
			strings.add("Score: " + game.getScore());
			strings.add("");
			strings.add("NEW HIGH SCORE!");
			strings.add("ENTER YOUR NAME:");
			strings.add("");
			strings.add(highScoreName);
			break;
		case Wait: {
			List<HighScore> scores = highScoreManager.getScores();
			for (HighScore highScore : scores) {
				String scoreFormatted = nf.format(highScore.score);
				strings.add(highScore.name + " :: " + scoreFormatted);
			}
		}
			strings.add("");
			strings.add("CTRL+N for New Game");
			strings.add("[ESC] to close");
			break;
		}
		String[] arr = new String[strings.size()];
		arr = strings.toArray(arr);
		drawBox(gfx, result.pct, -1, arr);
	}

	private void drawPauseBanner(Graphics gfx) {
		BoxStruct result = computeBoxParams();
		if (result.completed) {
			game.setGameMode(GameMode.Play);
		}
		drawBox(gfx, result.pct, 0, "PAUSED!", "Level " + game.getCurrentLevel(), "Score: " + game.getScore(), "",
				"[P] to un-pause");
	}

	public void drawTick(Graphics gfx, Color c, int x, int y) {
		gfx.setColor(c);
		gfx.drawLine(x - 2, y, x + 2, y);
		gfx.drawLine(x, y - 2, x, y + 2);
		gfx.drawString("(" + x + "," + y + ")", x, y);
	}

	private double easeInOut(double t) {
		return Math.pow(t, 2);
	}

	@Override
	public void gameModeChanged(GameMode gameMode) {
		switch (gameMode) {
		case Over:
			if (highScoresEnabled) {
				gameOverMode = GameOverMode.EnterHighScore;
			} else {
				gameOverMode = GameOverMode.Wait;
			}
			break;
		case Pause:
			break;
		case Play:
			break;
		}
	}

	@Override
	protected int getAdvanceDelay() {
		return -1;
	}

	/**
	 * @param alignment -1 for left align, 0 for centre, 1 for right-align
	 */
	public static void drawStringShadowed(Graphics gfx, String s, int x, int y, Color mainColor, Color shadowColor,
			int thickness, int alignment) {
		if (s == null)
			return;
		if (alignment != -1 && gfx instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) gfx;
			FontMetrics metrics = g2d.getFontMetrics();
			int width = metrics.stringWidth(s);
			if (alignment == 0) {
				x -= width / 2;
			} else if (alignment == 1) {
				x -= width;
			}
		}

		gfx.setColor(shadowColor);
		for (int d = 0; d < thickness; d++) {
			gfx.drawString(s, x + d, y);
			gfx.drawString(s, x - d, y);
			gfx.drawString(s, x + d, y - d);
			gfx.drawString(s, x - d, y - d);
			gfx.drawString(s, x + d, y + d);
			gfx.drawString(s, x - d, y + d);
			gfx.drawString(s, x, y + d);
			gfx.drawString(s, x, y - d);
			gfx.drawString(s, x - d, y + d);
			gfx.drawString(s, x - d, y - d);
			gfx.drawString(s, x + d, y + d);
			gfx.drawString(s, x + d, y - d);

		}
		gfx.setColor(mainColor);
		gfx.drawString(s, x, y);
	}

	private static final int getBoxRadius(int hw, int hh) {
		return (int) Math.sqrt(hw * hw + hh * hh);
	}
}
