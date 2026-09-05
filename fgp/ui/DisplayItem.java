package fgp.ui;

import java.util.function.Supplier;

/**
 * @author Nathan Hapke
 */
public class DisplayItem<T> {

	protected final String name;
	protected Supplier<T> supplier;

	public DisplayItem(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return supplier.get();
	}

	public String getValueAsString() {
		T value = getValue();
		if (value == null)
			return "null";
		else
			return value.toString();
	}
}
