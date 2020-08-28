package scw.core.annotation;

import scw.lang.NestedRuntimeException;

/**
 * Thrown by {@link AnnotationUtils} and <em>synthesized annotations</em>
 * if an annotation is improperly configured.
 * 
 * @see AnnotationUtils
 * @see SynthesizedAnnotation
 */
@SuppressWarnings("serial")
public class AnnotationConfigurationException extends NestedRuntimeException {

	/**
	 * Construct a new {@code AnnotationConfigurationException} with the
	 * supplied message.
	 * @param message the detail message
	 */
	public AnnotationConfigurationException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@code AnnotationConfigurationException} with the
	 * supplied message and cause.
	 * @param message the detail message
	 * @param cause the root cause
	 */
	public AnnotationConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
