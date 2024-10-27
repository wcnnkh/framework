package io.basc.framework.convert.lang;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.lang.Nullable;

@FunctionalInterface
public interface ValueFactory<K> {

	ObjectValue get(K key);

	default byte getAsByte(K key) {
		ObjectValue value = get(key);
		return value == null ? 0 : value.getAsByte();
	}

	default short getAsShort(K key) {
		ObjectValue value = get(key);
		return value == null ? 0 : value.getAsShort();
	}

	default int getAsInt(K key) {
		ObjectValue value = get(key);
		return value == null ? 0 : value.getAsInt();
	}

	default long getAsLong(K key) {
		ObjectValue value = get(key);
		return value == null ? 0L : value.getAsLong();
	}

	default boolean getAsBoolean(K key) {
		ObjectValue value = get(key);
		return value == null ? false : value.getAsBoolean();
	}

	default float getAsFloat(K key) {
		ObjectValue value = get(key);
		return value == null ? 0f : value.getAsFloat();
	}

	default double getAsDouble(K key) {
		ObjectValue value = get(key);
		return value == null ? 0d : value.getAsDouble();
	}

	default char getAsChar(K key) {
		ObjectValue value = get(key);
		return value == null ? 0 : value.getAsChar();
	}

	@Nullable
	default String getAsString(K key) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsString();
	}

	@Nullable
	default BigInteger getAsBigInteger(K key) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsBigInteger();
	}

	@Nullable
	default BigDecimal getAsBigDecimal(K key) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsBigDecimal();
	}

	@Nullable
	default Number getAsNumber(K key) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsNumber();
	}

	@Nullable
	default Enum<?> getAsEnum(K key, Class<?> enumType) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsEnum(enumType);
	}

	@Nullable
	default <T> T getAsObject(K key, Class<? extends T> type) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsObject(type);
	}

	@Nullable
	default Object getAsObject(K key, Type type) {
		ObjectValue value = get(key);
		return value == null ? null : value.getAsObject(type);
	}
}
