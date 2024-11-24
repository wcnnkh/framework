package io.basc.framework.core.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.stream.IntStream;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.convert.lang.EmptyValue;
import io.basc.framework.core.convert.lang.ObjectValue;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Enumerable;
import io.basc.framework.util.Value;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.function.Optional;
import io.basc.framework.util.function.Source;
import lombok.NonNull;

public interface ValueWrapper extends Value, Optional<ValueWrapper>, Wrapper<Object> {
	static final ValueWrapper EMPTY = new EmptyValue();

	static final ValueWrapper[] EMPTY_ARRAY = new ValueWrapper[0];

	static Elements<ValueWrapper> asElements(Object value, TypeDescriptor typeDescriptor) {
		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(collection).map((v) -> ValueWrapper.of(v, elementTypeDescriptor));
		} else if (value instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(iterable).map((v) -> ValueWrapper.of(v, elementTypeDescriptor));
		} else if (value instanceof Enumerable) {
			Enumerable<?> enumerable = (Enumerable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(enumerable).map((v) -> ValueWrapper.of(v, elementTypeDescriptor));
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(() -> IntStream.range(0, len)
					.mapToObj((index) -> ValueWrapper.of(Array.get(value, index), elementTypeDescriptor)));
		} /*
			 * else if (value instanceof Iterator) { Iterator<?> iterator = (Iterator<?>)
			 * value; TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			 * return Elements.of(() -> iterator).map((v) -> Value.of(v,
			 * elementTypeDescriptor)); } else if (value instanceof Enumeration) {
			 * Enumeration<?> enumeration = (Enumeration<?>) value; TypeDescriptor
			 * elementTypeDescriptor = typeDescriptor.getGeneric(0); return Elements.of(()
			 * -> enumeration).map((v) -> Value.of(v, elementTypeDescriptor)); }
			 */
		return Elements.singleton(ValueWrapper.of(value, typeDescriptor));
	}

	/**
	 * 这并不是指基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Type type) {
		return isUnconvertibleType(type) || Number.class == type;
	}

	static boolean isElements(TypeDescriptor typeDescriptor) {
		return typeDescriptor.isCollection() || typeDescriptor.isArray()
				|| Iterable.class.isAssignableFrom(typeDescriptor.getType())
				|| Enumerable.class.isAssignableFrom(typeDescriptor.getType());
	}

	/**
	 * 不可以发生转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isUnconvertibleType(Type type) {
		if (type == null) {
			return false;
		}

		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isEnum()) {
				return true;
			}
		}

		return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type == BigDecimal.class
				|| type == BigInteger.class || type == Class.class;
	}

	static ValueWrapper of(Object value) {
		return of(value, null);
	}

	static ValueWrapper of(Object value, TypeDescriptor type) {
		if (value == null && type == null) {
			return EMPTY;
		}

		if (type == null && value instanceof ValueWrapper) {
			return (ValueWrapper) value;
		}

		return new ObjectValue(value, type);
	}

	default <T> Optional<T> as(Class<? extends T> type) {
		return map((e) -> getAsObject(type));
	}

	default BigDecimal getAsBigDecimal() {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBigDecimal();
		}

		if (value instanceof Number) {
			return new BigDecimal(((Number) value).doubleValue());
		}
		return (BigDecimal) getAsObject(TypeDescriptor.valueOf(BigDecimal.class), Converter.unsupported());
	}

	default BigInteger getAsBigInteger() {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}

		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).toBigInteger();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBigInteger();
		}

		return (BigInteger) getAsObject(TypeDescriptor.valueOf(BigInteger.class), Converter.unsupported());
	}

	default boolean getAsBoolean() {
		Object value = getSource();
		if (value == null) {
			return false;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBoolean();
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}
		return (boolean) getAsObject(TypeDescriptor.valueOf(boolean.class), Converter.unsupported());
	}

	default byte getAsByte() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Byte) {
			return (Byte) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsByte();
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		return (byte) getAsObject(TypeDescriptor.valueOf(byte.class), Converter.unsupported());
	}

	default char getAsChar() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsChar();
		}

		return (char) getAsObject(TypeDescriptor.valueOf(char.class), Converter.unsupported());
	}

	@Override
	default double getAsDouble() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Double) {
			return (Double) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsDouble();
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		return (double) getAsObject(TypeDescriptor.valueOf(double.class), Converter.unsupported());
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof Enum<?>) {
			return (T) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsEnum(enumType);
		}

		return (T) getAsObject(TypeDescriptor.valueOf(enumType), Converter.unsupported());
	}

	default float getAsFloat() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Float) {
			return (Float) value;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof ValueWrapper) {
			return ((ValueWrapper) value).getAsFloat();
		}

		return (float) getAsObject(TypeDescriptor.valueOf(float.class), Converter.unsupported());
	}

	@Override
	default int getAsInt() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsInt();
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		return (int) getAsObject(TypeDescriptor.valueOf(int.class), Converter.unsupported());
	}

	@Override
	default long getAsLong() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Long) {
			return (Long) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsLong();
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		return (long) getAsObject(TypeDescriptor.valueOf(long.class), Converter.unsupported());
	}

	default Number getAsNumber() {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return (Number) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsNumber();
		}

		return (Number) getAsObject(TypeDescriptor.valueOf(Number.class), Converter.unsupported());
	}

	@SuppressWarnings("unchecked")
	default <T> T getAsObject(Class<? extends T> type) {
		if (!isPresent() && !ClassUtils.isPrimitive(type)) {
			return null;
		}

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

	default <E extends Throwable> Object getAsObject(TypeDescriptor targetType,
			@NonNull Converter<? super Object, ? extends Object, E> converter) throws E {
		Object source = getSource();
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

			if (source instanceof ValueWrapper) {
				source = ((ValueWrapper) source).getSource();
				sourceType = ((ValueWrapper) source).getTypeDescriptor();
			}
			break;
		}
		throw new ConversionFailedException(getTypeDescriptor(), targetType, getSource(), null);
	}

	default short getAsShort() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Short) {
			return (Short) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsShort();
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		return (short) getAsObject(TypeDescriptor.valueOf(short.class), Converter.unsupported());
	}

	default String getAsString() {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsString();
		}

		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}

		return (String) getAsObject(TypeDescriptor.valueOf(String.class), Converter.unsupported());
	}

	default TypeDescriptor getTypeDescriptor() {
		Object value = getSource();
		if (value == null) {
			return TypeDescriptor.valueOf(Object.class);
		}
		return TypeDescriptor.forObject(value);
	}

	/**
	 * 是否可以转换为number,此方法不代表数据的原始类型是number
	 * 
	 * @see #getAsNumber()
	 * @return
	 */
	default boolean isNumber() {
		// TODO 使用类型判断
		Object value = getSource();
		if (value instanceof Number) {
			return true;
		}

		if (value instanceof Value) {
			return ((Value) value).isNumber();
		}

		try {
			getAsNumber();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 是否存在
	 * 
	 * @return
	 */
	default boolean isPresent() {
		Object value = getSource();
		if (value == null) {
			return false;
		}

		if (value instanceof ValueWrapper) {
			return ((ValueWrapper) value).isPresent();
		}
		return true;
	}

	default ValueWrapper or(Object other) {
		return orElse(ValueWrapper.of(other));
	}

	@Override
	default ValueWrapper orElse(ValueWrapper other) {
		return isPresent() ? this : other;
	}

	default <E extends Throwable> ValueWrapper orGet(Source<? extends Object, ? extends E> other) throws E {
		return orElseGet(() -> ValueWrapper.of(other.get()));
	}
}
