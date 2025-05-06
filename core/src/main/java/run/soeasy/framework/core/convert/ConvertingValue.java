package run.soeasy.framework.core.convert;

import lombok.NonNull;

public final class ConvertingValue<W extends AccessibleDescriptor> extends ConvertingData<Object, AccessibleDescriptor>
		implements TypedValueAccessor {
	private static final long serialVersionUID = 1L;

	public ConvertingValue(@NonNull AccessibleDescriptor source) {
		super(source);
	}

	@Override
	public ConvertingValue<W> value() {
		return this;
	}
}
