package run.soeasy.framework.core.convert;

import lombok.Getter;
import run.soeasy.framework.core.ObjectUtils;

@Getter
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
}
