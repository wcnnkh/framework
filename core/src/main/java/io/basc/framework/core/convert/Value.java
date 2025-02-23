package io.basc.framework.core.convert;

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Optional;
import lombok.Data;
import lombok.NonNull;

@Data
public class Value<T> implements Optional<T, ConversionException> {
	private Converter<? super Object, ? extends Object, ? extends ConversionException> converter;
	private Function<? super Object, ? extends T, ? extends ConversionException> mapper;
	private TypeDescriptor typeDescriptor;
	private Object object;

	@Override
	public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
			throws ConversionException, X {
		T object = getValue();
		return mapper.apply(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Value<R> map(@NonNull Function<? super T, ? extends R, ? extends ConversionException> mapper) {
		Value<R> value = new Value<>();
		value.setObject(this);
		value.setMapper((Function<Object, ? extends R, ? extends ConversionException>) mapper);
		return value;
	}

	public Source any() {
		Target value = new Target();
		value.setObject(this);
		value.setConverter(this.converter);
		return value;
	}

	@SuppressWarnings("unchecked")
	public T getValue() {
		Object value = this.getObject();
		TypeDescriptor valueTypeDescriptor = this.getTypeDescriptor();
		if (value == null) {
			return mapper == null ? null : mapper.apply(null);
		}

		Class<?> rawClass = valueTypeDescriptor.getType();
		if (rawClass == Object.class || rawClass == null || Value.class.isAssignableFrom(rawClass)
				|| valueTypeDescriptor.isAssignableTo(valueTypeDescriptor)) {
			return mapper == null ? (T) value : mapper.apply(value);
		}
		if (converter != null && converter.canConvert(valueTypeDescriptor, this.typeDescriptor)) {
			value = converter.convert(value, valueTypeDescriptor, this.typeDescriptor);
			return mapper == null ? (T) value : mapper.apply(value);
		}
		if (value instanceof Source) {
			Source source = (Source) value;
			return (T) source.map(valueTypeDescriptor, this.converter).get();
		}
		return mapper == null ? null : mapper.apply(null);
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor == null ? TypeDescriptor.forObject(object) : typeDescriptor;
	}
}
