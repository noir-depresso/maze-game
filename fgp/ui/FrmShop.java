package fgp.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import fgp.engine.GameEngine;
import fgp.engine.GameMode;
import fgp.engine.util.PictureBox;
import fgp.game.MazeGame;
import fgp.game.bodies.Runner;
import fgp.game.classes.Items;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

public class FrmShop extends JFrame {
	private Runner player;
	private static int orb = 0;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea txtDescription;
	private PictureBox picOrb;
	public final static int pNum = 3;
	private final int pw = 250;
	private final int ph = 280;
	private int space = 20;
	private JPanel p1;
	private JPanel p2;
	private JPanel p3;
	//private JPanel p4;
	//private JPanel p5;
	
	private final int start_x = 10;
	private final int start_y = 100;
	private int selected = -1;
	private JPanel[] panelList = {p1,p2,p3};
	private Items[] shopList = new Items[pNum];
	private JButton btnReroll;
	private JScrollPane scrollPane;
	private JTextField txtOrb;
	private JTextField txtPrice;
	private List<Items> selfpool;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmShop frame = new FrmShop(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrmShop(Runner player) {
		Items.init();
		this.player = player;
		if(player==null) {
			orb=1000;
		}
		else {
			orb+=player.getOrbs();
			if(Runner.devmode) orb+=100000;
			System.out.println(orb);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 822, 575);
		contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(new Color(0, 0, 0)));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		txtOrb = new JTextField();
		txtOrb.setDisabledTextColor(new Color(0, 0, 0));
		txtOrb.setCaretColor(new Color(255, 0, 0));
		txtOrb.setEditable(false);
		txtOrb.setEnabled(false);
		txtOrb.setForeground(new Color(255, 0, 0));
		txtOrb.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtOrb.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtOrb.setBounds(63, 13, 88, 61);
		contentPane.add(txtOrb);
		txtOrb.setColumns(10);
		txtOrb.setText(""+orb);
		
		picOrb = new PictureBox("images", "orb_32.png");
		picOrb.setBounds(10, 13, 74, 55);
		contentPane.add(picOrb);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 406, 653, 119);
		contentPane.add(scrollPane);
		
		txtDescription = new JTextArea();
		txtDescription.setDisabledTextColor(new Color(0, 0, 0));
		txtDescription.setSelectionColor(new Color(255, 0, 0));
		txtDescription.setCaretColor(new Color(255, 0, 0));
		txtDescription.setEditable(false);
		txtDescription.setEnabled(false);
		txtDescription.setForeground(new Color(255, 0, 0));
		scrollPane.setViewportView(txtDescription);
		txtDescription.setWrapStyleWord(true);
		txtDescription.setLineWrap(true);
		txtDescription.setFont(new Font("Lucida Console", Font.PLAIN, 16));
		
		JButton btnInfo = new JButton("Info");
		btnInfo.setBounds(673, 12, 118, 62);
		contentPane.add(btnInfo);
		
		JButton btnBuy = new JButton("Buy");
		btnBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				purchased(selected);
			}
		});
		btnBuy.setBounds(678, 464, 124, 61);
		contentPane.add(btnBuy);
		
		btnReroll = new JButton("Reroll");
		btnReroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				for (int i = 0; i < pNum; i++) {
					delete(i);
					setUpShop(i);
				}
				draw(false);
				putInShop();
			}
		});
		btnReroll.setBounds(539, 13, 124, 61);
		contentPane.add(btnReroll);
		
		txtPrice = new JTextField();
		txtPrice.setText("");
		txtPrice.setForeground(Color.RED);
		txtPrice.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtPrice.setEnabled(false);
		txtPrice.setEditable(false);
		txtPrice.setDisabledTextColor(Color.BLACK);
		txtPrice.setColumns(10);
		txtPrice.setCaretColor(Color.RED);
		txtPrice.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		txtPrice.setBounds(268, 13, 144, 61);
		contentPane.add(txtPrice);
		
		JButton btnResume = new JButton("Resume");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//GameEngine.getInstance().setGameMode(GameMode.Play);
				GameEngine.getInstance().nextLevel();
				Runner.faster_Speedfactor();
				dispose();
			}
		});
		btnResume.setBounds(678, 392, 124, 61);
		contentPane.add(btnResume);
		
		
		for (int i = 0; i < panelList.length; i++) {
			setUpShop(i);
		}
		draw(false);
		putInShop();
		
		
	}
	private void setUpShop(int pindex) {
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(start_x+(pw+space)*pindex, start_y, pw, ph);
		contentPane.add(panel);
		panel.setLayout(null);
		
//		JTextArea pic = new JTextArea();
//		pic.setBounds(25, 25, 200, 200);
//		panel.add(pic);
		
		panelList[pindex] = panel;
		JButton btnNewButton = new JButton("");
		btnNewButton.setBounds(65, 220, 125, 50);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDescript(pindex);
			}
		});
		panel.add(btnNewButton);
		

		
	}
	public void putInShop() {
		System.out.println();
		for (int i = 0; i < pNum; i++) {
			JPanel p = panelList[i];
			Items it = selfpool.get(i);
			String name = it.getName();
			String img = it.getImg();
			shopList[i]= it;
			if(img!=null) {
				PictureBox picMe = new PictureBox("images", img);
				picMe.setBounds(25, 10, 200, 200);
				p.add(picMe);
			}
			Component[] pList = p.getComponents();
			for (Component component : pList) {
				if (component instanceof JButton) {
					JButton jb = (JButton) component;
					jb.setText(name);
				}
				if (component instanceof JTextArea) {
					JTextArea txtName = (JTextArea) component;
					txtName.setText(name);
				}
			}
		}
		
	}
	public void showDescript(int index) {
		Items i = shopList[index];
		selected = index;
		txtDescription.setText(i.getScript());
		txtPrice.setText("Price: "+i.pricing());
		
	}
	public void purchased(int index) {
		if(index == -1) {
			return;
		}
		JPanel p = panelList[index];
		Items i = shopList[index];
		i.setAva(false);
		int price = i.pricing();
		if(orb-price >= 0) {
			//successfully bought
			Component[] pList = p.getComponents();
			for (Component component : pList) {
				if (component instanceof JButton) {
					JButton jb = (JButton) component;
					jb.setBackground(Color.gray);
				}
			}
			orb-=price;
			txtOrb.setText(""+orb);
			if(player!=null)
				player.bought(i.getName());
		}
		else {
			JOptionPane.showMessageDialog(contentPane, "You're Poor!", "L", 0);
		}
	}
	public void draw(boolean remove) {
		 selfpool = Items.getRandomItem(false);
	}
	
	private void delete(int pindex) {
		panelList[pindex].setVisible(false);;
	}
}
