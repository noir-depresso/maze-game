package fgp.engine.highscore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fgp.engine.GameEngine;

/**
 * @author Mr. Hapke
 */
public class HighScoreManager {
	private static final int MAX_SCORES = 7;
	private static final String HIGHSCORES_FILE = "./highscores.txt";
	public static final int MAX_USERNAME_LENGTH = 15;

	private List<HighScore> scores;

	public List<HighScore> getScores() {
		if (scores == null) {
			scores = new ArrayList<>();

			File f = new File(HIGHSCORES_FILE);

			if (f.exists()) {
				try {
					BufferedReader r = new BufferedReader(new FileReader(f));

					String line;
					while ((line = r.readLine()) != null) {
						try {
							if (line.length() == 0) {
								continue;
							}

							String[] split = line.split("=");
							String name = split[0];
							double score = Double.parseDouble(split[1]);
							scores.add(new HighScore(name, score));
						} catch (Exception e) {
						}

					}
					r.close();
				} catch (Exception e) {
					if (GameEngine.getInstance().isDebugEnabled()) {
						System.err.println("Could not read high scores file.");
					}
				}
			}
		}
		return scores;
	}

	public void write() {
		if (scores == null)
			return;

		File f = new File(HIGHSCORES_FILE);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < MAX_SCORES && i < scores.size(); i++) {
				HighScore hs = scores.get(i);
				writer.write(hs.name);
				writer.write('=');
				writer.write(Double.toString(hs.score));
				writer.write('\n');
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			if (GameEngine.getInstance().isDebugEnabled()) {
				System.err.println("Could not write high scores file.");
			}
		}
	}

	public void add(HighScore hs) {
		if (scores == null) {
			getScores();
		}
		scores.add(hs);
		Collections.sort(scores);
		write();
	}
}
