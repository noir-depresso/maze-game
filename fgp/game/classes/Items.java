package fgp.game.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fgp.ui.FrmShop;

public class Items {
	public static List<Items> itemPool = new ArrayList<>();
	private static boolean initialized = false;
	public static Items example;
	private String script;
	private String img;
	private String name;
	private int price;
	private boolean availability;
	public Items(String n, String ds, String im, int price) {
		name = n;
		script = ds;
		img = im;
		availability = true;
		itemPool.add(this);
		this.price = price;
	}
	public Items() {
		name = "None";
		script = "Nothing!";
		img = "none.png";
		availability = true;
		itemPool.add(this);
		price = 1;
	}
	public static List<Items> getRandomItem(boolean remove) {
		System.out.println(itemPool);
		int size = Items.itemPool.size();
		Set<Items> resultSet = new HashSet<>();
		List<Items> resultList = new ArrayList<>();
		
			while(resultSet.size()<FrmShop.pNum) {
				int randIndex = (int) (Math.random()*size);
				if(resultSet.size()==size) {
					resultList.addAll(resultSet);
					while(resultList.size()<FrmShop.pNum) {
						resultList.add(new Items());
					}
					return resultList;
				}
				if(remove) {
					Items it = Items.itemPool.remove(randIndex);
					resultSet.add(it);
				}
				else {
					Items it = Items.itemPool.get(randIndex);
					resultSet.add(it);
				}
				//System.out.println(Items.itemPool.size());
			}
			resultList.addAll(resultSet);
			System.out.println(resultList.toString());
			return resultList;
		}

	public static void removeItem(String name) {
		int index = -1;
		for (Items it : itemPool) {
			String n = it.name;
			if(n.equals(name)) {
				index = itemPool.indexOf(it);
				System.out.println("index!!!!" + index);
				break;
			}
		}
		if(index!=-1)
		itemPool.remove(index);
		System.out.println(itemPool);
		
	}
	
	public String getImg() {
		return img;
	}
	
	public String getScript() {
		return script;
	}
	public boolean getAva() {
		return availability;
	}
	public void setAva(boolean b) {
		availability = b;
		//itemPool.remove(itemPool.indexOf(this));
	}
	public String getName() {
		return name;
	}
	public int pricing() {
		return price;
	}
	public String toString() {
		return name;
	}
	
	
	public static void init() {
		if(initialized)
			return;
		//Items i1 = new Items("name","description","image", price);
		Items i1 = new Items("Heart","A somehow still beating Heart.\n\n "
				+ "Buyting it gives +1 HP",
				"heart_item.png", 
				600);
		Items i2 = new Items("Repeller","An ancient device built with technology.\n\n "
				+ "Unlocks the freezing ability: Freezes the Chaser for 3 moves (depends on FPS and consumes 1 skill point)",
				"Mechanical_bomb.png", 
				300);
		Items i3 = new Items("Monkey Paw","Make a wish! But there is only one option. \n\n "
				+ "Unlocks the teleporting ability: Teleport either you or the Chaser to a random dead end (consumes 2 skill point)",
				"monkey_paw.png",
				100);
		Items i4 = new Items("Compass","The Survivor's must-have! 90% off for only a cheap cheap human arm!\n\n "
				+ "Points to the Exit",
				"compass.png",
				50);
		Items i5 = new Items("Qliphoth","And it grew both day and night, Till it bore an apple bright. \n\n "
				+ "gives 2 skill point",
				"fruit.jpg",
				150);
		Items i6 = new Items("Magic Wand","Condutor's Wand. A Grand Show is going to need a dramatic entrance! \n\n "
				+ "Delays the entrance of the Chaser by a duration"
				+ "\nPS: the audience is ready =D",
				"wand.jpg",
				100);
		Items i7 = new Items("Money Bag","An old pocket that seems to be able to collect orbs easier. \n\n "
				+ "Unlocks the magnet skill: You can now collect orbs from a further distance for a duration (uses 1 skill points).",
				"moneybag.jpg",
				200);
		Items i8 = new Items("Dusty Seashell","A construction of demise; restless souls reside within the shell. Put your ears against the shell and hear their final call...\n\n "
				+ "Unlocks the trap skill: Upon activation, the Chaser will confuse the location of you and the shell (consumes 2 skill points).",
				"seashell.png",
				150);
		Items i9 = new Items("Lantern Fuse","An ancient bit of twigs and oil twisted. \n\n "
				+ "Upgrades lantern, you can now see further.",
				"fireball_32.png",
				150);
		initialized = true;
	}

}
