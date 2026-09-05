package fgp.engine.inputs;

/**
 * @author Mr. Hapke
 *
 */
public interface Keyboarder {

	public void keyDown(int keycode, int modifiers);

	public void keyUp(int keycode, int modifiers);
}
