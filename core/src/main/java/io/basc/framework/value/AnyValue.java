package io.basc.framework.value;

import java.lang.reflect.Array;
import java.util.Objects;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.StringConverter;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

public class AnyValue implements Value, Cloneable {
	private Converter<? super Object, ? super Object, ? extends RuntimeException> converter;
	private JsonSupport jsonSupport;
	private TypeDescriptor typeDescriptor;
	private Object value;
	private StringConverter stringConverter;

	public AnyValue(Object value) {
		this(value, null, null);
	}

	public AnyValue(Object value, Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		this(value, null, converter);
	}

	public AnyValue(Object value, TypeDescriptor type) {
		this(value, type, null);
	}

	public AnyValue(Object value, TypeDescriptor type,
			Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		setValue(value, type);
		setConverter(converter);
	}

	@Override
	public StringConverter getStringConverter() {
		return stringConverter == null ? Value.super.getStringConverter() : stringConverter;
	}

	public void setStringConverter(StringConverter stringConverter) {
		this.stringConverter = stringConverter;
	}

	@Override
	public AnyValue clone() {
		AnyValue value = new AnyValue(this.value, this.typeDescriptor, this.converter);
		value.jsonSupport = this.jsonSupport;
		value.stringConverter = this.stringConverter;
		return value;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (converter != null) {
			if (converter instanceof ConversionService) {
				return ((ConversionService) converter).canConvert(sourceType, targetType);
			}

			return Value.super.canConvert(sourceType, targetType);
		}

		return !Value.class.isAssignableFrom(sourceType.getType()) && Value.super.canConvert(sourceType, targetType);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (isSupportAsArray(sourceType) && targetType.isArray()) {
			return getAsArray(targetType.getElementTypeDescriptor());
		}

		Converter<? super Object, ? super Object, ? extends RuntimeException> converter = getConverter();
		if (converter instanceof ConversionService) {
			if (!((ConversionService) converter).canConvert(sourceType, targetType)) {
				return convertInternal(source, sourceType, targetType, null);
			}
		}

		try {
			return converter.convert(source, sourceType, targetType);
		} catch (ConversionException e) {
			return convertInternal(source, sourceType, targetType, e);
		}
	}

	private Object convertInternal(Object source, TypeDescriptor sourceType, TypeDescriptor targetType,
			ConversionException exception) {
		if (source instanceof String) {
			return getJsonSupport().convert(source, sourceType, targetType);
		}

		if (Value.isBaseType(targetType.getType())) {
			return getAsObject(targetType.getType());
		}

		throw new ConversionFailedException(sourceType, targetType, source, exception);
	}

	@Override
	public boolean equals(Object obj) {
		if (value == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof AnyValue) {
			return ObjectUtils.equals(value, ((AnyValue) obj).get());
		}

		return false;
	}

	public <E> E[] getAsArray(Class<E> componentType) {
		return getAsArray(TypeDescriptor.valueOf(componentType));
	}

	@SuppressWarnings("unchecked")
	public <E> E[] getAsArray(TypeDescriptor componentType) {
		String[] values = split(getAsString());
		if (values == null) {
			return null;
		}

		Object array = Array.newInstance(componentType.getType(), values.length);
		for (int i = 0; i < values.length; i++) {
			AnyValue value = clone();
			value.setValue(values[i]);
			if (value.isPresent()) {
				Array.set(array, i, value.getAsObject(componentType));
			}
		}
		return (E[]) array;
	}

	public Converter<? super Object, ? super Object, ? extends RuntimeException> getConverter() {
		return converter == null ? Sys.getEnv().getConversionService() : converter;
	}

	public JsonSupport getJsonSupport() {
		return jsonSupport == null ? JsonUtils.getSupport() : jsonSupport;
	}

	@Override
	public Object getSource() {
		return value;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor != null) {
			return typeDescriptor;
		}

		return Value.super.getTypeDescriptor();
	}

	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}

	public boolean isSupportAsArray(TypeDescriptor type) {
		if (type == null) {
			return false;
		}

		return type.getType() == String.class;
	}

	public void setConverter(Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		this.converter = converter;
	}

	public void setJsonSupport(JsonSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public void setValue(Object value) {
		setValue(value, null);
	}

	public void setValue(Object value, TypeDescriptor type) {
		this.value = value;
		this.typeDescriptor = type;
	}

	public String[] split(String value) {
		return StringUtils.splitToArray(value);
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}

	@Override
	public Value transform(Object value, TypeDescriptor type) {
		AnyValue anyValue = clone();
		anyValue.setValue(value, type);
		return anyValue;
	}
}
