package scw.value;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.ResolvableType;
import scw.lang.Nullable;

public abstract class AbstractValue implements SimpleValue, Serializable {
	private static final long serialVersionUID = 1L;
	private final Value defaultValue;

	public AbstractValue(@Nullable Value defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Value getDefaultValue() {
		return defaultValue == null ? SimpleValue.super.getDefaultValue()
				: defaultValue;
	}

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
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			v = getAsBigDecimal();
		} else if (BigInteger.class.isAssignableFrom(type)) {
			v = getAsBigInteger();
		} else if (Number.class.isAssignableFrom(type)) {
			v = getAsNumber();
		} else if (Class.class == type) {
			v = getAsClass();
		} else if (type.isEnum()) {
			v = getAsEnum(type);
		} else if (type == Value.class) {
			v = this;
		} else {
			v = getAsNonBaseType(ResolvableType.forClass(type));
			if (v == null) {
				v = getDefaultValue().getAsObject(type);
			}
		}
		return (T) v;
	}

	public final Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class<?>) type);
		}
		Object v = getAsNonBaseType(ResolvableType.forType(type));
		if (v == null) {
			v = getDefaultValue().getAsObject(type);
		}
		return v;
	}

	public final Object getAsObject(ResolvableType type) {
		Class<?> rawClass = type.getRawClass();
		if (Value.isBaseType(rawClass)) {
			return getAsObject(rawClass);
		}

		Object v = getAsNonBaseType(type);
		if (v == null) {
			v = getDefaultValue().getAsObject(type);
		}
		return v;
	}

	@Override
	public String toString() {
		return getAsString();
	}

	protected abstract Object getAsNonBaseType(ResolvableType type);
}
