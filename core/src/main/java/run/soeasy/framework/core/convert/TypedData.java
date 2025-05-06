package run.soeasy.framework.core.convert;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.function.ThrowingSupplier;

public interface TypedData<T> extends SourceDescriptor, ThrowingSupplier<T, ConversionException> {
	public static interface TypedDataWrapper<T, W extends TypedData<T>>
			extends TypedData<T>, SourceDescriptorWrapper<W>, ThrowingSupplierWrapper<T, ConversionException, W> {
		@Override
		default <R> TypedData<R> map(@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default TypedValue value() {
			return getSource().value();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	default <R> TypedData<R> map(@NonNull ThrowingFunction<? super T, ? extends R, ConversionException> mapper) {
		ConvertingData<R, AccessibleDescriptor> converting = new ConvertingData<R, AccessibleDescriptor>(
				AccessibleDescriptor.forTypeDescriptor(getReturnTypeDescriptor()));
		converting.setValue(this);
		converting.setMapper((a, b) -> mapper.apply((T) a.get()));
		return converting;
	}

	default TypedValue value() {
		ConvertingValue<AccessibleDescriptor> value = new ConvertingValue<>(
				AccessibleDescriptor.forTypeDescriptor(getReturnTypeDescriptor()));
		value.setValue(this);
		return value;
	}
}
