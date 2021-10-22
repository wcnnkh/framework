package io.basc.framework.value;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;

public class AnyValue extends AbstractValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient ConversionService conversionService;
	private final Object value;

	public AnyValue(Object value) {
		this(value, null);
	}

	public AnyValue(Object value, @Nullable ConversionService conversionService) {
		this.value = value;
		this.conversionService = conversionService;
	}

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public String getAsString() {
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsString();
		}
		
		return value.toString();
	}

	public Byte getAsByte() {
		if (value == null) {
			return null;
		}

		if (value instanceof Byte) {
			return (Byte) value;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsByte();
		}
		return super.getAsByte();
	}

	public byte getAsByteValue() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Byte) {
			return (Byte) value;
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsByteValue();
		}
		return super.getAsByteValue();
	}

	public Short getAsShort() {
		if (value == null) {
			return null;
		}

		if (value instanceof Short) {
			return (Short) value;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsShort();
		}
		return super.getAsShort();
	}

	public short getAsShortValue() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Short) {
			return (Short) value;
		}

		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsShortValue();
		}
		return super.getAsShortValue();
	}

	public Integer getAsInteger() {
		if (value == null) {
			return null;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsInteger();
		}
		return super.getAsInteger();
	}

	public int getAsIntValue() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsIntValue();
		}
		return super.getAsIntValue();
	}

	public Long getAsLong() {
		if (value == null) {
			return null;
		}

		if (value instanceof Long) {
			return (Long) value;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsLong();
		}
		return super.getAsLong();
	}

	public long getAsLongValue() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Long) {
			return (Long) value;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsLongValue();
		}
		return super.getAsLongValue();
	}

	public Boolean getAsBoolean() {
		if (value == null) {
			return null;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBoolean();
		}
		return super.getAsBoolean();
	}

	public boolean getAsBooleanValue() {
		if (value == null) {
			return false;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue() == 1;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBooleanValue();
		}
		return super.getAsBooleanValue();
	}

	public Float getAsFloat() {
		if (value == null) {
			return null;
		}

		if (value instanceof Float) {
			return (Float) value;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsFloat();
		}
		return super.getAsFloat();
	}

	public float getAsFloatValue() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Float) {
			return (Float) value;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsFloatValue();
		}
		return super.getAsFloatValue();
	}

	public Double getAsDouble() {
		if (value == null) {
			return null;
		}

		if (value instanceof Double) {
			return (Double) value;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsDouble();
		}
		return super.getAsDouble();
	}

	public double getAsDoubleValue() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Double) {
			return (Double) value;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsDoubleValue();
		}
		return super.getAsDoubleValue();
	}

	public char getAsChar() {
		if (value == null) {
			return 0;
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsChar();
		}
		return super.getAsChar();
	}

	public Character getAsCharacter() {
		if (value == null) {
			return null;
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsCharacter();
		}
		return super.getAsCharacter();
	}

	public BigInteger getAsBigInteger() {
		if (value == null) {
			return null;
		}

		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}

		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).toBigInteger();
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBigInteger();
		}
		return super.getAsBigInteger();
	}

	public BigDecimal getAsBigDecimal() {
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}

		if (value instanceof Number) {
			return new BigDecimal(((Number) value).doubleValue());
		}

		if (value instanceof Value) {
			return ((Value) value).getAsBigDecimal();
		}
		return super.getAsBigDecimal();
	}

	public Number getAsNumber() {
		if (value == null) {
			return null;
		}

		if (value instanceof Number) {
			return (Number) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsNumber();
		}
		return super.getAsNumber();
	}

	public Class<?> getAsClass() {
		if (value == null) {
			return null;
		}

		if (value instanceof Class) {
			return (Class<?>) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsClass();
		}
		return super.getAsClass();
	}

	public Enum<?> getAsEnum(Class<?> enumType) {
		if (value == null) {
			return null;
		}

		if (value instanceof Enum<?>) {
			return (Enum<?>) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsEnum(enumType);
		}

		return super.getAsEnum(enumType);
	}

	@Override
	protected Object getAsNonBaseType(TypeDescriptor type) {
		if (value == null) {
			return null;
		}

		if (type.getType().isInstance(value)) {
			return value;
		}

		Class<?> rawClass = type.getType();
		if (rawClass == Object.class || rawClass == null) {
			return value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsObject(type);
		}
		return getConversionService().convert(value, TypeDescriptor.forObject(value), type);
	}

	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (value == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof AnyValue) {
			return ObjectUtils.nullSafeEquals(value, ((AnyValue) obj).value);
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	public boolean isEmpty() {
		if (value == null) {
			return true;
		}

		if (value instanceof Value) {
			return ((Value) value).isEmpty();
		}

		if (value instanceof Collection) {
			return ((Collection) value).isEmpty();
		}

		if (value instanceof Map) {
			return ((Map) value).isEmpty();
		}

		if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		}
		
		if ("".equals(value)) {
			return true;
		}
		return super.isEmpty();
	}

	public boolean isNumber() {
		if (isEmpty()) {
			return false;
		}

		if (value instanceof Number) {
			return true;
		}

		if (value instanceof Value) {
			return ((Value) value).isNumber();
		}
		return super.isNumber();
	}

	@Override
	public Object getSourceValue() {
		if(value == null) {
			return null;
		}
		
		if(value instanceof Value) {
			return ((Value) value).getSourceValue();
		}
		return value;
	}
}
