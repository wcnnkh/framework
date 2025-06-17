package run.soeasy.framework.core.convert.value;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;

public interface TypedValueAccessor extends TypedValue, TypedDataAccessor<Object> {
	@Override
	default TypedValue getAsValue(@NonNull Converter converter) {
		ConvertingValue<AccessibleDescriptor> converting = new ConvertingValue<AccessibleDescriptor>(this);
		converting.setValue(this);
		converting.setConverter(converter);
		return converting;
	}
}
