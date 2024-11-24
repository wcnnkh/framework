package io.basc.framework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public interface ValueFactoryWrapper<K, V extends Value, W extends ValueFactory<K, V>>
		extends ValueFactory<K, V>, Wrapper<W> {
	@Override
	default V get(K key) {
		return getSource().get(key);
	}

	default byte getAsByte(K key) {
		return getSource().getAsByte(key);
	}

	default short getAsShort(K key) {
		return getSource().getAsShort(key);
	}

	default int getAsInt(K key) {
		return getSource().getAsInt(key);
	}

	default long getAsLong(K key) {
		return getSource().getAsLong(key);
	}

	default boolean getAsBoolean(K key) {
		return getSource().getAsBoolean(key);
	}

	default float getAsFloat(K key) {
		return getSource().getAsFloat(key);
	}

	default double getAsDouble(K key) {
		return getSource().getAsDouble(key);
	}

	default char getAsChar(K key) {
		return getSource().getAsChar(key);
	}

	default String getAsString(K key) {
		return getSource().getAsString(key);
	}

	default BigInteger getAsBigInteger(K key) {
		return getSource().getAsBigInteger(key);
	}

	default BigDecimal getAsBigDecimal(K key) {
		return getSource().getAsBigDecimal(key);
	}

	default Number getAsNumber(K key) {
		return getSource().getAsNumber(key);
	}

	default <T extends Enum<T>> T getAsEnum(K key, Class<T> enumType) {
		return getSource().getAsEnum(key, enumType);
	}

	default <T> T getAsObject(K key, Class<? extends T> type, Supplier<? extends T> defaultSupplier) {
		return getSource().getAsObject(key, type, defaultSupplier);
	}
}
