package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;

public interface TypedValueAccessorWrapper<W extends TypedValueAccessor>
		extends TypedValueAccessor, TypedValueWrapper<W>, TypedDataAccessorWrapper<Object, W> {
	@Override
	default TypedValue getAsValue(@NonNull Converter converter) {
		return getSource().getAsValue(converter);
	}
}