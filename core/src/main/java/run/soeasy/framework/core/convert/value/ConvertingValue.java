package run.soeasy.framework.core.convert.value;

import lombok.NonNull;

public class ConvertingValue<W extends AccessibleDescriptor> extends ConvertingData<Object, W>
		implements TypedValueAccessor {
	private static final long serialVersionUID = 1L;

	public ConvertingValue(@NonNull W source) {
		super(source);
	}

	@Override
	public ConvertingValue<W> value() {
		return this;
	}
}
