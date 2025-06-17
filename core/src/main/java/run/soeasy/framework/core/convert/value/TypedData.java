package run.soeasy.framework.core.convert.value;

import java.util.function.Supplier;

import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TypedData<T> extends SourceDescriptor, Supplier<T> {
	default TypedValue value() {
		ConvertingValue<AccessibleDescriptor> value = new ConvertingValue<>(
				AccessibleDescriptor.forTypeDescriptor(getReturnTypeDescriptor()));
		value.setValue(this);
		return value;
	}

	public static <V> TypedData<V> forValue(V value, TypeDescriptor typeDescriptor) {
		CustomizeTypedDataAccessor<V> dataAccessor = new CustomizeTypedDataAccessor<>();
		dataAccessor.set(value);
		dataAccessor.setTypeDescriptor(typeDescriptor);
		return dataAccessor;
	}
}
