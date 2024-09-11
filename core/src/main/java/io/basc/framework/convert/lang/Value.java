package io.basc.framework.convert.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.stream.IntStream;

import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Enumerable;
import io.basc.framework.util.Wrapper;
import io.basc.framework.util.function.Optional;
import io.basc.framework.util.function.Source;

public interface Value
		extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier, Optional<Value>, Wrapper<Object> {
	static final Value EMPTY = new EmptyValue();

	static final Value[] EMPTY_ARRAY = new Value[0];

	/**
	 * 这并不是指基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Type type) {
		return isUnconvertibleType(type) || Number.class == type;
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

	static Value of(@Nullable Object value) {
		return of(value, null);
	}

	static Value of(@Nullable Object value, @Nullable TypeDescriptor type) {
		if (value == null && type == null) {
			return EMPTY;
		}

		if (type == null && value instanceof Value) {
			return (Value) value;
		}

		return new ObjectValue(value, type);
	}

	static boolean isElements(TypeDescriptor typeDescriptor) {
		return typeDescriptor.isCollection() || typeDescriptor.isArray()
				|| Iterable.class.isAssignableFrom(typeDescriptor.getType())
				|| Enumerable.class.isAssignableFrom(typeDescriptor.getType());
	}

	static Elements<Value> asElements(Object value, TypeDescriptor typeDescriptor) {
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
		} /*
			 * else if (value instanceof Iterator) { Iterator<?> iterator = (Iterator<?>)
			 * value; TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			 * return Elements.of(() -> iterator).map((v) -> Value.of(v,
			 * elementTypeDescriptor)); } else if (value instanceof Enumeration) {
			 * Enumeration<?> enumeration = (Enumeration<?>) value; TypeDescriptor
			 * elementTypeDescriptor = typeDescriptor.getGeneric(0); return Elements.of(()
			 * -> enumeration).map((v) -> Value.of(v, elementTypeDescriptor)); }
			 */
		return Elements.singleton(Value.of(value, typeDescriptor));
	}

	@Nullable
	default <E extends Throwable> Object convert(TypeDescriptor targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		Assert.requiredArgument(converter != null, "converter");
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

			if (ValueConverter.getInstance().canConvert(sourceType, targetType)) {
				return ValueConverter.getInstance().convert(source, sourceType, targetType);
			}

			if (source instanceof Value) {
				source = ((Value) source).getSource();
				sourceType = ((Value) source).getTypeDescriptor();
			}
			break;
		}
		throw new ConversionFailedException(getTypeDescriptor(), targetType, getSource(), null);
	}

	@Nullable
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

		if (value instanceof Number) {
			return new BigDecimal(((Number) value).doubleValue());
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBigDecimal();
		}

		return (BigDecimal) convert(TypeDescriptor.valueOf(BigDecimal.class), Converter.unsupported());
	}

	@Nullable
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

		return (BigInteger) convert(TypeDescriptor.valueOf(BigInteger.class), Converter.unsupported());
	}

	default boolean getAsBoolean() {
		Object value = getSource();
		if (value == null) {
			return false;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBoolean();
		}
		return (boolean) convert(TypeDescriptor.valueOf(boolean.class), Converter.unsupported());
	}

	default byte getAsByte() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Byte) {
			return (Byte) value;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsByte();
		}

		return (byte) convert(TypeDescriptor.valueOf(byte.class), Converter.unsupported());
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

		return (char) convert(TypeDescriptor.valueOf(char.class), Converter.unsupported());
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

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsDouble();
		}

		return (double) convert(TypeDescriptor.valueOf(double.class), Converter.unsupported());
	}

	@Nullable
	default Enum<?> getAsEnum(Class<?> enumType) {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof Enum<?>) {
			return (Enum<?>) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsEnum(enumType);
		}

		return (Enum<?>) convert(TypeDescriptor.valueOf(enumType), Converter.unsupported());
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

		if (value instanceof Value) {
			return ((Value) value).getAsFloat();
		}

		return (float) convert(TypeDescriptor.valueOf(float.class), Converter.unsupported());
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

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsInt();
		}

		return (int) convert(TypeDescriptor.valueOf(int.class), Converter.unsupported());
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

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsLong();
		}

		return (long) convert(TypeDescriptor.valueOf(long.class), Converter.unsupported());
	}

	@Nullable
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

		return (Number) convert(TypeDescriptor.valueOf(Number.class), Converter.unsupported());
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

		if (value instanceof Property) {
			return ((Property) value).isPresent();
		}
		return true;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default <T> T getAsObject(Class<? extends T> type) {
		if (!ClassUtils.isPrimitive(type) && !isPresent()) {
			return null;
		}

		Object v = null;
		if (String.class == type) {
			v = getAsString();
		} else if (ClassUtils.isInt(type)) {
			v = getAsInt();
		} else if (ClassUtils.isLong(type)) {
			v = getAsLong();
		} else if (ClassUtils.isFloat(type)) {
			v = getAsFloat();
		} else if (ClassUtils.isDouble(type)) {
			v = getAsDouble();
		} else if (ClassUtils.isShort(type)) {
			v = getAsShort();
		} else if (ClassUtils.isBoolean(type)) {
			v = getAsBoolean();
		} else if (ClassUtils.isByte(type)) {
			v = getAsByte();
		} else if (ClassUtils.isChar(type)) {
			v = getAsChar();
		} else if (BigDecimal.class == type) {
			v = getAsBigDecimal();
		} else if (BigInteger.class == type) {
			v = getAsBigInteger();
		} else if (Number.class == type) {
			v = getAsNumber();
		} else if (type.isEnum()) {
			v = getAsEnum(type);
		} else if (type == Value.class) {
			v = this;
		} else {
			v = convert(TypeDescriptor.valueOf(type), Converter.unsupported());
		}
		return (T) v;
	}

	default Object getAsObject(ResolvableType type) {
		return getAsObject(TypeDescriptor.valueOf(type));
	}

	@Nullable
	default Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class<?>) type);
		}
		return getAsObject(TypeDescriptor.valueOf(type));
	}

	@Nullable
	default Object getAsObject(TypeDescriptor type) {
		if (Value.isBaseType(type.getType())) {
			return getAsObject(type.getType());
		}

		return convert(type, Converter.unsupported());
	}

	default short getAsShort() {
		Object value = getSource();
		if (value == null) {
			return 0;
		}

		if (value instanceof Short) {
			return (Short) value;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsShort();
		}

		return (short) convert(TypeDescriptor.valueOf(short.class), Converter.unsupported());
	}

	@Nullable
	default String getAsString() {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsString();
		}

		return (String) convert(TypeDescriptor.valueOf(String.class), Converter.unsupported());
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

	default <T> Optional<T> as(Class<? extends T> type) {
		return map((e) -> getAsObject(type));
	}

	default Value or(Object other) {
		return orElse(Value.of(other));
	}

	@Override
	default Value orElse(Value other) {
		return isPresent() ? this : other;
	}

	default <E extends Throwable> Value orGet(Source<? extends Object, ? extends E> other) throws E {
		return orElseGet(() -> Value.of(other.get()));
	}
}
