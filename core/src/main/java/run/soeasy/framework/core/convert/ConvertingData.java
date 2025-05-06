package run.soeasy.framework.core.convert;

import java.io.Serializable;
import java.util.function.BiFunction;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.AccessibleDescriptor.AccessibleDescriptorWrapper;

@Data
public class ConvertingData<T, W extends AccessibleDescriptor>
		implements TypedDataAccessor<T>, AccessibleDescriptorWrapper<W>, Serializable {
	private static final long serialVersionUID = 1L;
	private BiFunction<? super TypedValue, ? super TargetDescriptor, ? extends Object> mapper;
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
			if (mapper != null) {
				result = mapper.apply(typedValue, AccessibleDescriptor.forTypeDescriptor(targetTypeDescriptor));
			} else {
				result = ((TypedValue) value).getAsObject(targetTypeDescriptor);
			}
		} else {
			if (mapper == null) {
				TypedValue typedValue = TypedValue.of(value);
				result = mapper.apply(typedValue, AccessibleDescriptor.forTypeDescriptor(targetTypeDescriptor));
			} else {
				result = value;
			}
		}
		return result;
	}
}