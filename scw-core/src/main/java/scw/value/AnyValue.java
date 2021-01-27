package scw.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.support.JsonConversionService;
import scw.core.ResolvableType;
import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;

public class AnyValue extends SupportDefaultValue {
	private volatile ConversionService conversionService;
	private final Object value;

	public AnyValue(Object value) {
		this(value, EmptyValue.INSTANCE);
	}
	
	public AnyValue(Object value, ConversionService conversionService) {
		this(value, EmptyValue.INSTANCE, conversionService);
	}
	
	public AnyValue(Object value, Value defaultValue){
		this(value, defaultValue, null);
	}

	public AnyValue(Object value, Value defaultValue, ConversionService conversionService) {
		super(defaultValue);
		this.value = value;
		this.conversionService = conversionService;
	}

	public ConversionService getConversionService() {
		if(conversionService == null){
			synchronized (this) {
				if(conversionService == null){
					conversionService = new JsonConversionService();
				}
			}
		}
		return conversionService;
	}

	public Object getValue() {
		if (value instanceof AnyValue) {
			return ((AnyValue) value).getValue();
		}
		return value;
	}

	public String getAsString() {
		if (value == null) {
			return getDefaultValue().getAsString();
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
			return getDefaultValue().getAsByte();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsByte();
	}

	public byte getAsByteValue() {
		if (value == null) {
			return getDefaultValue().getAsByteValue();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsByteValue();
	}

	public Short getAsShort() {
		if (value == null) {
			return getDefaultValue().getAsShort();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsShort();
	}

	public short getAsShortValue() {
		if (value == null) {
			return getDefaultValue().getAsShortValue();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsShortValue();
	}

	public Integer getAsInteger() {
		if (value == null) {
			return getDefaultValue().getAsInteger();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsInteger();
	}

	public int getAsIntValue() {
		if (value == null) {
			return getDefaultValue().getAsIntValue();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsIntValue();
	}

	public Long getAsLong() {
		if (value == null) {
			return getDefaultValue().getAsLong();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsLong();
	}

	public long getAsLongValue() {
		if (value == null) {
			return getDefaultValue().getAsLongValue();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsLongValue();
	}

	public Boolean getAsBoolean() {
		if (value == null) {
			return getDefaultValue().getAsBoolean();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsBoolean();
	}

	public boolean getAsBooleanValue() {
		if (value == null) {
			return getDefaultValue().getAsBooleanValue();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsBoolean();
	}

	public Float getAsFloat() {
		if (value == null) {
			return getDefaultValue().getAsFloat();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsFloat();
	}

	public float getAsFloatValue() {
		if (value == null) {
			return getDefaultValue().getAsFloatValue();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsFloatValue();
	}

	public Double getAsDouble() {
		if (value == null) {
			return getDefaultValue().getAsDouble();
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

		return new StringValue(getAsString(), getDefaultValue()).getAsDouble();
	}

	public double getAsDoubleValue() {
		if (value == null) {
			return getDefaultValue().getAsDoubleValue();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsDoubleValue();
	}

	public char getAsChar() {
		if (value == null) {
			return getDefaultValue().getAsChar();
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsChar();
		}

		return new StringValue(getAsString(), getDefaultValue()).getAsChar();
	}

	public Character getAsCharacter() {
		if (value == null) {
			return getDefaultValue().getAsCharacter();
		}

		if (value instanceof Character) {
			return (Character) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsCharacter();
		}

		return new StringValue(getAsString(), getDefaultValue())
				.getAsCharacter();
	}

	public BigInteger getAsBigInteger() {
		if (value == null) {
			return getDefaultValue().getAsBigInteger();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsBigInteger();
	}

	public BigDecimal getAsBigDecimal() {
		if (value == null) {
			return getDefaultValue().getAsBigDecimal();
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

		return new StringValue(getAsString(), getDefaultValue())
				.getAsBigDecimal();
	}

	public Number getAsNumber() {
		if (value == null) {
			return getDefaultValue().getAsNumber();
		}

		if (value instanceof Number) {
			return (Number) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsNumber();
		}

		return new StringValue(getAsString(), getDefaultValue()).getAsNumber();
	}

	public Class<?> getAsClass() {
		if (value == null) {
			return getDefaultValue().getAsClass();
		}

		if (value instanceof Class) {
			return (Class<?>) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsClass();
		}

		return new StringValue(getAsString(), getDefaultValue()).getAsClass();
	}

	public Enum<?> getAsEnum(Class<?> enumType) {
		if (value == null) {
			return getDefaultValue().getAsEnum(enumType);
		}

		if (value instanceof Enum<?>) {
			return (Enum<?>) value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsEnum(enumType);
		}

		return new StringValue(getAsString(), getDefaultValue())
				.getAsEnum(enumType);
	}

	@Override
	protected Object getAsObjectNotSupport(ResolvableType type,
			Class<?> rawClass) {
		return getConversionService().convert(value, TypeDescriptor.forObject(value), TypeDescriptor.valueOf(type));
	}
	
	@Override
	public Object getAsObject(ResolvableType resolvableType) {
		if (value == null) {
			return getDefaultValue().getAsObject(resolvableType);
		}

		Class<?> type = resolvableType.getRawClass();
		if (type == Object.class || type == null) {
			return value;
		}

		if (value instanceof Value) {
			return ((Value) value).getAsObject(type);
		}
		return super.getAsObject(resolvableType);
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

		return obj.equals(value);
	}

	public boolean isEmpty() {
		if (value == null) {
			return true;
		}

		if (value instanceof CharSequence) {
			return StringUtils.isEmpty((CharSequence) value);
		}

		if (value instanceof Value) {
			return ((Value) value).isEmpty();
		}
		return false;
	}

	public boolean isNumber() {
		if (isEmpty()) {
			return false;
		}

		if (value instanceof Number) {
			return true;
		}
		
		if(value instanceof String){
			StringValue textValue = new StringValue((String)value);
			return textValue.isNumber();
		}

		if (value instanceof CharSequence) {
			return StringUtils.isNumeric((CharSequence) value);
		}

		if (value instanceof Value) {
			return ((Value) value).isNumber();
		}
		return false;
	}

	@Override
	public String toString() {
		return getAsString();
	}
}
