package fgp.engine;

/**
 * @author Mr. Hapke
 */
public abstract class KeyUtil {

	private KeyUtil() {
		// not intended to be instantiated
	}

	public static String convertClassToName(Class<?> cls) {
		return cls.getCanonicalName();
	}

	public static String getShortName(Object o) {
		return o.getClass().getSimpleName();
	}

	public static String getName(Object o) {
		return KeyUtil.convertClassToName(o.getClass());
	}

	public static String createKey(String id, int i) {
		return id + "$" + i;
	}
}
