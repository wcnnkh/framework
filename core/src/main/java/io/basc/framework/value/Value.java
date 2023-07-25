package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.strings.StringConverter;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.function.Optional;
import io.basc.framework.util.function.Source;

public interface Value extends Optional<Value>, IntSupplier, LongSupplier, DoubleSupplier, ConversionService {
	static final Value EMPTY = new EmptyValue();

	static final Value[] EMPTY_ARRAY = new Value[0];

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

	/**
	 * 这并不是指基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Type type) {
		return isUnconvertibleType(type) || Number.class == type;
	}

	static Value of(@Nullable Object value) {
		return of(value, null, null);
	}

	static Value of(@Nullable Object value,
			@Nullable Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		return of(value, null, converter);
	}

	static Value of(@Nullable Object value, @Nullable TypeDescriptor type) {
		return of(value, type, null);
	}

	static Value of(@Nullable Object value, @Nullable TypeDescriptor type,
			@Nullable Converter<? super Object, ? super Object, ? extends RuntimeException> converter) {
		if (converter == null) {
			if (type == null) {
				if (value == null) {
					return EMPTY;
				}

				if (value instanceof Value) {
					return (Value) value;
				}

				return new AnyValue(value);
			}

			// 使用默认的转换行为
			return new AnyValue(value, type);
		}
		return new AnyValue(value, type, converter);
	}

	default <T> Optional<T> as(Class<? extends T> type) {
		return map((e) -> getAsObject(type));
	}

	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return !isBaseType(targetType.getType());
	}

	@Nullable
	@SuppressWarnings("unchecked")
	default <T, E extends Throwable> T convert(Class<? extends T> targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		return (T) convert(TypeDescriptor.valueOf(targetType), converter);
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
			while (true) {
				ConversionService conversionService = (ConversionService) converter;
				if (conversionService.canConvert(sourceType, targetType)) {
					return converter.convert(value, sourceType, targetType);
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

		return getStringConverter().convert(getAsString(), BigDecimal.class);
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

		return getStringConverter().convert(getAsString(), BigInteger.class);
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
		return getStringConverter().convert(getAsString(), boolean.class);
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

		return getStringConverter().convert(getAsString(), byte.class);
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

		return getStringConverter().convert(getAsString(), char.class);
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

		return getStringConverter().convert(getAsString(), Class.class);
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

		return getStringConverter().convert(getAsString(), double.class);
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

		return (Enum<?>) getStringConverter().convert(getAsString(), enumType);
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

		return getStringConverter().convert(getAsString(), float.class);
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

		return getStringConverter().convert(getAsString(), int.class);
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

		return getStringConverter().convert(getAsString(), long.class);
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

		return getStringConverter().convert(getAsString(), Number.class);
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
			v = getAsObject(TypeDescriptor.valueOf(type));
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

		Object value = getSource();
		if (value == null) {
			return null;
		}

		Class<?> rawClass = type.getType();
		if (rawClass == Object.class || rawClass == null) {
			return value;
		}

		if (!type.isGeneric() && type.getType().isInstance(value)) {
			return value;
		}

		TypeDescriptor sourceType = getTypeDescriptor();
		while (true) {
			if (canConvert(sourceType, type)) {
				return convert(value, sourceType, type);
			}

			if (value instanceof Value) {
				Value sourceValue = (Value) value;
				if (sourceValue.canConvert(sourceType, type)) {
					return sourceValue.convert(value, sourceType, type);
				}
				value = sourceValue.getSource();
				sourceType = sourceValue.getTypeDescriptor();
			}
			break;
		}

		value = getSource();
		sourceType = getTypeDescriptor();
		if (value instanceof Value) {
			return ((Value) value).getAsObject(type);
		}
		return convert(value, sourceType, type);
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

		return getStringConverter().convert(getAsString(), short.class);
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

		return convert(value, getTypeDescriptor(), String.class);
	}

	@Nullable
	Object getSource();

	default StringConverter getStringConverter() {
		return StringConverter.DEFAULT;
	}

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

	@Override
	default Value orElse(Value other) {
		Object value = getSource();
		if (value == null) {
			return other;
		}

		if (value instanceof Value) {
			return ((Value) value).orElse(other);
		}

		return transform(value, getTypeDescriptor());
	}

	default Value or(Object other) {
		return orElse(other instanceof Value ? (Value) other : transform(other, null));
	}

	default <E extends Throwable> Value orGet(Source<? extends Object, ? extends E> other) throws E {
		return orElseGet(() -> transform(other.get(), null));
	}

	default Value transform(Object value, @Nullable TypeDescriptor type) {
		return new AnyValue(value, type);
	}
}
