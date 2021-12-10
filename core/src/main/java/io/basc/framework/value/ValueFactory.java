package io.basc.framework.value;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.NamedEventRegistry;
import io.basc.framework.event.Observable;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public interface ValueFactory<K> extends NamedEventRegistry<K, ChangeEvent<K>> {
	@Nullable
	Value getValue(K key);

	default Value getDefaultValue(K key) {
		return EmptyValue.INSTANCE;
	}

	@Nullable
	default Byte getByte(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsByte() : value.getAsByte();
	}

	default byte getByteValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsByteValue() : value.getAsByteValue();
	}

	@Nullable
	default Short getShort(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsShort() : value.getAsShort();
	}

	default short getShortValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsShortValue() : value.getAsShortValue();
	}

	@Nullable
	default Integer getInteger(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsInteger() : value.getAsInteger();
	}

	default int getIntValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsIntValue() : value.getAsIntValue();
	}

	@Nullable
	default Long getLong(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsLong() : value.getAsLong();
	}

	default long getLongValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsLongValue() : value.getAsLongValue();
	}

	@Nullable
	default Boolean getBoolean(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsBoolean() : value.getAsBoolean();
	}

	default boolean getBooleanValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsBooleanValue() : value.getAsBooleanValue();
	}

	@Nullable
	default Float getFloat(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsFloat() : value.getAsFloat();
	}

	default float getFloatValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsFloatValue() : value.getAsFloatValue();
	}

	@Nullable
	default Double getDouble(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsDouble() : value.getAsDouble();
	}

	default double getDoubleValue(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsDoubleValue() : value.getAsDoubleValue();
	}

	default char getChar(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsChar() : value.getAsChar();
	}

	@Nullable
	default Character getCharacter(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsCharacter() : value.getAsCharacter();
	}

	@Nullable
	default String getString(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsString() : value.getAsString();
	}

	@Nullable
	default BigInteger getBigInteger(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsBigInteger() : value.getAsBigInteger();
	}

	@Nullable
	default BigDecimal getBigDecimal(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsBigDecimal() : value.getAsBigDecimal();
	}

	@Nullable
	default Number getNumber(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsNumber() : value.getAsNumber();
	}

	@Nullable
	default Class<?> getClass(K key) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsClass() : value.getAsClass();
	}

	@Nullable
	default Enum<?> getEnum(K key, Class<?> enumType) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsEnum(enumType) : value.getAsEnum(enumType);
	}

	@Nullable
	default <T> T getObject(K key, Class<? extends T> type) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsObject(type) : value.getAsObject(type);
	}

	@Nullable
	default Object getObject(K key, Type type) {
		Value value = getValue(key);
		return value == null ? getDefaultValue(key).getAsObject(type) : value.getAsObject(type);
	}

	@Nullable
	default Object getValue(K key, Type type, @Nullable Object defaultValue) {
		Object v;
		if (ClassUtils.isPrimitive(type)) {
			v = getObject(key, ClassUtils.resolvePrimitiveIfNecessary((Class<?>) type));
		} else {
			v = getObject(key, type);
		}

		return v == null ? (defaultValue == null ? getDefaultValue(key).getAsObject(type) : defaultValue) : v;
	}

	default Object getValue(K key, Type type, @Nullable Supplier<?> defaultValue) {
		Object v;
		if (ClassUtils.isPrimitive(type)) {
			v = getObject(key, ClassUtils.resolvePrimitiveIfNecessary((Class<?>) type));
		} else {
			v = getObject(key, type);
		}

		return v == null ? (defaultValue == null ? getDefaultValue(key).getAsObject(type) : defaultValue.get()) : v;
	}

	@Nullable
	default <T> T getValue(K key, Class<? extends T> type, T defaultValue) {
		@SuppressWarnings("unchecked")
		T v = (T) getObject(key, ClassUtils.resolvePrimitiveIfNecessary(type));
		return v == null ? (defaultValue == null ? getDefaultValue(key).getAsObject(type) : defaultValue) : v;
	}

	@Nullable
	default <T> T getValue(K key, Class<? extends T> type, Supplier<? extends T> defaultValue) {
		@SuppressWarnings("unchecked")
		T v = (T) getObject(key, ClassUtils.resolvePrimitiveIfNecessary(type));
		return v == null ? (defaultValue == null ? getDefaultValue(key).getAsObject(type) : defaultValue.get()) : v;
	}

	default boolean containsKey(K key) {
		return getValue(key) != null;
	}

	@Override
	default EventRegistration registerListener(K name, EventListener<ChangeEvent<K>> eventListener) {
		return EventRegistration.EMPTY;
	}

	default Observable<Value> getObservableValue(K key) {
		return new ObservableValue<K, Value>(this, key, Value.class, null);
	}

	default Observable<Value> getObservableValue(K key, @Nullable Value defaultValue) {
		return new ObservableValue<K, Value>(this, key, Value.class, defaultValue);
	}

	default Observable<Value> getObservableValue(K key, @Nullable Supplier<? extends Value> defaultValue) {
		return new ObservableValue<K, Value>(this, key, Value.class, defaultValue);
	}

	default <T> Observable<T> getObservableValue(K key, Class<? extends T> type) {
		return new ObservableValue<K, T>(this, key, type, null);
	}

	default <T> Observable<T> getObservableValue(K key, Class<? extends T> type, T defaultValue) {
		return new ObservableValue<K, T>(this, key, type, defaultValue);
	}

	default <T> Observable<T> getObservableValue(K key, Class<? extends T> type, Supplier<? extends T> defaultValue) {
		return new ObservableValue<K, T>(this, key, type, defaultValue);
	}

	default Observable<Object> getObservableValue(K key, Type type) {
		return new ObservableValue<K, Object>(this, key, type, null);
	}

	default Observable<Object> getObservableValue(K key, Type type, Object defaultValue) {
		return new ObservableValue<K, Object>(this, key, type, defaultValue);
	}

	default Observable<Object> getObservableValue(K key, Type type, Supplier<?> defaultValue) {
		return new ObservableValue<K, Object>(this, key, type, defaultValue);
	}
}
