package io.basc.framework.core.convert;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.stream.IntStream;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.util.Any;
import io.basc.framework.util.CharSequenceTemplate;
import io.basc.framework.util.Version;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.Enumerable;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.math.BigDecimalValue;
import io.basc.framework.util.math.NumberUtils;
import io.basc.framework.util.math.NumberValue;
import lombok.Data;
import lombok.NonNull;

@FunctionalInterface
public interface Source extends SourceDescriptor, Any, Supplier<Object, ConversionException> {

	public static class EmptyValue implements Source, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Object get() throws ConversionException {
			return null;
		}
	}

	@Data
	public static class SharedValue<W extends SourceDescriptor>
			implements Source, SourceDescriptorWrapper<W>, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private final W source;
		private Object value;

		@Override
		public Object get() throws ConversionException {
			return value;
		}

		@Override
		public TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}
	}

	@FunctionalInterface
	public static interface SourceWrapper<W extends Source>
			extends Source, SourceDescriptorWrapper<W>, AnyWrapper<W>, SupplierWrapper<Object, ConversionException, W> {

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
		default Version getAsVersion() {
			return getSource().getAsVersion();
		}

		@Override
		default double getAsDouble() {
			return getSource().getAsDouble();
		}

		@Override
		default Elements<? extends Source> getAsElements() {
			return getSource().getAsElements();
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
		default NumberValue getAsNumber() {
			return getSource().getAsNumber();
		}

		@Override
		default <T> T getAsObject(Class<? extends T> type) {
			return getSource().getAsObject(type);
		}

		@Override
		default <T> T getAsObject(Class<? extends T> requiredType,
				java.util.function.Supplier<? extends T> defaultSupplier) {
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

		@Override
		default <T> Value<T> getAsValue(Class<? extends T> requriedType) {
			return getSource().getAsValue(requriedType);
		}

		@Override
		default <T> Value<T> map(@NonNull TypeDescriptor requriedTypeDescriptor,
				@NonNull Converter<? super Object, ? extends T, ? extends ConversionException> converter) {
			return getSource().map(requriedTypeDescriptor, converter);
		}

		@Override
		default <R> Value<R> map(@NonNull Function<? super Object, ? extends R, ? extends ConversionException> mapper) {
			return getSource().map(mapper);
		}

	}

	static final Source EMPTY = new EmptyValue();

	static final Source[] EMPTY_ARRAY = new Source[0];

	static Source of(Object value) {
		return of(value, null);
	}

	static Source of(Object value, TypeDescriptor type) {
		if (value == null && type == null) {
			return EMPTY;
		}

		if (type == null && value instanceof Source) {
			return (Source) value;
		}

		TypeDescriptor typeDescriptor = type == null ? TypeDescriptor.forObject(value) : type;
		SharedValueDescriptor valueDescriptor = new SharedValueDescriptor(typeDescriptor);
		SharedValue<SharedValueDescriptor> sharedValue = new SharedValue<>(valueDescriptor);
		sharedValue.setValue(value);
		return sharedValue;
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
		return getAsValue(BigDecimal.class).orElse(null);
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

		return getAsValue(BigInteger.class).orElse(null);
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
		return getAsValue(boolean.class).orElse(false);
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

		return getAsValue(byte.class).orElse(null);
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

		return getAsValue(char.class).orElse((char) 0);
	}

	@Override
	default Version getAsVersion() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof CharSequence) {
			return new CharSequenceTemplate((CharSequence) value);
		}

		if (value instanceof Any) {
			return ((Any) value).getAsVersion();
		}

		return getAsValue(Version.class).orElse(null);
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

		return getAsValue(double.class).orElse(0d);
	}

	@Override
	default Elements<? extends Source> getAsElements() {
		Object value = get();
		TypeDescriptor typeDescriptor = getTypeDescriptor();
		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(collection).map((v) -> Source.of(v, elementTypeDescriptor));
		} else if (value instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(iterable).map((v) -> Source.of(v, elementTypeDescriptor));
		} else if (value instanceof Enumerable) {
			Enumerable<?> enumerable = (Enumerable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(enumerable).map((v) -> Source.of(v, elementTypeDescriptor));
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(() -> IntStream.range(0, len)
					.mapToObj((index) -> Source.of(Array.get(value, index), elementTypeDescriptor)));
		}
		return Elements.singleton(Source.of(value, typeDescriptor));
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

		return getAsValue(enumType).orElse(null);
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

		if (value instanceof Source) {
			return ((Source) value).getAsFloat();
		}

		return getAsValue(float.class).orElse(0f);
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

		return getAsValue(int.class).orElse(0);
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

		return (long) getAsValue(long.class).orElse(0L);
	}

	default NumberValue getAsNumber() {
		Object value = get();
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return new BigDecimalValue(getAsString());
		}

		if (value instanceof Number) {
			return new BigDecimalValue(getAsString());
		}

		if (value instanceof Any) {
			return ((Any) value).getAsNumber();
		}

		return getAsValue(NumberValue.class).orElse(null);
	}

	default <T> T getAsObject(Class<? extends T> type) {
		return getAsObject(type, () -> getAsValue(type).orElse(null));
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
		return getAsObject(type.getType(), () -> map(type, Converter.unsupported()).orElse(null));
	}

	@Override
	default <R> Value<R> map(@NonNull Function<? super Object, ? extends R, ? extends ConversionException> mapper) {
		Value<R> value = new Value<>();
		value.setObject(this);
		value.setMapper(mapper);
		return value;
	}

	default <T> Value<T> getAsValue(Class<? extends T> requriedType) {
		return map(TypeDescriptor.valueOf(requriedType), Converter.unsupported());
	}

	default <T> Value<T> map(@NonNull TypeDescriptor typeDescriptor,
			@NonNull Converter<? super Object, ? extends T, ? extends ConversionException> converter) {
		Value<T> value = new Value<>();
		value.setObject(this);
		value.setTypeDescriptor(typeDescriptor);
		value.setConverter(converter);
		return value;
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

		return getAsValue(short.class).orElse((short) 0);
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

		return (String) getAsValue(String.class).orElse(null);
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
