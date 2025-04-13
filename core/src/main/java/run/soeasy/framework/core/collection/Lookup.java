package run.soeasy.framework.core.collection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import run.soeasy.framework.core.Value;
import run.soeasy.framework.core.Version;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.math.NumberValue;

public interface Lookup<K, V extends Value> {

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

		default <T> T getAsObject(K key, Class<? extends T> type, Supplier<? extends T> defaultSupplier) {
			return getSource().getAsObject(key, type, defaultSupplier);
		}

		default short getAsShort(K key) {
			return getSource().getAsShort(key);
		}

		default String getAsString(K key) {
			return getSource().getAsString(key);
		}
	}

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
