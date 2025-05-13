package run.soeasy.framework.core.convert.value;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import lombok.NonNull;
import run.soeasy.framework.core.CharSequenceTemplate;
import run.soeasy.framework.core.Value;
import run.soeasy.framework.core.Version;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Enumerable;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.math.BigDecimalValue;
import run.soeasy.framework.core.math.NumberUtils;
import run.soeasy.framework.core.math.NumberValue;

public interface TypedValue extends TypedData<Object>, Value {
	public static interface TypedValueWrapper<W extends TypedValue>
			extends TypedValue, TypedDataWrapper<Object, W>, ValueWrapper<W> {
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
		default double getAsDouble() {
			return getSource().getAsDouble();
		}

		@Override
		default Elements<? extends TypedValue> getAsElements() {
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
		default <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
			return getSource().getAsObject(requiredType, defaultSupplier);
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
		default Version getAsVersion() {
			return getSource().getAsVersion();
		}

		@Override
		default boolean isMultiple() {
			return getSource().isMultiple();
		}

		@Override
		default boolean isNumber() {
			return getSource().isNumber();
		}

		default TypedValue value() {
			return getSource().value();
		}

		@Override
		default TypeDescriptor getReturnTypeDescriptor() {
			return getSource().getReturnTypeDescriptor();
		}

		@Override
		default Object getAsObject(TypeDescriptor type) {
			return getSource().getAsObject(type);
		}

		@Override
		default <T> TypedData<T> getAsData(Class<? extends T> requriedType) {
			return getSource().getAsData(requriedType);
		}

		@Override
		default <T> TypedData<T> getAsData(@NonNull TypeDescriptor typeDescriptor) {
			return getSource().getAsData(typeDescriptor);
		}

		@Override
		default TypedValue getAsValue(@NonNull AccessibleDescriptor typeDescriptor) {
			return getSource().getAsValue(typeDescriptor);
		}

		@Override
		default TypedValue getAsValue(
				@NonNull Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
			return getSource().getAsValue(converter);
		}
	}

	public static class DefaultValue implements Value {

		@Override
		public BigDecimal getAsBigDecimal() {
			return null;
		}

		@Override
		public BigInteger getAsBigInteger() {
			return null;
		}

		@Override
		public boolean getAsBoolean() {
			return false;
		}

		@Override
		public byte getAsByte() {
			return 0;
		}

		@Override
		public char getAsChar() {
			return 0;
		}

		@Override
		public double getAsDouble() {
			return 0;
		}

		@Override
		public Elements<? extends Value> getAsElements() {
			return Elements.empty();
		}

		@Override
		public <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
			return null;
		}

		@Override
		public float getAsFloat() {
			return 0;
		}

		@Override
		public int getAsInt() {
			return 0;
		}

		@Override
		public long getAsLong() {
			return 0;
		}

		@Override
		public NumberValue getAsNumber() {
			return null;
		}

		@Override
		public short getAsShort() {
			return 0;
		}

		@Override
		public String getAsString() {
			return null;
		}

		@Override
		public Version getAsVersion() {
			return null;
		}

		@Override
		public boolean isMultiple() {
			return false;
		}

		@Override
		public boolean isNumber() {
			return false;
		}
	}

	public static TypedValue of(Object value) {
		return of(value, null);
	}

	public static TypedValue of(Object value, TypeDescriptor type) {
		if (type == null && value instanceof TypedValue) {
			return (TypedValue) value;
		}

		ConvertingValue<AccessibleDescriptor> any = new ConvertingValue<>(AccessibleDescriptor.forTypeDescriptor(type));
		any.setValue(value);
		return any;
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

		return getAsData(BigDecimal.class).get();
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

		return getAsData(BigInteger.class).get();
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
		return getAsData(boolean.class).get();
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

		return getAsData(byte.class).get();
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

		return Optional.ofNullable(getAsData(char.class).get()).orElse((char) 0);
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

		return Optional.ofNullable(getAsData(double.class).get()).orElse(0d);
	}

	@Override
	default Elements<? extends TypedValue> getAsElements() {
		Object value = get();
		TypeDescriptor typeDescriptor = getReturnTypeDescriptor();
		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(collection).map((v) -> TypedValue.of(v, elementTypeDescriptor));
		} else if (value instanceof Iterable) {
			Iterable<?> iterable = (Iterable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(iterable).map((v) -> TypedValue.of(v, elementTypeDescriptor));
		} else if (value instanceof Enumerable) {
			Enumerable<?> enumerable = (Enumerable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getGeneric(0);
			return Elements.of(enumerable).map((v) -> TypedValue.of(v, elementTypeDescriptor));
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(() -> IntStream.range(0, len)
					.mapToObj((index) -> TypedValue.of(Array.get(value, index), elementTypeDescriptor)));
		}
		return Elements.singleton(TypedValue.of(value, typeDescriptor));
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

		return getAsData(enumType).get();
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

		if (value instanceof TypedValue) {
			return ((TypedValue) value).getAsFloat();
		}

		return Optional.ofNullable(getAsData(float.class).get()).orElse(0f);
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

		return Optional.ofNullable(getAsData(int.class).get()).orElse(0);
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

		return Optional.ofNullable(getAsData(long.class).get()).orElse(0L);
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

		if (value instanceof Value) {
			return ((Value) value).getAsNumber();
		}

		return getAsData(NumberValue.class).get();
	}

	default <T> T getAsObject(Class<? extends T> type) {
		return getAsObject(type, () -> getAsData(type).get());
	}

	default Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class<?>) type);
		}
		return getAsObject(TypeDescriptor.valueOf(type));
	}

	default Object getAsObject(TypeDescriptor type) {
		return getAsObject(type.getType(), () -> getAsData(type).get());
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

		return Optional.ofNullable(getAsData(short.class).get()).orElse((short) 0);
	}

	default String getAsString() {
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

		return (String) getAsData(String.class).get();
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

		if (value instanceof Value) {
			return ((Value) value).getAsVersion();
		}

		return getAsData(Version.class).get();
	}

	@Override
	default boolean isMultiple() {
		TypeDescriptor typeDescriptor = getReturnTypeDescriptor();
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
		TypeDescriptor typeDescriptor = getReturnTypeDescriptor();
		if (NumberUtils.isNumber(typeDescriptor.getType())) {
			return true;
		}

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

	default <T> TypedData<T> getAsData(Class<? extends T> requriedType) {
		return getAsData(TypeDescriptor.valueOf(requriedType));
	}

	@SuppressWarnings("unchecked")
	default <T> TypedData<T> getAsData(@NonNull TypeDescriptor typeDescriptor) {
		return (TypedData<T>) getAsValue(AccessibleDescriptor.forTypeDescriptor(typeDescriptor));
	}

	default TypedValue getAsValue(@NonNull AccessibleDescriptor typeDescriptor) {
		ConvertingValue<AccessibleDescriptor> any = new ConvertingValue<>(typeDescriptor);
		any.setValue(this);
		return any;
	}

	default TypedValue getAsValue(
			@NonNull Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
		ConvertingValue<AccessibleDescriptor> converting = new ConvertingValue<AccessibleDescriptor>(
				AccessibleDescriptor.forTypeDescriptor(getReturnTypeDescriptor()));
		converting.setValue(this);
		converting.setConverter(converter);
		return converting;
	}

	default TypedValue value() {
		return this;
	}
}
