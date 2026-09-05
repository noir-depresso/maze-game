package fgp.engine.inputs;

/**
 * @author Mr. Hapke
 *
 */
public abstract class Notification<T> {
	public final int type;
	public final T value;

	public Notification(int type, T value) {
		this.type = type;
		this.value = value;
	}
}
