package run.soeasy.framework.core.convert.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Enumerable;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.CharSequenceTemplate;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.math.BigDecimalValue;
import run.soeasy.framework.core.math.NumberUtils;
import run.soeasy.framework.core.math.NumberValue;

public interface TypedValue extends TypedData<Object>, Value {
	public static TypedValue of(Object value) {
		return of(value, null);
	}

	public static TypedValue of(Object value, TypeDescriptor typeDescriptor) {
		if (value instanceof TypedData) {
			TypedValue typedValue = ((TypedData<?>) value).value();
			return typeDescriptor == null ? typedValue : typedValue.map(typeDescriptor, Converter.assignable());
		}

		CustomizeTypedValueAccessor any = new CustomizeTypedValueAccessor();
		any.setValue(value);
		any.setTypeDescriptor(typeDescriptor);
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
		return map(BigDecimal.class, Converter.assignable()).get();
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
		return map(BigInteger.class, Converter.assignable()).get();
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
		Boolean target = map(boolean.class, Converter.assignable()).get();
		return target == null ? false : target;
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
		Byte target = map(byte.class, Converter.assignable()).get();
		return target == null ? 0 : target;
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
		Character target = map(char.class, Converter.assignable()).get();
		return target == null ? 0 : target;
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
		Double target = map(double.class, Converter.assignable()).get();
		return target == null ? 0d : target;
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
			TypeDescriptor elementTypeDescriptor = typeDescriptor.upcast(Iterable.class)
					.map((e) -> e.getActualTypeArgument(0));
			return Elements.of(iterable).map((v) -> TypedValue.of(v, elementTypeDescriptor));
		} else if (value instanceof Enumerable) {
			Enumerable<?> enumerable = (Enumerable<?>) value;
			TypeDescriptor elementTypeDescriptor = typeDescriptor.upcast(Enumerable.class)
					.map((e) -> e.getActualTypeArgument(0));
			return Elements.of(enumerable).map((v) -> TypedValue.of(v, elementTypeDescriptor));
		} else if (value.getClass().isArray()) {
			TypeDescriptor elementTypeDescriptor = typeDescriptor.getElementTypeDescriptor();
			return Elements.of(() -> ArrayUtils.stream(value).map((e) -> TypedValue.of(e, elementTypeDescriptor)));
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
		return map(enumType, Converter.assignable()).get();
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
		Float target = map(float.class, Converter.assignable()).get();
		return target == null ? 0f : target;
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

		Integer target = map(int.class, Converter.assignable()).get();
		return target == null ? 0 : target;
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

		Long target = map(long.class, Converter.assignable()).get();
		return target == null ? 0L : target;
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

		return map(NumberValue.class, Converter.assignable()).get();
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
		Short target = map(short.class, Converter.assignable()).get();
		return target == null ? 0 : target;
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

		return map(String.class, Converter.assignable()).get();
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

		return map(Version.class, Converter.assignable()).get();
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
		return false;
	}

	@SuppressWarnings("unchecked")
	default <R> TypedData<R> map(@NonNull Class<R> type, @NonNull Converter converter) {
		return (TypedData<R>) map(TypeDescriptor.forObject(type), converter);
	}

	default TypedValue map(@NonNull TypeDescriptor typeDescriptor, @NonNull Converter converter) {
		return new MappedTypedValue<>(this, typeDescriptor, converter);
	}

	default TypedValue value() {
		return this;
	}
}
