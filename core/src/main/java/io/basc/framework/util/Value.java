package io.basc.framework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface Value extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {

	@FunctionalInterface
	public static interface ValueWrapper<W extends Value> extends Value, Wrapper<W> {
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
		default CharSequence getAsCharSequence() {
			return getSource().getAsCharSequence();
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
		default Value[] getAsMultiple() {
			return getSource().getAsMultiple();
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
		default String getAsString() {
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

	BigDecimal getAsBigDecimal();

	BigInteger getAsBigInteger();

	byte getAsByte();

	char getAsChar();

	CharSequence getAsCharSequence();

	<T extends Enum<T>> T getAsEnum(Class<T> enumType);

	float getAsFloat();

	Value[] getAsMultiple();

	default <T, E extends Throwable> Elements<T> getAsMultiple(Class<? extends T> componentType,
			Supplier<? extends T> defaultSupplier) {
		if (isMultiple()) {
			return Elements.forArray(getAsMultiple()).map((e) -> e.getAsObject(componentType, defaultSupplier));
		} else {
			return Elements.singleton(getAsObject(componentType, defaultSupplier));
		}
	}

	Number getAsNumber();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <T> T getAsObject(Class<? extends T> requiredType, Supplier<? extends T> defaultSupplier) {
		Object v = null;
		if (CharSequence.class == requiredType) {
			v = getAsCharSequence();
		} else if (String.class == requiredType) {
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
		} else if (Number.class == requiredType) {
			v = getAsNumber();
		} else if (requiredType.isEnum()) {
			v = getAsEnum((Class<? extends Enum>) requiredType);
		} else if (requiredType == Value.class) {
			v = this;
		} else {
			v = defaultSupplier.get();
		}
		return (T) v;
	}

	short getAsShort();

	default String getAsString() {
		CharSequence charSequence = getAsCharSequence();
		return charSequence == null ? null : charSequence.toString();
	}

	/**
	 * 是否有多个，例如是一个数组或集合
	 * 
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
