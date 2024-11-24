package io.basc.framework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public interface ValueFactory<K, V extends Value> {
	V get(K key);

	default byte getAsByte(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsByte();
	}

	default short getAsShort(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsShort();
	}

	default int getAsInt(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsInt();
	}

	default long getAsLong(K key) {
		V value = get(key);
		return value == null ? 0L : value.getAsLong();
	}

	default boolean getAsBoolean(K key) {
		V value = get(key);
		return value == null ? false : value.getAsBoolean();
	}

	default float getAsFloat(K key) {
		V value = get(key);
		return value == null ? 0f : value.getAsFloat();
	}

	default double getAsDouble(K key) {
		V value = get(key);
		return value == null ? 0d : value.getAsDouble();
	}

	default char getAsChar(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsChar();
	}

	default String getAsString(K key) {
		V value = get(key);
		return value == null ? null : value.getAsString();
	}

	default BigInteger getAsBigInteger(K key) {
		V value = get(key);
		return value == null ? null : value.getAsBigInteger();
	}

	default BigDecimal getAsBigDecimal(K key) {
		V value = get(key);
		return value == null ? null : value.getAsBigDecimal();
	}

	default Number getAsNumber(K key) {
		V value = get(key);
		return value == null ? null : value.getAsNumber();
	}

	default <T extends Enum<T>> T getAsEnum(K key, Class<T> enumType) {
		V value = get(key);
		return value == null ? null : value.getAsEnum(enumType);
	}

	default <T> T getAsObject(K key, Class<? extends T> type, Supplier<? extends T> defaultSupplier) {
		V value = get(key);
		return value == null ? null : value.getAsObject(type, defaultSupplier);
	}
}
