package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.event.BroadcastNamedEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.Observable;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Registration;

public interface ValueFactory<K> extends BroadcastNamedEventRegistry<K, ChangeEvent<K>> {

	Value get(K key);

	default byte getAsByte(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsByte();
	}

	default short getAsShort(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsShort();
	}

	default int getAsInt(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsInt();
	}

	default long getAsLong(K key) {
		Value value = get(key);
		return value == null ? 0L : value.getAsLong();
	}

	default boolean getAsBoolean(K key) {
		Value value = get(key);
		return value == null ? false : value.getAsBoolean();
	}

	default float getAsFloat(K key) {
		Value value = get(key);
		return value == null ? 0f : value.getAsFloat();
	}

	default double getAsDouble(K key) {
		Value value = get(key);
		return value == null ? 0d : value.getAsDouble();
	}

	default char getAsChar(K key) {
		Value value = get(key);
		return value == null ? 0 : value.getAsChar();
	}

	@Nullable
	default String getAsString(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsString();
	}

	@Nullable
	default BigInteger getAsBigInteger(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsBigInteger();
	}

	@Nullable
	default BigDecimal getAsBigDecimal(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsBigDecimal();
	}

	@Nullable
	default Number getAsNumber(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsNumber();
	}

	@Nullable
	default Class<?> getAsClass(K key) {
		Value value = get(key);
		return value == null ? null : value.getAsClass();
	}

	@Nullable
	default Enum<?> getAsEnum(K key, Class<?> enumType) {
		Value value = get(key);
		return value == null ? null : value.getAsEnum(enumType);
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

	@Override
	default Registration registerListener(K name, EventListener<ChangeEvent<K>> eventListener) {
		return Registration.EMPTY;
	}

	default Observable<Value> getObservable(K key) {
		return new ObservableValue<K>(key, this);
	}
}
