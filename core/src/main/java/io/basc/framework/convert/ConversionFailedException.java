package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;

public class ConversionFailedException extends ConversionException {
	private static final long serialVersionUID = 1L;

	@Nullable
	private final TypeDescriptor sourceType;

	private final TypeDescriptor targetType;

	@Nullable
	private final Object value;

	public ConversionFailedException(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType,
			@Nullable Object value, Throwable cause) {

		super("Failed to convert from type [" + sourceType + "] to type [" + targetType + "] for value '"
				+ ObjectUtils.toString(value) + "'", cause);
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.value = value;
	}

	@Nullable
	public TypeDescriptor getSourceType() {
		return this.sourceType;
	}

	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

	@Nullable
	public Object getValue() {
		return this.value;
	}

}
