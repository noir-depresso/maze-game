package fgp.engine;

/**
 * @author Mr. Hapke
 */
public abstract class Waiter {
	private final int delay;
	private int i;
	public Waiter (int delay) {
		this.delay = delay;
		this.i = delay;
		GameEngine.getInstance().addWaiter(this);
	}
	
	/**
	 * @return true if the waiter is complete, and should be removed
	 */
	protected abstract boolean doAction();

	public final boolean frame() {
		i--;
		if (i <= 0) {
			boolean result = doAction();
			i = delay;
			return result;
		}
		return false;
	}
}
