package fgp.engine.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathan Hapke
 */
public abstract class AdaptiveTimerThread extends Thread {

	private static final int NUM_FRAMES = 40;
	private static final double ADJUSTMENT_FACTOR = 0.1;
	private double[] tickList = new double[NUM_FRAMES];

	protected volatile boolean kill = false;
	private long millis;
	private final int targetFps;
	private double currentFps;
	private int frameTick = 0;
	private int adjustmentTick;

	private static List<AdaptiveTimerThread> threads = new ArrayList<>();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread("shutdown thread") {
			@Override
			public void run() {
				shutdownThreads();
			}
		});
	}

	public static void shutdownThreads() {
		for (AdaptiveTimerThread t : threads) {
			t.kill = true;
		}
	}

	public AdaptiveTimerThread(String name, int targetFps) {
		super(name);
		this.targetFps = targetFps;
		threads.add(this);
		// making it a little faster than targetFps makes it start more accurately.
		millis = (long) (1000l / targetFps * 0.96);
	}

	public boolean isKill() {
		return kill;
	}

	@Override
	public final void run() {
		while (!kill) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				System.out.println(Thread.interrupted());
			}
			if (kill)
				break;
			try {
				doWork();
				currentFps = calcFps();

				adjustmentTick++;
				if (adjustmentTick >= NUM_FRAMES && currentFps > 1) {
					// only adjust once the FPS can be calculated because we've run enough frames
					double delta = targetFps - currentFps;
					if (Math.abs(delta) > 2.5) {
						double qty = Math.abs(millis * ADJUSTMENT_FACTOR);
						double magnitude = Math.max(1, qty);
						int amt = (int) (Math.copySign(magnitude, delta));
						millis -= amt;
					}
					adjustmentTick = 0;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected abstract void doWork();

	private double calcFps() {
		double newTick = System.currentTimeMillis();

		double prev = tickList[frameTick];
		tickList[frameTick] = newTick;

		double deltaT = newTick - prev;
		// prevent div/0 error
		if (deltaT == 0) {
			currentFps = -1;
		} else {
			currentFps = (1000d * NUM_FRAMES) / deltaT;
		}
		frameTick++;
		if (frameTick >= NUM_FRAMES)
			frameTick = 0;

		return currentFps;
	}

	public double getFps() {
		return currentFps;
	}
}
