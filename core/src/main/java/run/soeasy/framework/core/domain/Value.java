package run.soeasy.framework.core.domain;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;
import run.soeasy.framework.core.type.ClassUtils;

public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {
	public static final Value DEFAULT = new DefaultValue();

	@SuppressWarnings("unchecked")
	default <T> T getAsArray(Class<? extends T> componentType) {
		Value[] values = getAsElements().toArray(new Value[0]);
		int len = values.length;
		T array = (T) Array.newInstance(componentType, len);
		for (int i = 0; i < len; i++) {
			Value value = values[i];
			if (value == null) {
				continue;
			}

			Object target = value.getAsObject(componentType);
			Array.set(array, i, target);
		}
		return array;
	}

	default BigDecimal getAsBigDecimal() {
		if (isNumber()) {
			return getAsNumber().getAsBigDecimal();
		}
		String value = getAsString();
		return value == null ? null : new BigDecimal(value);
	}

	default BigInteger getAsBigInteger() {
		if (isNumber()) {
			return getAsNumber().getAsBigInteger();
		}

		String value = getAsString();
		return value == null ? null : new BigInteger(value);
	}

	@Override
	default boolean getAsBoolean() {
		if (isNumber()) {
			return getAsNumber().compareTo(NumberValue.ONE) == 0;
		}

		String value = getAsString();
		return value == null ? false : Boolean.parseBoolean(value);
	}

	default byte getAsByte() {
		if (isNumber()) {
			return getAsNumber().getAsByte();
		}

		String value = getAsString();
		return value == null ? 0 : Byte.parseByte(value);
	}

	default char getAsChar() {
		String value = getAsString();
		if (value.length() == 1) {
			return value.charAt(0);
		}
		throw new UnsupportedOperationException("Not a char");
	}

	@Override
	default double getAsDouble() {
		if (isNumber()) {
			return getAsNumber().getAsDouble();
		}

		String value = getAsString();
		return value == null ? 0 : Double.parseDouble(value);
	}

	default boolean isMultiple() {
		return false;
	}

	default Elements<? extends Value> getAsElements() {
		return Elements.empty();
	}

	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		if (isNumber()) {
			int ordinal = getAsNumber().getAsInt();
			EnumSet<T> enumSet = EnumSet.noneOf(enumType);
			for (T e : enumSet) {
				if (e.ordinal() == ordinal) {
					return e;
				}
			}
			throw new NoSuchElementException(enumType + "[" + ordinal + "]");
		}

		String value = getAsString();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return Enum.valueOf(enumType, value);
	}

	default float getAsFloat() {
		if (isNumber()) {
			return getAsNumber().getAsFloat();
		}

		String value = getAsString();
		return value == null ? 0 : Float.parseFloat(value);
	}

	@Override
	default int getAsInt() {
		if (isNumber()) {
			return getAsNumber().getAsInt();
		}

		String value = getAsString();
		return value == null ? 0 : Integer.parseInt(value);
	}

	@Override
	default long getAsLong() {
		if (isNumber()) {
			return getAsNumber().getAsLong();
		}

		String value = getAsString();
		return value == null ? 0 : Long.parseLong(value);
	}

	default <R> R getAsObject(Class<? extends R> requiredType) {
		return getAsObject(requiredType, () -> DEFAULT.getAsObject(requiredType));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <R> R getAsObject(Class<? extends R> requiredType, Supplier<? extends R> defaultSupplier) {
		Object v = null;
		if (String.class == requiredType) {
			v = getAsString();
		} else if (ClassUtils.isInt(requiredType)) {
			v = getAsInt();
		} else if (ClassUtils.isLong(requiredType)) {
			v = getAsLong();
		} else if (ClassUtils.isFloat(requiredType)) {
			v = getAsFloat();
		} else if (ClassUtils.isDouble(requiredType)) {
			v = getAsDouble();
		} else if (ClassUtils.isShort(requiredType)) {
			v = getAsShort();
		} else if (ClassUtils.isBoolean(requiredType)) {
			v = getAsBoolean();
		} else if (ClassUtils.isByte(requiredType)) {
			v = getAsByte();
		} else if (ClassUtils.isChar(requiredType)) {
			v = getAsChar();
		} else if (BigDecimal.class == requiredType) {
			v = getAsBigDecimal();
		} else if (BigInteger.class == requiredType) {
			v = getAsBigInteger();
		} else if (Version.class == requiredType) {
			v = getAsVersion();
		} else if (NumberValue.class.isAssignableFrom(requiredType)) {
			v = getAsNumber();
		} else if (requiredType.isEnum()) {
			v = getAsEnum((Class<? extends Enum>) requiredType);
		} else if (requiredType == Value.class) {
			v = this;
		} else if (requiredType.isArray()) {
			v = getAsArray(requiredType);
		} else {
			v = defaultSupplier.get();
		}
		return (R) v;
	}

	default short getAsShort() {
		if (isNumber()) {
			return getAsNumber().getAsShort();
		}

		String value = getAsString();
		return value == null ? 0 : Short.parseShort(value);
	}

	default Version getAsVersion() {
		return isNumber() ? getAsNumber() : new CharSequenceTemplate(getAsString(), null);
	}

	boolean isNumber();

	NumberValue getAsNumber();

	String getAsString();
}
