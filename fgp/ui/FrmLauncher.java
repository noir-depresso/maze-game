package fgp.ui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import fgp.engine.GameEngine;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

/**
 * @author Mr. Hapke
 */
public class FrmLauncher extends JFrame {

	private static final long serialVersionUID = 3811418719500055475L;
	private JPanel contentPane;
	private GameEngine game;
	private JTextArea txtReadme;
	private int difficulty = 2;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Launch the application.
	 */
	public static void launch(GameEngine game) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					FrmLauncher frame = new FrmLauncher(game);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FrmLauncher(GameEngine game) {
		if (game == null) {
			System.err.println("You must create your game by passing it to FrmLauncher.launch(new YourGame())");
		}
		this.game = game;
		this.game.init();
		setTitle(game.getGameTitle());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 369);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnStart = new JButton("Start Game!");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.newGame(difficulty);
				FrmGame frame = new FrmGame(game);
				frame.setVisible(true);
				FrmLauncher.this.setVisible(false);
			}
		});
		btnStart.setFont(new Font("Candara", Font.BOLD, 20));
		btnStart.setBounds(10, 274, 239, 47);
		contentPane.add(btnStart);

		txtReadme = new JTextArea();
		txtReadme.setFont(new Font("Century Gothic", Font.PLAIN, 16));
		txtReadme.setWrapStyleWord(true);
		txtReadme.setLineWrap(true);
		txtReadme.setText(
				"Customize this frame and explain your game a little bit!\r\n\r\nInclude what the controls and objective of the game are.");
		txtReadme.setBounds(10, 11, 239, 256);
		contentPane.add(txtReadme);
		
		JRadioButton rdbtnEasy = new JRadioButton("Easy");
		rdbtnEasy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = 1;
			}
		});
		buttonGroup.add(rdbtnEasy);
		rdbtnEasy.setBounds(267, 73, 125, 29);
		contentPane.add(rdbtnEasy);
		
		JRadioButton rdbtnMedium = new JRadioButton("Medium");
		rdbtnMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = 2;
			}
		});
		rdbtnMedium.setSelected(true);
		buttonGroup.add(rdbtnMedium);
		rdbtnMedium.setBounds(267, 105, 125, 29);
		contentPane.add(rdbtnMedium);
		
		JRadioButton rdbtnHard = new JRadioButton("Hard");
		rdbtnHard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = 3;
			}
		});
		buttonGroup.add(rdbtnHard);
		rdbtnHard.setBounds(267, 137, 125, 29);
		contentPane.add(rdbtnHard);
		
		JRadioButton rdbtnExpert = new JRadioButton("Expert");
		rdbtnExpert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				difficulty = 4;
			}
		});
		buttonGroup.add(rdbtnExpert);
		rdbtnExpert.setBounds(267, 169, 125, 29);
		contentPane.add(rdbtnExpert);
	}
}
