package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;

/**
 * Exception to be thrown when an actual type conversion attempt fails.
 */
public class ConversionFailedException extends ConversionException {
	private static final long serialVersionUID = 1L;

	@Nullable
	private final TypeDescriptor sourceType;

	private final TypeDescriptor targetType;

	@Nullable
	private final Object value;

	/**
	 * Create a new conversion exception.
	 * 
	 * @param sourceType the value's original type
	 * @param targetType the value's target type
	 * @param value      the value we tried to convert
	 * @param cause      the cause of the conversion failure
	 */
	public ConversionFailedException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType,
			@Nullable Object value, Throwable cause) {

		super("Failed to convert from type [" + sourceType + "] to type [" + targetType + "] for value '"
				+ ObjectUtils.toString(value) + "'", cause);
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.value = value;
	}

	/**
	 * Return the source type we tried to convert the value from.
	 */
	@Nullable
	public TypeDescriptor getSourceType() {
		return this.sourceType;
	}

	/**
	 * Return the target type we tried to convert the value to.
	 */
	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

	/**
	 * Return the offending value.
	 */
	@Nullable
	public Object getValue() {
		return this.value;
	}

}
