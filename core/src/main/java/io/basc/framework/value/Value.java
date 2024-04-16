package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.function.Optional;
import io.basc.framework.util.function.Source;

public interface Value extends Optional<Value>, IntSupplier, LongSupplier, DoubleSupplier,
		Converter<Object, Object, ConversionException> {
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

	default <T> Optional<T> as(Class<? extends T> type) {
		return map((e) -> getAsObject(type));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default <T, E extends Throwable> T convert(Class<? extends T> targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		return (T) convert(TypeDescriptor.valueOf(targetType), converter);
	}

	default <E extends Throwable> Object convert(Type targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		return convert(TypeDescriptor.valueOf(targetType), converter);
	}

	@Nullable
	default <E extends Throwable> Object convert(TypeDescriptor targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		Assert.requiredArgument(converter != null, "converter");
		Object value = getSource();
		if (value == null) {
			return null;
		}

		Class<?> rawClass = targetType.getType();
		if (rawClass == Object.class || rawClass == null) {
			return value;
		}

		TypeDescriptor sourceType = getTypeDescriptor();
		if (converter instanceof ConversionService) {
			ConversionService conversionService = (ConversionService) converter;
			while (true) {
				if (conversionService.canConvert(sourceType, targetType)) {
					return conversionService.convert(value, sourceType, targetType);
				}

				if (value instanceof Value) {
					value = ((Value) value).getSource();
					sourceType = ((Value) value).getTypeDescriptor();
				}
				break;
			}
		}

		value = getSource();
		sourceType = getTypeDescriptor();
		try {
			return converter.convert(value, sourceType, targetType);
		} catch (RuntimeException e) {
			if (value instanceof Value) {
				return ((Value) value).getAsObject(targetType);
			}
			throw e;
		}
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

		return convert(getAsString(), BigDecimal.class);
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

		return convert(getAsString(), BigInteger.class);
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
		return convert(getAsString(), boolean.class);
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

		return convert(getAsString(), byte.class);
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

		return convert(getAsString(), char.class);
	}

	@Nullable
	default Class<?> getAsClass() {
		Object value = getSource();
		if (value == null) {
			return null;
		}

		if (value instanceof Class) {
			return (Class<?>) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsClass();
		}

		return convert(getAsString(), Class.class);
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

		return convert(getAsString(), double.class);
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

		return (Enum<?>) convert(getAsString(), enumType);
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

		return convert(getAsString(), float.class);
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

		return convert(getAsString(), int.class);
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

		return convert(getAsString(), long.class);
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

		return convert(getAsString(), Number.class);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default <T> T getAsObject(Class<? extends T> type) {
		Object v = null;
		if (String.class == type) {
			v = getAsString();
		} else if (int.class == type) {
			v = getAsInt();
		} else if (Integer.class == type) {
			v = isEmpty() ? null : getAsInt();
		} else if (long.class == type) {
			v = getAsLong();
		} else if (Long.class == type) {
			v = isEmpty() ? null : getAsLong();
		} else if (float.class == type) {
			v = getAsFloat();
		} else if (Float.class == type) {
			v = isEmpty() ? null : getAsFloat();
		} else if (double.class == type) {
			v = getAsDouble();
		} else if (Double.class == type) {
			v = isEmpty() ? null : getAsDouble();
		} else if (short.class == type) {
			v = getAsShort();
		} else if (Short.class == type) {
			v = isEmpty() ? null : getAsShort();
		} else if (boolean.class == type) {
			v = getAsBoolean();
		} else if (Boolean.class == type) {
			v = isEmpty() ? null : getAsBoolean();
		} else if (byte.class == type) {
			v = getAsByte();
		} else if (Byte.class == type) {
			v = isEmpty() ? null : getAsByte();
		} else if (char.class == type) {
			v = getAsChar();
		} else if (Character.class == type) {
			v = isEmpty() ? null : getAsChar();
		} else if (BigDecimal.class == type) {
			v = getAsBigDecimal();
		} else if (BigInteger.class == type) {
			v = getAsBigInteger();
		} else if (Number.class == type) {
			v = getAsNumber();
		} else if (Class.class == type) {
			v = getAsClass();
		} else if (type.isEnum()) {
			v = getAsEnum(type);
		} else if (type == Value.class) {
			v = this;
		} else {
			v = convert(type, this);
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

		return convert(type, this);
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

		return convert(getAsString(), short.class);
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

		return convert(String.class, this);
	}

	/**
	 * 注意，这是一个兜底方法，是用来转换非常用类型的
	 */
	@Override
	default Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return ValueConverter.getInstance().convert(source, sourceType, targetType);
	}

	@Nullable
	Object getSource();

	default TypeDescriptor getTypeDescriptor() {
		Object value = getSource();
		if (value == null) {
			return TypeDescriptor.valueOf(Object.class);
		}
		return TypeDescriptor.forObject(value);
	}

	default boolean isEmpty() {
		if (isPresent()) {
			Object value = getSource();
			if (value instanceof Value) {
				return ((Value) value).isEmpty();
			}

			return ObjectUtils.isEmpty(value);
		}
		return true;
	}

	/**
	 * 是否可以转换为number,此方法不代表数据的原始类型是number
	 * 
	 * @see #getAsNumber()
	 * @return
	 */
	default boolean isNumber() {
		if (isEmpty()) {
			return false;
		}

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

	@Override
	default boolean isPresent() {
		Object value = getSource();
		if (value == null) {
			return false;
		}

		if (value instanceof Value) {
			return ((Value) value).isPresent();
		}
		return true;
	}

	default Value or(Object other) {
		return orElse(other instanceof Value ? (Value) other : new ConvertableValue(other, null, this));
	}

	@Override
	default Value orElse(Value other) {
		Object value = getSource();
		if (value == null) {
			return other;
		}

		if (value instanceof Value) {
			return ((Value) value).orElse(other);
		}

		return this;
	}

	default <E extends Throwable> Value orGet(Source<? extends Object, ? extends E> other) throws E {
		return orElseGet(() -> new ConvertableValue(other, null, this));
	}
}
