package run.soeasy.framework.core.convert;

import java.io.Serializable;

import lombok.NonNull;
import run.soeasy.framework.core.function.Function;
import run.soeasy.framework.core.function.Optional;

@lombok.Data
public class Data<T> implements SourceDescriptor, Optional<T, ConversionException>, Serializable {
	private static final long serialVersionUID = 1L;
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
	public <R> Data<R> map(@NonNull Function<? super T, ? extends R, ? extends ConversionException> mapper) {
		Data<R> value = new Data<>();
		value.setObject(this);
		value.setMapper((Function<Object, ? extends R, ? extends ConversionException>) mapper);
		return value;
	}

	public Any any() {
		Any value = new Any();
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
		if (rawClass == Object.class || rawClass == null || Data.class.isAssignableFrom(rawClass)
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
