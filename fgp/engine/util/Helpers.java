package fgp.engine.util;

import fgp.game.classes.Node;

/**
 * @author Mr. Hapke
 *
 */
public abstract class Helpers {

	/**
	 * At least min, less than max.
	 */
	public static boolean inside(int min, int max, int val) {
		return val >= min && val < max;
	}

	public static boolean inside(double min, double max, double val) {
		return val >= min && val < max;
	}

	public static Node[][] transpose(Node[][] in) {
		Node[][] out = new Node[in[0].length][in.length];
		for (int i = 0; i < in.length; i++) {
			for (int j = 0; j < in[i].length; j++) {
				out[j][i] = in[i][j];
			}
		}
		return out;
	}

	public static int[][] transpose(int[][] in) {
		int[][] out = new int[in[0].length][in.length];
		for (int i = 0; i < in.length; i++) {
			for (int j = 0; j < in[i].length; j++) {
				out[j][i] = in[i][j];
			}
		}
		return out;
	}

	public static int[][] flipVertical(int[][] in) {
		int h = in.length;
		int[][] out = new int[in[0].length][h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < in[i].length; j++) {
				out[i][h - j - 1] = in[i][j];
			}
		}
		return out;
	}

	public static int limit(int min, int max, int val) {
		if (val < min)
			return min;
		else if (val > max)
			return max;
		else
			return val;
	}

	public static double limit(double min, double max, double val) {
		if (val < min)
			return min;
		else if (val > max)
			return max;
		else
			return val;
	}
}
