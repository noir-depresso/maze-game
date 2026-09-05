package fgp.engine.highscore;

/**
 * @author Mr. Hapke
 */
public class HighScore implements Comparable<HighScore> {
	public final String name;
	public final double score;

	public HighScore(String name, double score) {
		this.name = name;
		this.score = score;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HighScore [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		builder.append("score=");
		builder.append(score);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(HighScore that) {
		double x = that.score - this.score;
		if (x == 0) {
			x = that.name.compareTo(this.name);
		}
		if (x < 0) {
			return -1;
		}
		if (x > 0) {
			return 1;
		}
		return 0;
	}

}
