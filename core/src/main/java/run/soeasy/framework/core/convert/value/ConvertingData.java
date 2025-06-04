package run.soeasy.framework.core.convert.value;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor.AccessibleDescriptorWrapper;

@Data
public class ConvertingData<T, W extends AccessibleDescriptor>
		implements TypedDataAccessor<T>, AccessibleDescriptorWrapper<W>, Serializable {
	private static final long serialVersionUID = 1L;
	private Converter<? super Object, ? extends Object> converter;
	@NonNull
	private final W source;
	private Object value;

	@SuppressWarnings("unchecked")
	@Override
	public T get() throws ConversionException {
		return (T) convert(this.value, source.getReturnTypeDescriptor());
	}

	@Override
	public void set(T value) {
		this.value = convert(value, source.getRequiredTypeDescriptor());
	}

	protected Object convert(Object value, TypeDescriptor targetTypeDescriptor) {
		Object result;
		if (value instanceof TypedData) {
			TypedValue typedValue = (TypedValue) value;
			if (converter != null) {
				result = converter.convert(typedValue.get(), typedValue.getReturnTypeDescriptor(),
						targetTypeDescriptor);
			} else {
				result = ((TypedValue) value).getAsObject(targetTypeDescriptor);
			}
		} else {
			if (converter == null) {
				result = converter.convert(value, TypeDescriptor.forObject(value), targetTypeDescriptor);
			} else {
				result = value;
			}
		}
		return result;
	}
}