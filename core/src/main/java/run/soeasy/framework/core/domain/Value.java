package run.soeasy.framework.core.domain;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;
import run.soeasy.framework.core.type.ClassUtils;

public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {
	public static final Value DEFAULT = new DefaultValue();

	@SuppressWarnings("unchecked")
	default <T> T getAsArray(Class<? extends T> arrayType) {
		Class<?> componentType = arrayType.getComponentType();
		Value[] values = getAsElements().toArray(new Value[0]);
		int len = values.length;
		T array = (T) Array.newInstance(componentType, len);
		for (int i = 0; i < len; i++) {
			Value value = values[i];
			if (value == null) {
				continue;
			}

			Object target = value.getAsObject(arrayType);
			Array.set(array, i, target);
		}
		return array;
	}

	default BigDecimal getAsBigDecimal() {
		throw new UnsupportedOperationException("Not a BigDecimal");
	}

	default BigInteger getAsBigInteger() {
		throw new UnsupportedOperationException("Not a BigInteger");
	}

	@Override
	default boolean getAsBoolean() {
		throw new UnsupportedOperationException("Not a boolean");
	}

	default byte getAsByte() {
		throw new UnsupportedOperationException("Not a byte");
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
		throw new UnsupportedOperationException("Not a double");
	}

	default Elements<? extends Value> getAsElements() {
		throw new UnsupportedOperationException("Not a Multiple");
	}

	default <T extends Enum<T>> T getAsEnum(Class<T> enumType) {
		throw new UnsupportedOperationException("Not a enum");
	}

	default float getAsFloat() {
		throw new UnsupportedOperationException("Not a float");
	}

	@Override
	default int getAsInt() {
		throw new UnsupportedOperationException("Not a int");
	}

	@Override
	default long getAsLong() {
		throw new UnsupportedOperationException("Not a long");
	}

	NumberValue getAsNumber();

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
		throw new UnsupportedOperationException("Not a short");
	}

	String getAsString();

	default Version getAsVersion() {
		return isNumber() ? getAsNumber() : new CharSequenceTemplate(getAsString(), null);
	}

	/**
	 * 是否有多个，例如是一个数组或集合
	 * 
	 * @see #getAsElements()
	 * @return
	 */
	boolean isMultiple();

	/**
	 * 是否是数值
	 * 
	 * @return
	 */
	boolean isNumber();
}
