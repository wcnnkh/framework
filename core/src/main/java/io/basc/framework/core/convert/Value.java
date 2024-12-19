package io.basc.framework.core.convert;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Enumerable;
import io.basc.framework.util.NumberUtils;
import io.basc.framework.util.Source;
import io.basc.framework.util.Any;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@FunctionalInterface
public interface Value extends ValueDescriptor, Any, Source<Object, ConversionException> {

	@FunctionalInterface
	public static interface ValueWrapper<W extends Value>
			extends Value, ValueDescriptorWrapper<W>, AnyWrapper<W>, SourceWrapper<Object, ConversionException, W> {

		@Override
		default Object get() throws ConversionException {
			return getSource().get();
		}

		@Override
		default <T> T getAsArray(Class<? extends T> arrayType) {
			return getSource().getAsArray(arrayType);
		}

		@Override
		default BigDecimal getAsBigDecimal() {
			return getSource().getAsBigDecimal();
		}

		@Override
		default BigInteger getAsBigInteger() {
			return getSource().getAsBigInteger();
		}

		@Override
		default boolean getAsBoolean() {
			return getSource().getAsBoolean();
		}

		@Override
		default byte getAsByte() {
			return getSource().getAsByte();
		}

		@Override
		default char getAsChar() {
			return getSource().getAsChar();
		}

		@Override
		default CharSequence getAsCharSequence() {
			return getSource().getAsCharSequence();
		}

		@Override
		default double getAsDouble() {
			return getSource().getAsDouble();
		}

		@Override
		default Elements<? extends Value> getAsElements() {
			return getSource().getAsElements();
		}

		@Override
		default <T, E extends Throwable> Elements<T> getAsElements(Class<? extends T> componentType) {
			return getSource().getAsElements(componentType);
		}

		@Override
		default <T, E extends Throwable> Elements<T> getAsElements(Class<? extends T> componentType,
				Supplier<? extends T> defaultSupplier) {
			return getSource().getAsElements(componentType, defaultSupplier);
		}

		@Override
		default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
			return getSource().getAsEnum(enumType);
		}

		@Override
		default float getAsFloat() {
			return getSource().getAsFloat();
		}

		@Override
		default int getAsInt() {
			return getSource().getAsInt();
		}

		@Override
		default long getAsLong() {
			return getSource().getAsLong();
		}

		@Override
		default Number getAsNumber() {
			return getSource().getAsNumber();
		}

		@Override
		default <T> T getAsObject(Class<? extends T> type) {
			return getSource().getAsObject(type);
		}

		@Override
		default <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
			return getSource().getAsObject(requiredType, defaultSupplier);
		}

		@Override
		default Object getAsObject(ResolvableType type) {
			return getSource().getAsObject(type);
		}

		@Override
		default Object getAsObject(Type type) {
			return getSource().getAsObject(type);
		}

		@Override
		default Object getAsObject(TypeDescriptor type) {
			return getSource().getAsObject(type);
		}

		@Override
		default <E extends Throwable> Object getAsObject(TypeDescriptor targetType,
				@NonNull Converter<? super Object, ? extends Object, E> converter) throws E {
			return getSource().getAsObject(targetType, converter);
		}

		@Override
		default short getAsShort() {
			return getSource().getAsShort();
		}

		@Override
		default String getAsString() {
			return getSource().getAsString();
		}

		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}

		@Override
		default boolean isMultiple() {
			return getSource().isMultiple();
		}

		@Override
		default boolean isNumber() {
			return getSource().isNumber();
		}
	}

	public static class EmptyValue implements Value, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Object get() throws ConversionException {
			return null;
		}
	}

	@AllArgsConstructor
	@Setter
	@Getter
	public static class ObjectValue implements Value, Cloneable, Serializable {
		private static final long serialVersionUID = 1L;
		private Object value;
		private TypeDescriptor typeDescriptor;

		public ObjectValue(Object source) {
			this(source, null);
		}

		@Override
		public ObjectValue clone() {
			return new ObjectValue(value, typeDescriptor);
		}

		@Override
		public TypeDescriptor getTypeDescriptor() {
			if (typeDescriptor != null) {
				return typeDescriptor;
			}
			return Value.super.getTypeDescriptor();
		}

		public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
			this.typeDescriptor = typeDescriptor;
		}

		@Override
		public Object get() throws ConversionException {
			return value;
		}
	}

	static final Value EMPTY = new EmptyValue();

	static final Value[] EMPTY_ARRAY = new Value[0];

	static Value of(Object value) {
		return of(value, null);
	}

	static Value of(Object value, TypeDescriptor type) {
		if (value == null && type == null) {
			return EMPTY;
		}

		if (type == null && value instanceof Value) {
			return (Value) value;
		}

		return new ObjectValue(value, type);
	}

	default BigDecimal getAsBigDecimal() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}

		if (value instanceof Any) {
			return ((Any) value).getAsBigDecimal();
		}

		if (value instanceof Number) {
			return new BigDecimal(((Number) value).doubleValue());
		}
		return (BigDecimal) getAsObject(TypeDescriptor.valueOf(BigDecimal.class), Converter.unsupported());
	}

	default BigInteger getAsBigInteger() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}

		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).toBigInteger();
		}

		if (value instanceof Any) {
			return ((Any) value).getAsBigInteger();
		}

		return (BigInteger) getAsObject(TypeDescriptor.valueOf(BigInteger.class), Converter.unsupported());
	}

	default boolean getAsBoolean() {
		Object value = get();
		if (value == null) {
			return false;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsBoolean();
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}
		return (boolean) getAsObject(TypeDescriptor.valueOf(boolean.class), Converter.unsupported());
	}

	default byte getAsByte() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Byte) {
			return (Byte) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsByte();
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		return (byte) getAsObject(TypeDescriptor.valueOf(byte.class), Converter.unsupported());
	}

	default char getAsChar() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsChar();
		}

		return (char) getAsObject(TypeDescriptor.valueOf(char.class), Converter.unsupported());
	}

	@Override
	default CharSequence getAsCharSequence() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof CharSequence) {
			return (CharSequence) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsCharSequence();
		}

		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}

		return (String) getAsObject(TypeDescriptor.valueOf(CharSequence.class), Converter.unsupported());
	}

	@Override
	default double getAsDouble() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Double) {
			return (Double) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsDouble();
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		return (double) getAsObject(TypeDescriptor.valueOf(double.class), Converter.unsupported());
	}

	@Override
	default Elements<? extends Value> getAsElements() {
		Object value = get();
		TypeDescriptor typeDescriptor = getTypeDescriptor();
		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(collection).map((v) -> Value.of(v, elementTypeDescriptor));
		} else if (value instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(iterable).map((v) -> Value.of(v, elementTypeDescriptor));
		} else if (value instanceof Enumerable) {
			Enumerable<?> enumerable = (Enumerable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(enumerable).map((v) -> Value.of(v, elementTypeDescriptor));
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(() -> IntStream.range(0, len)
					.mapToObj((index) -> Value.of(Array.get(value, index), elementTypeDescriptor)));
		}
		return Elements.singleton(Value.of(value, typeDescriptor));
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof Enum<?>) {
			return (T) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsEnum(enumType);
		}

		return (T) getAsObject(TypeDescriptor.valueOf(enumType), Converter.unsupported());
	}

	default float getAsFloat() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Float) {
			return (Float) value;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsFloat();
		}

		return (float) getAsObject(TypeDescriptor.valueOf(float.class), Converter.unsupported());
	}

	@Override
	default int getAsInt() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsInt();
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		return (int) getAsObject(TypeDescriptor.valueOf(int.class), Converter.unsupported());
	}

	@Override
	default long getAsLong() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Long) {
			return (Long) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsLong();
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		return (long) getAsObject(TypeDescriptor.valueOf(long.class), Converter.unsupported());
	}

	default Number getAsNumber() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return (Number) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsNumber();
		}

		return (Number) getAsObject(TypeDescriptor.valueOf(Number.class), Converter.unsupported());
	}

	@SuppressWarnings("unchecked")
	default <T> T getAsObject(Class<? extends T> type) {
		return (T) getAsObject(type, () -> getAsObject(TypeDescriptor.valueOf(type), Converter.unsupported()));
	}

	default Object getAsObject(ResolvableType type) {
		return getAsObject(TypeDescriptor.valueOf(type));
	}

	default Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class<?>) type);
		}
		return getAsObject(TypeDescriptor.valueOf(type));
	}

	default Object getAsObject(TypeDescriptor type) {
		return getAsObject(type.getType(), () -> getAsObject(type, Converter.unsupported()));
	}

	/**
	 * 所有转换的基类
	 * 
	 * @param <E>
	 * @param targetType
	 * @param converter
	 * @return
	 * @throws E
	 */
	default <E extends Throwable> Object getAsObject(TypeDescriptor targetType,
			@NonNull Converter<? super Object, ? extends Object, E> converter) throws E {
		Object source = get();
		if (source == null) {
			return null;
		}

		Class<?> rawClass = targetType.getType();
		if (rawClass == Object.class || rawClass == null) {
			return source;
		}

		TypeDescriptor sourceType = getTypeDescriptor();
		while (true) {
			if (converter.canConvert(sourceType, targetType)) {
				return converter.convert(source, sourceType, targetType);
			}

			if (source instanceof Value) {
				source = ((Value) source).get();
				sourceType = ((Value) source).getTypeDescriptor();
			}
			break;
		}
		throw new ConversionFailedException(getTypeDescriptor(), targetType, get(), null);
	}

	default short getAsShort() {
		Object value = get();
		if (value == null) {
			return 0;
		}

		if (value instanceof Short) {
			return (Short) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsShort();
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		return (short) getAsObject(TypeDescriptor.valueOf(short.class), Converter.unsupported());
	}

	default String getAsString() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof Any) {
			return ((Any) value).getAsString();
		}

		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}

		return (String) getAsObject(TypeDescriptor.valueOf(String.class), Converter.unsupported());
	}

	default TypeDescriptor getTypeDescriptor() {
		Object value = get();
		if (value == null) {
			return TypeDescriptor.valueOf(Object.class);
		}
		return TypeDescriptor.forObject(value);
	}

	@Override
	default boolean isMultiple() {
		TypeDescriptor typeDescriptor = getTypeDescriptor();
		return typeDescriptor.isCollection() || typeDescriptor.isArray()
				|| Iterable.class.isAssignableFrom(typeDescriptor.getType())
				|| Enumerable.class.isAssignableFrom(typeDescriptor.getType());
	}

	/**
	 * 是否可以转换为number,此方法不代表数据的原始类型是number
	 * 
	 * @see #getAsNumber()
	 * @return
	 */
	default boolean isNumber() {
		TypeDescriptor typeDescriptor = getTypeDescriptor();
		if (NumberUtils.isNumber(typeDescriptor.getType())) {
			return true;
		}

		Object value = get();
		if (value instanceof Number) {
			return true;
		}

		if (value instanceof Any) {
			return ((Any) value).isNumber();
		}

		try {
			getAsNumber();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
