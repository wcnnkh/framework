package io.basc.framework.core.convert;

import io.basc.framework.util.ObjectUtils;

public class ConversionFailedException extends ConversionException {
	private static final long serialVersionUID = 1L;

	private final TypeDescriptor sourceType;

	private final TypeDescriptor targetType;

	private final Object value;

	public ConversionFailedException(TypeDescriptor sourceType, TypeDescriptor targetType, Object value,
			Throwable cause) {

		super("Failed to convert from type [" + sourceType + "] to type [" + targetType + "] for value '"
				+ ObjectUtils.toString(value) + "'", cause);
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.value = value;
	}

	public TypeDescriptor getSourceType() {
		return this.sourceType;
	}

	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

	public Object getValue() {
		return this.value;
	}

}
