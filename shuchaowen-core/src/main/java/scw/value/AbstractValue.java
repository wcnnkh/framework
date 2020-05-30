package scw.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.lang.NotSupportedException;

public abstract class AbstractValue implements Value {
	@SuppressWarnings("unchecked")
	public <T> T getAsObject(Class<? extends T> type) {
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
			v = getAsObjectNotSupport(type);
		}
		return (T) v;
	}

	protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
		throw new NotSupportedException(type.toString());
	}

	protected <T> T getAsObjectNotSupport(Type type) {
		throw new NotSupportedException(type.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAsObject(Type type) {
		if (type instanceof Class) {
			return getAsObject((Class) type);
		}

		return getAsObjectNotSupport(type);
	}
}
