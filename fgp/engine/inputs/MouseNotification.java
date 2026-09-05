package fgp.engine.inputs;

/**
 * @author Mr. Hapke
 */
public class MouseNotification extends Notification<MouserEvent> {

	public static final int MOUSE_DOWN = 1;
	public static final int MOUSE_UP = 2;
	public static final int MOUSE_MOVE = 4;
	public static final int MOUSE_ENTER = 8;
	public static final int MOUSE_EXIT = 16;

	public MouseNotification(int type, int x, int y, int button) {
		super(type, new MouserEvent(x, y, button));
	}

	public MouseNotification(int type, MouserEvent me) {
		super(type, me);
	}
}
