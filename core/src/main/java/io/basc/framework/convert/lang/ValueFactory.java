package io.basc.framework.convert.lang;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.lang.Nullable;

@FunctionalInterface
public interface ValueFactory<K> {

	ValueWrapper get(K key);

	default byte getAsByte(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0 : value.getAsByte();
	}

	default short getAsShort(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0 : value.getAsShort();
	}

	default int getAsInt(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0 : value.getAsInt();
	}

	default long getAsLong(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0L : value.getAsLong();
	}

	default boolean getAsBoolean(K key) {
		ValueWrapper value = get(key);
		return value == null ? false : value.getAsBoolean();
	}

	default float getAsFloat(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0f : value.getAsFloat();
	}

	default double getAsDouble(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0d : value.getAsDouble();
	}

	default char getAsChar(K key) {
		ValueWrapper value = get(key);
		return value == null ? 0 : value.getAsChar();
	}

	@Nullable
	default String getAsString(K key) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsString();
	}

	@Nullable
	default BigInteger getAsBigInteger(K key) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsBigInteger();
	}

	@Nullable
	default BigDecimal getAsBigDecimal(K key) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsBigDecimal();
	}

	@Nullable
	default Number getAsNumber(K key) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsNumber();
	}
	
	default <T extends Enum<T>> T getAsEnum(K key, Class<T> enumType) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsEnum(enumType);
	}

	@Nullable
	default <T> T getAsObject(K key, Class<? extends T> type) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsObject(type);
	}

	@Nullable
	default Object getAsObject(K key, Type type) {
		ValueWrapper value = get(key);
		return value == null ? null : value.getAsObject(type);
	}
}
