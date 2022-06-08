package io.basc.framework.value;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StaticSupplier;

public class AnyValue extends AbstractValue implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private transient Converter<? super Object, ? extends Object, ? extends RuntimeException> converter;
	private Supplier<? extends Object> valueSupplier;
	private TypeDescriptor typeDescriptor;

	public AnyValue(Object value) {
		this(value, null, null);
	}

	public AnyValue(Object value,
			@Nullable Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
		this(value, null, converter);
	}

	public AnyValue(Object value, @Nullable TypeDescriptor typeDescriptor) {
		this(value, typeDescriptor, null);
	}

	public AnyValue(Object value, @Nullable TypeDescriptor typeDescriptor,
			@Nullable Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
		this(new StaticSupplier<>(value), typeDescriptor, converter);
	}

	public AnyValue(Supplier<? extends Object> valueSupplier, @Nullable TypeDescriptor typeDescriptor,
			@Nullable Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
		this.valueSupplier = valueSupplier;
		this.typeDescriptor = typeDescriptor;
		this.converter = converter;
	}

	public AnyValue(AnyValue value) {
		if (value != null) {
			this.valueSupplier = value.valueSupplier;
			this.typeDescriptor = value.typeDescriptor;
			this.converter = value.converter;
		}
	}

	@Override
	public final TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor != null) {
			return typeDescriptor;
		}

		return getTypeDescriptor(get());
	}

	protected TypeDescriptor getTypeDescriptor(Object value) {
		if (typeDescriptor != null) {
			return typeDescriptor;
		}

		if (value instanceof Value) {
			return ((Value) value).getTypeDescriptor();
		}

		if (value == null) {
			return TypeDescriptor.valueOf(Object.class);
		}
		return TypeDescriptor.forObject(value);
	}

	public Converter<? super Object, ? extends Object, ? extends RuntimeException> getConverter() {
		return converter == null ? Sys.env.getConversionService() : converter;
	}

	public String getAsString() {
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		Object value = get();
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
		return getConverter().convert(value, getTypeDescriptor(value), type);
	}

	@Override
	public <E extends Throwable> Object convert(TypeDescriptor targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		Object value = get();
		if (value == null) {
			return null;
		}

		Class<?> rawClass = targetType.getType();
		if (rawClass == Object.class || rawClass == null) {
			return value;
		}

		if (value instanceof Value) {
			return ((Value) value).convert(targetType, converter);
		}
		return converter.convert(value, getTypeDescriptor(value), targetType);
	}

	@Override
	public int hashCode() {
		Object value = get();
		return value == null ? super.hashCode() : value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Object value = get();
		if (value == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof AnyValue) {
			return ObjectUtils.equals(value, ((AnyValue) obj).get());
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	public boolean isEmpty() {
		Object value = get();
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

		Object value = get();
		if (value instanceof Number) {
			return true;
		}

		if (value instanceof Value) {
			return ((Value) value).isNumber();
		}
		return super.isNumber();
	}

	@Override
	public Object get() {
		if (valueSupplier == null) {
			return null;
		}

		Object value = valueSupplier.get();
		if (value == null) {
			return null;
		}

		if (typeDescriptor != null && ClassUtils.isAssignableValue(typeDescriptor.getType(), value)) {
			return value;
		}

		if (value instanceof Value) {
			return ((Value) value).get();
		}
		return value;
	}

	@Override
	public AnyValue clone() {
		return new AnyValue(this);
	}

	public void setValue(Object value) {
		setValueSupplier(new StaticSupplier<Object>(value));
	}

	public void setValueSupplier(Supplier<? extends Object> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	public void setConverter(Converter<? super Object, ? extends Object, ? extends RuntimeException> converter) {
		this.converter = converter;
	}

	public void setTypeDescriptor(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}
}
