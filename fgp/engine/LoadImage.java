package fgp.engine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mr. Hapke
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadImage {
	public enum ResizeMode {
		Scaled, Filled, Off;
	}

	String[] value();

	boolean keepIndex() default true;

	boolean showErrors() default true;

	ResizeMode resizeMode() default ResizeMode.Scaled;
}
