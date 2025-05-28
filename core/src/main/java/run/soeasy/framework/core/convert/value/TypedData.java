package run.soeasy.framework.core.convert.value;

import java.util.function.Supplier;

public interface TypedData<T> extends SourceDescriptor, Supplier<T> {
	default TypedValue value() {
		ConvertingValue<AccessibleDescriptor> value = new ConvertingValue<>(
				AccessibleDescriptor.forTypeDescriptor(getReturnTypeDescriptor()));
		value.setValue(this);
		return value;
	}
}
