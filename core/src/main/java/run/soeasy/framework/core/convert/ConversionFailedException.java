package run.soeasy.framework.core.convert;

import run.soeasy.framework.core.ObjectUtils;

public class ConversionFailedException extends ConversionException {
	private static final long serialVersionUID = 1L;

	private final TypeDescriptor targetType;

	private final TypedValue value;

	public ConversionFailedException(TypedValue value, TypeDescriptor targetType, Throwable cause) {
		super("Failed to convert from type [" + value.getReturnTypeDescriptor() + "] to type [" + targetType
				+ "] for value '" + ObjectUtils.toString(value) + "'", cause);
		this.targetType = targetType;
		this.value = value;
	}

	public ConversionFailedException(TypeDescriptor sourceType, TypeDescriptor targetType, Object value,
			Throwable cause) {
		super("Failed to convert from type [" + sourceType + "] to type [" + targetType + "] for value '"
				+ ObjectUtils.toString(value) + "'", cause);
		this.targetType = targetType;
		this.value = TypedValue.of(value, sourceType);
	}

	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

	public TypedValue getValue() {
		return this.value;
	}

}
