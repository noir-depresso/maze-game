package fgp.engine;

/**
 * @author Mr. Hapke
 *
 */
public class Transaction<T> {
	public enum Mode {
		Add;
	}

	public final Mode mode;
	public final T value;

	public Transaction(Mode mode, T value) {
		this.mode = mode;
		this.value = value;
	}

}
