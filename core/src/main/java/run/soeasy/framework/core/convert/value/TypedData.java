package run.soeasy.framework.core.convert.value;

import java.util.function.Supplier;

import run.soeasy.framework.core.convert.TypeDescriptor;

public interface TypedData<T> extends SourceDescriptor, Supplier<T> {
	default TypedValue value() {
		CustomizeTypedValueAccessor typedValueAccessor = new CustomizeTypedValueAccessor();
		typedValueAccessor.set(get());
		typedValueAccessor.setTypeDescriptor(getReturnTypeDescriptor());
		return typedValueAccessor;
	}

	public static <V> TypedData<V> forValue(V value) {
		return forValue(value, null);
	}

	public static <V> TypedData<V> forValue(V value, TypeDescriptor typeDescriptor) {
		CustomizeTypedDataAccessor<V> dataAccessor = new CustomizeTypedDataAccessor<>();
		dataAccessor.set(value);
		dataAccessor.setTypeDescriptor(typeDescriptor);
		return dataAccessor;
	}
}
