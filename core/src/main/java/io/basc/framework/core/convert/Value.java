package io.basc.framework.core.convert;

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Value<T> implements Optional<T, ConversionException> {
	private final Object value;
	private TypeDescriptor typeDescriptor;
	private Converter<? super Object, ? extends Object, ? extends ConversionException> converter;

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor == null ? TypeDescriptor.forObject(value) : typeDescriptor;
	}

	public Source getSource() {
		ObjectValue source = new ObjectValue(value);
		source.setTypeDescriptor(typeDescriptor);
		source.setConverter(converter);
		return source;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
			throws ConversionException, X {
		Object value = this.getValue();
		TypeDescriptor valueTypeDescriptor = this.getTypeDescriptor();
		while (true) {
			if (value == null) {
				return mapper.apply(null);
			}

			Class<?> rawClass = valueTypeDescriptor.getType();
			if (rawClass == Object.class || rawClass == null || Value.class.isAssignableFrom(rawClass)
					|| valueTypeDescriptor.isAssignableTo(valueTypeDescriptor)) {
				return mapper.apply((T) value);
			}

			if (converter.canConvert(valueTypeDescriptor, this.typeDescriptor)) {
				value = converter.convert(value, valueTypeDescriptor, this.typeDescriptor);
				return mapper.apply((T) value);
			}

			if (value instanceof Source) {
				Source source = (Source) value;
				Value<T> v = (Value<T>) source.getAsValue(this.typeDescriptor, converter);
				return v.apply(mapper);
			}
			break;
		}
		return mapper.apply(null);
	}
}
