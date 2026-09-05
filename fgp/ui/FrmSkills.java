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
import fgp.game.classes.Items;
import fgp.game.layers.CharactersLayer;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JTextArea;

/**
 * @author Mr. Hapke
 *
 */
public class FrmSkills extends JFrame {

	private static final long serialVersionUID = 693561859040738681L;
	private static boolean magnet = false;
	private static boolean tp = false;
	private static boolean freeze = false;
	private static boolean trap = false;
	protected JPanel contentPane;
	protected GameEngine game;
	protected JCheckBox chkDisplayDebug;
	private JTextField textField;
	private JTextField txtSkillPoints;
	private static int skillPoint;
	private JButton btnNextLevel;
	private JButton btnPrevLevel;
	private JButton btnRestartLevel;
	private JTextArea txtDiscription;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmSkills frame = new FrmSkills(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public FrmSkills(GameEngine g) {
		this.game = g;
		setTitle("Skills");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(200, 200, 367, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtDiscription = new JTextArea();
		txtDiscription.setBounds(10, 115, 254, 135);
		contentPane.add(txtDiscription);
		
		textField = new JTextField();
		textField.setText("0");
		textField.setForeground(Color.RED);
		textField.setEnabled(false);
		textField.setEditable(false);
		textField.setDisabledTextColor(Color.BLACK);
		textField.setColumns(10);
		textField.setCaretColor(Color.RED);
		textField.setBorder(null);
		textField.setBounds(95, 11, 37, 31);
		contentPane.add(textField);
		
		txtSkillPoints = new JTextField();
		txtSkillPoints.setText("Skill Points");
		txtSkillPoints.setForeground(Color.RED);
		txtSkillPoints.setEnabled(false);
		txtSkillPoints.setEditable(false);
		txtSkillPoints.setDisabledTextColor(Color.BLACK);
		txtSkillPoints.setColumns(10);
		txtSkillPoints.setCaretColor(Color.RED);
		txtSkillPoints.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtSkillPoints.setBounds(10, 11, 75, 31);
		contentPane.add(txtSkillPoints);
		
		btnNextLevel = new JButton("Freeze");
		btnNextLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(freeze)
				txtDiscription.setText("Press 2\nConsumes 1 SKill Point. Freezes the Chaser for 3 turns");
			}
		});
		btnNextLevel.setBounds(10, 53, 122, 23);
		contentPane.add(btnNextLevel);
		
		btnPrevLevel = new JButton("Trap");
		btnPrevLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(trap)
					txtDiscription.setText("Press 3\nConsumes 2 SKill Point. Confuses the Chaser. It will go to where you placed down the lure");
			}
		});
		btnPrevLevel.setBounds(10, 81, 122, 23);
		contentPane.add(btnPrevLevel);
		
		btnRestartLevel = new JButton("Teleport");
		btnRestartLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tp)
					txtDiscription.setText("Press 4\nConsumes 2 SKill Point. Teleports either you or the Chaser to a semi-random location");
			}
		});
		btnRestartLevel.setBounds(142, 53, 122, 23);
		contentPane.add(btnRestartLevel);
		
		JButton btnPrevLevel_1 = new JButton("Magnet");
		btnPrevLevel_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(magnet)
					txtDiscription.setText("Consumes 1 SKill Point. Freezes the Chaser for 3 turns");
			}
		});
		btnPrevLevel_1.setBounds(142, 81, 122, 23);
		contentPane.add(btnPrevLevel_1);
		

	}

	public static void unlock(String skill) {
		switch(skill) {
			case "Dusty Seashell":
				//trap skill
				trap  = true;
				break;
			case "Repeller":
				//freeze
				freeze = true;
				break;
			case "Monkey Paw":
				tp = true;
				//tp
				break;
			case "Money Bag":
				magnet = true;
				//magnet skill
				break;
			case "Compass":
				
				break;
			default:
				break;
			}
		}


		public static int getSkillPoint() {
			return skillPoint;
		}
	
	
		public static void setSkillPoint(int sp) {
			skillPoint+=sp;
		}
	}
