package io.basc.framework.value;

import io.basc.framework.core.utils.ObjectUtils;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.Observable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public class ValueFactoryWrapper<K, F extends ValueFactory<K>> implements ValueFactory<K> {
	protected final F targetFactory;

	public ValueFactoryWrapper(F targetFactory) {
		this.targetFactory = targetFactory;
	}

	@Override
	public Value getValue(K key) {
		return targetFactory.getValue(key);
	}

	public Value getDefaultValue(K key) {
		return targetFactory.getDefaultValue(key);
	}

	public Byte getByte(K key) {
		return targetFactory.getByte(key);
	}

	public byte getByteValue(K key) {
		return targetFactory.getByteValue(key);
	}

	public Short getShort(K key) {
		return targetFactory.getShort(key);
	}

	public short getShortValue(K key) {
		return targetFactory.getShortValue(key);
	}

	public Integer getInteger(K key) {
		return targetFactory.getInteger(key);
	}

	public int getIntValue(K key) {
		return targetFactory.getIntValue(key);
	}

	public Long getLong(K key) {
		return targetFactory.getLong(key);
	}

	public long getLongValue(K key) {
		return targetFactory.getLongValue(key);
	}

	public Boolean getBoolean(K key) {
		return targetFactory.getBoolean(key);
	}

	public boolean getBooleanValue(K key) {
		return targetFactory.getBooleanValue(key);
	}

	public Float getFloat(K key) {
		return targetFactory.getFloat(key);
	}

	public float getFloatValue(K key) {
		return targetFactory.getFloatValue(key);
	}

	public Double getDouble(K key) {
		return targetFactory.getDouble(key);
	}

	public double getDoubleValue(K key) {
		return targetFactory.getDoubleValue(key);
	}

	public char getChar(K key) {
		return targetFactory.getChar(key);
	}

	public Character getCharacter(K key) {
		return targetFactory.getCharacter(key);
	}

	public String getString(K key) {
		return targetFactory.getString(key);
	}

	public BigInteger getBigInteger(K key) {
		return targetFactory.getBigInteger(key);
	}

	public BigDecimal getBigDecimal(K key) {
		return targetFactory.getBigDecimal(key);
	}

	public Number getNumber(K key) {
		return targetFactory.getNumber(key);
	}

	public Class<?> getClass(K key) {
		return targetFactory.getClass(key);
	}

	public Enum<?> getEnum(K key, Class<?> enumType) {
		return targetFactory.getEnum(key, enumType);
	}

	public <T> T getObject(K key, Class<? extends T> type) {
		return targetFactory.getObject(key, type);
	}

	public Object getObject(K key, Type type) {
		return targetFactory.getObject(key, type);
	}

	public Object getValue(K key, Type type, Object defaultValue) {
		return targetFactory.getValue(key, type, defaultValue);
	}

	public <T> T getValue(K key, Class<? extends T> type, T defaultValue) {
		return targetFactory.getValue(key, type, defaultValue);
	}

	public boolean containsKey(K key) {
		return targetFactory.containsKey(key);
	}

	@Override
	public Observable<Value> getObservableValue(K key) {
		return targetFactory.getObservableValue(key);
	}

	@Override
	public <T> Observable<T> getObservableValue(K key, Class<? extends T> type) {
		return targetFactory.getObservableValue(key, type);
	}

	@Override
	public <T> Observable<T> getObservableValue(K key, Class<? extends T> type, T defaultValue) {
		return targetFactory.getObservableValue(key, type, defaultValue);
	}

	@Override
	public Observable<Object> getObservableValue(K key, Type type) {
		return targetFactory.getObservableValue(key, type);
	}

	@Override
	public Observable<Object> getObservableValue(K key, Type type, Object defaultValue) {
		return targetFactory.getObservableValue(key, type, defaultValue);
	}

	@Override
	public Observable<Value> getObservableValue(K key, Value defaultValue) {
		return targetFactory.getObservableValue(key, defaultValue);
	}

	@Override
	public EventRegistration registerListener(K name, EventListener<ChangeEvent<K>> eventListener) {
		return targetFactory.registerListener(name, eventListener);
	}

	@Override
	public String toString() {
		return targetFactory.toString();
	}

	@Override
	public int hashCode() {
		return targetFactory.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ValueFactoryWrapper) {
			return ObjectUtils.nullSafeEquals(((ValueFactoryWrapper) obj).targetFactory, targetFactory);
		}

		return ObjectUtils.nullSafeEquals(obj, targetFactory);
	}
	
	@Override
	public <T> Observable<T> getObservableValue(K key, Class<? extends T> type, Supplier<? extends T> defaultValue) {
		return targetFactory.getObservableValue(key, type, defaultValue);
	}
	
	@Override
	public Observable<Value> getObservableValue(K key, Supplier<? extends Value> defaultValue) {
		return targetFactory.getObservableValue(key, defaultValue);
	}
	
	@Override
	public Observable<Object> getObservableValue(K key, Type type, Supplier<?> defaultValue) {
		return targetFactory.getObservableValue(key, type, defaultValue);
	}
	
	@Override
	public <T> T getValue(K key, Class<? extends T> type, Supplier<? extends T> defaultValue) {
		return targetFactory.getValue(key, type, defaultValue);
	}
	
	@Override
	public Object getValue(K key, Type type, Supplier<?> defaultValue) {
		return targetFactory.getValue(key, type, defaultValue);
	}
}
