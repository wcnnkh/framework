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
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Enumerable;
import io.basc.framework.util.Source;
import io.basc.framework.util.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public interface Any extends ValueDescriptor, Value, Source<Object, ConversionException> {

	@FunctionalInterface
	public static interface AnyWrapper<W extends Any> extends Any, ValueDescriptorWrapper<W>, ValueWrapper<W> {

		@Override
		default Object get() throws ConversionException {
			return getSource().get();
		}

		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}

		@Override
		default Any[] getAsMultiple() {
			return getSource().getAsMultiple();
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
		default double getAsDouble() {
			return getSource().getAsDouble();
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
		default <T, E extends Throwable> Elements<T> getAsMultiple(Class<? extends T> componentType,
				Supplier<? extends T> defaultSupplier) {
			return getSource().getAsMultiple(componentType, defaultSupplier);
		}

		@Override
		default Number getAsNumber() {
			return getSource().getAsNumber();
		}

		@Override
		default <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
			return getSource().getAsObject(requiredType, defaultSupplier);
		}

		@Override
		default short getAsShort() {
			return getSource().getAsShort();
		}

		@Override
		default CharSequence getAsString() {
			return getSource().getAsString();
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

	public static class EmptyValue implements Any, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Object get() {
			return null;
		}
	}

	@AllArgsConstructor
	@Setter
	@Getter
	public static class ObjectValue implements Any, Cloneable, Serializable {
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
		public Object get() {
			return value;
		}

		@Override
		public TypeDescriptor getTypeDescriptor() {
			if (typeDescriptor != null) {
				return typeDescriptor;
			}
			return Any.super.getTypeDescriptor();
		}

		public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
			this.typeDescriptor = typeDescriptor;
		}
	}

	static final Any EMPTY = new EmptyValue();

	static final Any[] EMPTY_ARRAY = new Any[0];

	@Override
	default boolean isMultiple() {
		TypeDescriptor typeDescriptor = getTypeDescriptor();
		return typeDescriptor.isArray() || typeDescriptor.isCollection();
	}

	@Override
	default Any[] getAsMultiple() {
		// TODO 暂时这样实现
		return asElements(get(), getTypeDescriptor()).toArray(new Any[0]);
	}

	static Elements<Any> asElements(Object value, TypeDescriptor typeDescriptor) {
		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(collection).map((v) -> Any.of(v, elementTypeDescriptor));
		} else if (value instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(iterable).map((v) -> Any.of(v, elementTypeDescriptor));
		} else if (value instanceof Enumerable) {
			Enumerable<?> enumerable = (Enumerable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(enumerable).map((v) -> Any.of(v, elementTypeDescriptor));
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(() -> IntStream.range(0, len)
					.mapToObj((index) -> Any.of(Array.get(value, index), elementTypeDescriptor)));
		} /*
			 * else if (value instanceof Iterator) { Iterator<?> iterator = (Iterator<?>)
			 * value; TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			 * return Elements.of(() -> iterator).map((v) -> Value.of(v,
			 * elementTypeDescriptor)); } else if (value instanceof Enumeration) {
			 * Enumeration<?> enumeration = (Enumeration<?>) value; TypeDescriptor
			 * elementTypeDescriptor = typeDescriptor.getGeneric(0); return Elements.of(()
			 * -> enumeration).map((v) -> Value.of(v, elementTypeDescriptor)); }
			 */
		return Elements.singleton(Any.of(value, typeDescriptor));
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

	static Any of(Object value) {
		return of(value, null);
	}

	static Any of(Object value, TypeDescriptor type) {
		if (value == null && type == null) {
			return EMPTY;
		}

		if (type == null && value instanceof Any) {
			return (Any) value;
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

		if (value instanceof Value) {
			return ((Value) value).getAsBigDecimal();
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

		if (value instanceof Value) {
			return ((Value) value).getAsBigInteger();
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

		if (value instanceof Value) {
			return ((Value) value).getAsBoolean();
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

		if (value instanceof Value) {
			return ((Value) value).getAsByte();
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

		if (value instanceof Value) {
			return ((Value) value).getAsChar();
		}

		return (char) getAsObject(TypeDescriptor.valueOf(char.class), Converter.unsupported());
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
		Object value = get();
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

		if (value instanceof Any) {
			return ((Any) value).getAsFloat();
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
		Object value = get();
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
		Object value = get();
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

			if (source instanceof Any) {
				source = ((Any) source).get();
				sourceType = ((Any) source).getTypeDescriptor();
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

		if (value instanceof Value) {
			return ((Value) value).getAsShort();
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		return (short) getAsObject(TypeDescriptor.valueOf(short.class), Converter.unsupported());
	}

	default CharSequence getAsString() {
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
		if (value == null) {
			return false;
		}

		if (value instanceof Any) {
			return ((Any) value).isPresent();
		}
		return true;
	}
}
