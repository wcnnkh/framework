package io.basc.framework.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface Any extends IntSupplier, LongSupplier, DoubleSupplier, BooleanSupplier {

	@FunctionalInterface
	public static interface AnyWrapper<W extends Any> extends Any, Wrapper<W> {
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
		default Elements<? extends Any> getAsElements() {
			return getSource().getAsElements();
		}

		@Override
		default <T, E extends Throwable> Elements<T> getAsElements(Class<? extends T> componentType,
				Supplier<? extends T> defaultSupplier) {
			return getSource().getAsElements(componentType, defaultSupplier);
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

		@Override
		default <T, E extends Throwable> Elements<T> getAsElements(Class<? extends T> componentType) {
			return getSource().getAsElements(componentType);
		}

		@Override
		default <T> T getAsArray(Class<? extends T> arrayType) {
			return getSource().getAsArray(arrayType);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T getAsArray(Class<? extends T> arrayType) {
		Class<?> componentType = arrayType.getComponentType();
		Any[] values = getAsElements().toArray(new Any[0]);
		int len = values.length;
		T array = (T) Array.newInstance(componentType, len);
		for (int i = 0; i < len; i++) {
			Any value = values[i];
			if (value == null) {
				continue;
			}

			Object target = value.getAsObject(arrayType);
			Array.set(array, i, target);
		}
		return array;
	}

	BigDecimal getAsBigDecimal();

	BigInteger getAsBigInteger();

	byte getAsByte();

	char getAsChar();

	CharSequence getAsCharSequence();

	Elements<? extends Any> getAsElements();

	default <T, E extends Throwable> Elements<T> getAsElements(Class<? extends T> componentType) {
		return getAsElements(componentType, () -> null);
	}

	default <T, E extends Throwable> Elements<T> getAsElements(Class<? extends T> componentType,
			Supplier<? extends T> defaultSupplier) {
		if (isMultiple()) {
			return getAsElements().map((e) -> e.getAsObject(componentType, defaultSupplier));
		} else {
			return Elements.singleton(getAsObject(componentType, defaultSupplier));
		}
	}

	<T extends Enum<T>> T getAsEnum(Class<T> enumType);

	float getAsFloat();

	Number getAsNumber();

	default <T> T getAsObject(Class<? extends T> requiredType) {
		return getAsObject(requiredType, () -> null);
	}

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
		} else if (requiredType == Any.class) {
			v = this;
		} else if (requiredType.isArray()) {
			v = getAsArray(requiredType);
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
