package run.soeasy.framework.core.collection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.math.NumberValue;

public interface Lookup<K, V extends Value> {

	V get(K key);

	default BigDecimal getAsBigDecimal(K key) {
		V value = get(key);
		return value == null ? null : value.getAsBigDecimal();
	}

	default BigInteger getAsBigInteger(K key) {
		V value = get(key);
		return value == null ? null : value.getAsBigInteger();
	}

	default boolean getAsBoolean(K key) {
		V value = get(key);
		return value == null ? false : value.getAsBoolean();
	}

	default byte getAsByte(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsByte();
	}

	default char getAsChar(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsChar();
	}

	default Version getAsVersion(K key) {
		V value = get(key);
		return value == null ? null : value.getAsVersion();
	}

	default double getAsDouble(K key) {
		V value = get(key);
		return value == null ? 0d : value.getAsDouble();
	}

	default <T extends Enum<T>> T getAsEnum(K key, Class<T> enumType) {
		V value = get(key);
		return value == null ? null : value.getAsEnum(enumType);
	}

	default float getAsFloat(K key) {
		V value = get(key);
		return value == null ? 0f : value.getAsFloat();
	}

	default int getAsInt(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsInt();
	}

	default long getAsLong(K key) {
		V value = get(key);
		return value == null ? 0L : value.getAsLong();
	}

	default NumberValue getAsNumber(K key) {
		V value = get(key);
		return value == null ? null : value.getAsNumber();
	}

	default <T> T getAsObject(K key, Class<? extends T> type, Supplier<? extends T> defaultSupplier) {
		V value = get(key);
		return value == null ? null : value.getAsObject(type, defaultSupplier);
	}

	default short getAsShort(K key) {
		V value = get(key);
		return value == null ? 0 : value.getAsShort();
	}

	default String getAsString(K key) {
		V value = get(key);
		return value == null ? null : value.getAsString();
	}
}
