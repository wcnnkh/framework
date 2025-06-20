package run.soeasy.framework.core.collection;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.domain.Version;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.math.NumberValue;

@FunctionalInterface
public interface LookupWrapper<K, V extends Value, W extends Lookup<K, V>> extends Lookup<K, V>, Wrapper<W> {
	@Override
	default V get(K key) {
		return getSource().get(key);
	}

	default BigDecimal getAsBigDecimal(K key) {
		return getSource().getAsBigDecimal(key);
	}

	default BigInteger getAsBigInteger(K key) {
		return getSource().getAsBigInteger(key);
	}

	default boolean getAsBoolean(K key) {
		return getSource().getAsBoolean(key);
	}

	default byte getAsByte(K key) {
		return getSource().getAsByte(key);
	}

	default char getAsChar(K key) {
		return getSource().getAsChar(key);
	}

	@Override
	default Version getAsVersion(K key) {
		return getSource().getAsVersion(key);
	}

	default double getAsDouble(K key) {
		return getSource().getAsDouble(key);
	}

	default <T extends Enum<T>> T getAsEnum(K key, Class<T> enumType) {
		return getSource().getAsEnum(key, enumType);
	}

	default float getAsFloat(K key) {
		return getSource().getAsFloat(key);
	}

	default int getAsInt(K key) {
		return getSource().getAsInt(key);
	}

	default long getAsLong(K key) {
		return getSource().getAsLong(key);
	}

	default NumberValue getAsNumber(K key) {
		return getSource().getAsNumber(key);
	}

	default <T> T getAsObject(K key, Class<? extends T> type) {
		return getSource().getAsObject(key, type);
	}

	default short getAsShort(K key) {
		return getSource().getAsShort(key);
	}

	default String getAsString(K key) {
		return getSource().getAsString(key);
	}
}