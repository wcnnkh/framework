package run.soeasy.framework.core.convert;

import java.util.function.Supplier;

public interface TypedData<T> extends SourceDescriptor, Supplier<T> {
	public static interface TypedDataWrapper<T, W extends TypedData<T>>
			extends TypedData<T>, SourceDescriptorWrapper<W> {

		@Override
		default TypedValue value() {
			return getSource().value();
		}

		@Override
		default T get() {
			return getSource().get();
		}
	}

	default TypedValue value() {
		ConvertingValue<AccessibleDescriptor> value = new ConvertingValue<>(
				AccessibleDescriptor.forTypeDescriptor(getReturnTypeDescriptor()));
		value.setValue(this);
		return value;
	}
}
