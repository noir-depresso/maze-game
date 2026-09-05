package fgp.engine.inputs;

/**
 * @author Mr. Hapke
 */
public class MouserEvent {
	public final int x;
	public final int y;
	public final int button;

	public MouserEvent(int x, int y, int button) {
		this.x = x;
		this.y = y;
		this.button = button;
	}
}
