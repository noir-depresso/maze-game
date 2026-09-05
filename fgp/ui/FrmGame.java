package fgp.ui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fgp.engine.GameEngine;
import fgp.engine.GameEngine.OsDetected;
import fgp.engine.inputs.KeyNotification;
import fgp.engine.inputs.MouseNotification;

/**
 * @author Mr. Hapke
 */
public class FrmGame extends JFrame {

	private static final long serialVersionUID = 6255440113168001509L;
	private static final int GAP = 0;

	private final class KeyManager extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			int code = e.getKeyCode();
			int modifiers = e.getModifiersEx();
			game.notify(new KeyNotification(KeyNotification.KEY_UP, code, modifiers));
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			int modifiers = e.getModifiersEx();
			game.notify(new KeyNotification(KeyNotification.KEY_DOWN, code, modifiers));
		}
	}

	private final class FormEvents extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			moveDebug(frmDebug,false);
			moveDebug(f,true);
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			moveDebug(frmDebug,false);
			moveDebug(f,true);
		}

		@Override
		public void componentShown(ComponentEvent e) {
			start();
		}
	}

	private final class MouseManager implements MouseMotionListener, MouseListener {

		@Override
		public void mouseMoved(MouseEvent e) {
			Point point = e.getPoint();
			int button = e.getButton();
			game.notify(new MouseNotification(MouseNotification.MOUSE_MOVE, point.x, point.y, button));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Point point = e.getPoint();
			int button = e.getButton();
			game.notify(new MouseNotification(MouseNotification.MOUSE_EXIT, point.x, point.y, button));
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Point point = e.getPoint();
			int button = e.getButton();
			game.notify(new MouseNotification(MouseNotification.MOUSE_DOWN, point.x, point.y, button));
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point point = e.getPoint();
			int button = e.getButton();
			game.notify(new MouseNotification(MouseNotification.MOUSE_UP, point.x, point.y, button));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			Point point = e.getPoint();
			int button = e.getButton();
			game.notify(new MouseNotification(MouseNotification.MOUSE_ENTER, point.x, point.y, button));
		}

		@Override
		public void mouseDragged(MouseEvent e) {
//			Point point = e.getPoint();
//			int button = e.getButton();
//			game.notify(new MouseNotification(MouseNotification.MOUSE_DRAG, point.x, point.y, button));
		}

	}

	private JPanel contentPane;
	private GameEngine game;
	private GamePanel pnlMain;
	private FrameManager manager;
	private JFrame frmDebug;
	private JFrame f;

	private void init() {
		pnlMain = new GamePanel(game);
		MouseManager mouser = new MouseManager();
		pnlMain.addMouseMotionListener(mouser);
		pnlMain.addMouseListener(mouser);
		addKeyListener(new KeyManager());
		manager = pnlMain.getManager();
	}

	private void start() {
		manager.start();
	}

	/**
	 * Create the frame.
	 */
	public FrmGame(GameEngine game) {
		this.game = game;
		init();

//		setTitle("Game!");
		setTitle(game.getGameTitle());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		int tileSize = game.getTileSize();
		int debugSize = GamePanel.DEBUG_HEIGHT;

		Insets frameSize = getInsets();
		int extraX, extraY;
		if (game.os == OsDetected.Mac) {
			extraX = 0;
			extraY = 28;
		} else {
			extraX = 16;
			extraY = 42;
		}
		int w = game.convertXGridToPixel(game.getXViewSize()) + frameSize.left + frameSize.right + extraX;
		int h = game.convertYGridToPixel(game.getYViewSize()) + debugSize + frameSize.bottom + frameSize.top + extraY;
		setLocation(100, 100);
		setBounds(100, 100, w, h);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		pnlMain.setBackground(Color.WHITE);
		pnlMain.setForeground(Color.BLACK);
		pnlMain.setBounds(0, 0, w, h);
		contentPane.setLayout(null);
		contentPane.add(pnlMain);

		addComponentListener(new FormEvents());
		frmDebug = game.createDebugFrame();
		if (frmDebug != null) {
			frmDebug.setVisible(true);
			moveDebug(frmDebug,false);
			f = new FrmSkills(game);
			moveDebug(f,true);
			f.setVisible(true);
		}
	}

	private void moveDebug(JFrame frame, boolean second) {
		if (frame != null) {
			int x = getX();
			int y = getY();
			int w = getWidth();
			if(second)
				y+=300;
			frame.setLocation(x + w + GAP, y);
		}
	}

}
