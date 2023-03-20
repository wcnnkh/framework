package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public class ConverterNotFoundException extends ConversionException {
	private static final long serialVersionUID = 1L;

	@Nullable
	private final TypeDescriptor sourceType;

	private final TypeDescriptor targetType;

	public ConverterNotFoundException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		super("No converter found capable of converting from type [" + sourceType + "] to type [" + targetType + "]");
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	@Nullable
	public TypeDescriptor getSourceType() {
		return this.sourceType;
	}

	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

}
