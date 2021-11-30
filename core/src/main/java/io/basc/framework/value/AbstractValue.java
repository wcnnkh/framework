package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.convert.TypeDescriptor;

public abstract class AbstractValue implements BaseValue {

	@SuppressWarnings("unchecked")
	public final <T> T getAsObject(Class<T> type) {
		Object v = null;
		if (String.class == type) {
			v = getAsString();
		} else if (int.class == type) {
			v = getAsIntValue();
		} else if (Integer.class == type) {
			v = getAsInteger();
		} else if (long.class == type) {
			v = getAsLongValue();
		} else if (Long.class == type) {
			v = getAsLong();
		} else if (float.class == type) {
			v = getAsFloatValue();
		} else if (Float.class == type) {
			v = getAsFloat();
		} else if (double.class == type) {
			v = getAsDoubleValue();
		} else if (Double.class == type) {
			v = getAsDouble();
		} else if (short.class == type) {
			v = getAsShortValue();
		} else if (Short.class == type) {
			v = getAsShort();
		} else if (boolean.class == type) {
			v = getAsBooleanValue();
		} else if (Boolean.class == type) {
			v = getAsBoolean();
		} else if (byte.class == type) {
			v = getAsByteValue();
		} else if (Byte.class == type) {
			v = getAsByte();
		} else if (char.class == type) {
			v = getAsChar();
		} else if (Character.class == type) {
			v = getAsCharacter();
		} else if (BigDecimal.class == type) {
			v = getAsBigDecimal();
		} else if (BigInteger.class == type) {
			v = getAsBigInteger();
		} else if (Number.class == type) {
			v = getAsNumber();
		} else if (Class.class == type) {
			v = getAsClass();
		} else if (type.isEnum()) {
			v = getAsEnum(type);
		} else if (type == Value.class) {
			v = this;
		} else {
			v = getAsNonBaseType(TypeDescriptor.valueOf(type));
		}
		return (T) v;
	}

	public final Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class<?>) type);
		}
		return getAsNonBaseType(TypeDescriptor.valueOf(type));
	}

	public final Object getAsObject(TypeDescriptor type) {
		Class<?> rawClass = type.getType();
		if (ValueUtils.isBaseType(rawClass)) {
			return getAsObject(rawClass);
		}

		return getAsNonBaseType(type);
	}

	@Override
	public String toString() {
		return getAsString();
	}

	protected abstract Object getAsNonBaseType(TypeDescriptor type);
}
