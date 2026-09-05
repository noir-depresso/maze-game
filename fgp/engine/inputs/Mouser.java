package fgp.engine.inputs;

/**
 * @author Mr. Hapke
 */
public interface Mouser {
	void mouseDown(MouserEvent me);

	void mouseUp(MouserEvent me);

	void mouseMove(MouserEvent me);

	default void mouseEnter(MouserEvent me) {
	};

	default void mouseExit(MouserEvent me) {
	};
}
