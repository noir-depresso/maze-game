package fgp.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fgp.engine.GameEngine;
import fgp.game.layers.CharactersLayer;

/**
 * @author Mr. Hapke
 *
 */
public class FrmDebug extends JFrame {

	private static final long serialVersionUID = 693561859040738681L;
	protected JPanel contentPane;
	protected GameEngine game;
	protected JCheckBox chkDisplayDebug;

	public FrmDebug(GameEngine g) {
		this.game = g;
		setTitle("Debug");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 367, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		chkDisplayDebug = new JCheckBox("Display Debug");
		chkDisplayDebug.setSelected(true);
		chkDisplayDebug.setSelected(g.isDebugEnabled());
		chkDisplayDebug.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				game.setDebugEnabled(chkDisplayDebug.isSelected());
			}
		});
		chkDisplayDebug.setBounds(17, 19, 122, 23);
		contentPane.add(chkDisplayDebug);

		JButton btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.newGame(2);
			}
		});
		btnNewGame.setBounds(221, 19, 122, 23);
		contentPane.add(btnNewGame);

		JButton btnNextLevel = new JButton("Next Level");
		btnNextLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.nextLevel();
			}
		});
		btnNextLevel.setBounds(221, 95, 122, 23);
		contentPane.add(btnNextLevel);

		JButton btnRestartLevel = new JButton("Restart Level");
		btnRestartLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.restartLevel();
			}
		});
		btnRestartLevel.setBounds(221, 151, 122, 23);
		contentPane.add(btnRestartLevel);

		JButton btnGameOver = new JButton("Game Over");
		btnGameOver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.gameOver();
			}
		});
		btnGameOver.setBounds(221, 49, 122, 23);
		contentPane.add(btnGameOver);

		JButton btnPrevLevel = new JButton("Previous Level");
		btnPrevLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.changeLevel(game.getCurrentLevel() - 1);
			}
		});
		btnPrevLevel.setBounds(221, 123, 122, 23);
		contentPane.add(btnPrevLevel);
		
		JCheckBox chkSquareMode = new JCheckBox("Square Mode");
		chkSquareMode.setSelected(true);
		chkDisplayDebug.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				System.out.println(chkSquareMode.isSelected());
				CharactersLayer.setSquareMode(chkSquareMode.isSelected());;
			}
		});
		chkSquareMode.setBounds(17, 73, 122, 23);
		contentPane.add(chkSquareMode);

	}
}
