package fgp.engine.inputs;

/**
 * @author Mr. Hapke
 *
 */
public class KeyNotification extends Notification<Integer> {

	public static final int KEY_DOWN = 1;
	public static final int KEY_UP = 2;
	public final int modifiers;

	public KeyNotification(int type, Integer value, int modifiers) {
		super(type, value);
		this.modifiers = modifiers;
	}
}
