package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;

public interface TypedValueAccessor extends TypedValue, TypedDataAccessor<Object> {
	public static interface TypedValueAccessorWrapper<W extends TypedValueAccessor>
			extends TypedValueAccessor, TypedValueWrapper<W>, TypedDataAccessorWrapper<Object, W> {
		@Override
		default TypedValue getAsValue(
				@NonNull Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
			return getSource().getAsValue(converter);
		}
	}

	@Override
	default TypedValue getAsValue(
			@NonNull Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
		ConvertingValue<AccessibleDescriptor> converting = new ConvertingValue<AccessibleDescriptor>(this);
		converting.setValue(this);
		converting.setConverter(converter);
		return converting;
	}
}
