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
import run.soeasy.framework.core.lang.ClassUtils;
import run.soeasy.framework.core.math.NumberValue;

public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {
	@FunctionalInterface
	public static interface ValueWrapper<W extends Value> extends Value, Wrapper<W> {
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
		default Elements<? extends Value> getAsElements() {
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

	/*
	 * default boolean canConvert(Class<?> requiredType) { return String.class ==
	 * requiredType || ClassUtils.isInt(requiredType) ||
	 * ClassUtils.isLong(requiredType) || ClassUtils.isFloat(requiredType) ||
	 * ClassUtils.isDouble(requiredType) || ClassUtils.isShort(requiredType) ||
	 * ClassUtils.isBoolean(requiredType) || ClassUtils.isByte(requiredType) ||
	 * ClassUtils.isChar(requiredType) || BigDecimal.class == requiredType ||
	 * BigInteger.class == requiredType || Version.class == requiredType ||
	 * NumberValue.class.isAssignableFrom(requiredType) || requiredType.isEnum() ||
	 * requiredType == Any.class || requiredType.isArray(); }
	 */

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
