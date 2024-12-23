package io.basc.framework.core.convert;

import io.basc.framework.util.ObjectUtils;

public class ConversionFailedException extends ConversionException {
	private static final long serialVersionUID = 1L;

	private final TypeDescriptor targetType;

	private final Value value;

	public ConversionFailedException(Value value, TypeDescriptor targetType, Throwable cause) {
		super("Failed to convert from type [" + value.getTypeDescriptor() + "] to type [" + targetType + "] for value '"
				+ ObjectUtils.toString(value) + "'", cause);
		this.targetType = targetType;
		this.value = value;
	}

	public ConversionFailedException(TypeDescriptor sourceType, TypeDescriptor targetType, Object value,
			Throwable cause) {
		super("Failed to convert from type [" + sourceType + "] to type [" + targetType + "] for value '"
				+ ObjectUtils.toString(value) + "'", cause);
		this.targetType = targetType;
		this.value = Value.of(value, sourceType);
	}

	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

	public Value getValue() {
		return this.value;
	}

}
