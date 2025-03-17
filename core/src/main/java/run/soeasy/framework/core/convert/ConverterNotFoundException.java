package run.soeasy.framework.core.convert;

public class ConverterNotFoundException extends ConversionException {
	private static final long serialVersionUID = 1L;

	private final TypeDescriptor sourceType;

	private final TypeDescriptor targetType;

	public ConverterNotFoundException(TypeDescriptor sourceType, TypeDescriptor targetType) {
		super("No converter found capable of converting from type [" + sourceType + "] to type [" + targetType + "]");
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	public TypeDescriptor getSourceType() {
		return this.sourceType;
	}

	public TypeDescriptor getTargetType() {
		return this.targetType;
	}

}
