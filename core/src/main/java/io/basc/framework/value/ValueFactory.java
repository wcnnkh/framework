package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.Observable;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;

public interface ValueFactory<K> {

	Value get(K key);

	@Nullable
	default BigDecimal getAsBigDecimal(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsBigDecimal();
	}

	@Nullable
	default BigInteger getAsBigInteger(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsBigInteger();
	}

	default boolean getAsBoolean(K key) {
		Value value = get(key);
		return value == null ? false : value.getAsBoolean();
	}

	default byte getAsByte(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsByte();
	}

	default char getAsChar(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsChar();
	}

	@Nullable
	default Class<?> getAsClass(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsClass();
	}

	default double getAsDouble(K key) {
		Value value = get(key);
		return value == null ? 0d : value.getAsDouble();
	}

	@Nullable
	default Enum<?> getAsEnum(K key, Class<?> enumType) {
		Value value = get(key);
		return value == null ? null : value.getAsEnum(enumType);
	}

	default float getAsFloat(K key) {
		Value value = get(key);
		return value == null ? 0f : value.getAsFloat();
	}

	default int getAsInt(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsInt();
	}

	default long getAsLong(K key) {
		Value value = get(key);
		return value == null ? 0L : value.getAsLong();
	}

	@Nullable
	default Number getAsNumber(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsNumber();
	}

	@Nullable
	default <T> T getAsObject(K key, Class<? extends T> type) {
		Value value = get(key);
		return value == null ? null : value.getAsObject(type);
	}

	@Nullable
	default Object getAsObject(K key, Type type) {
		Value value = get(key);
		return value == null ? null : value.getAsObject(type);
	}

	default short getAsShort(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsShort();
	}

	@Nullable
	default String getAsString(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsString();
	}

	default Observable<Value> getObservable(K key) {
		return new DynamicValue<K>(key, this);
	}

	default BroadcastEventRegistry<ChangeEvent<Elements<K>>> getKeyEventRegistry() {
		return (e) -> Registration.EMPTY;
	}
}
