package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassUtils;

public interface Value extends Supplier<Object> {
	final Value[] EMPTY_ARRAY = new Value[0];

	/**
	 * 获取来源值
	 * 
	 * @return
	 */
	@Nullable
	Object get();

	default TypeDescriptor getTypeDescriptor() {
		Object value = get();
		if (value instanceof Value) {
			return ((Value) value).getTypeDescriptor();
		}
		return TypeDescriptor.forObject(value);
	}

	@Nullable
	<T> T getAsObject(Class<T> type);

	@Nullable
	Object getAsObject(Type type);

	@Nullable
	Object getAsObject(TypeDescriptor type);

	@Nullable
	String getAsString();

	@Nullable
	Byte getAsByte();

	byte getAsByteValue();

	@Nullable
	Short getAsShort();

	short getAsShortValue();

	@Nullable
	Integer getAsInteger();

	int getAsIntValue();

	@Nullable
	Long getAsLong();

	long getAsLongValue();

	@Nullable
	Boolean getAsBoolean();

	boolean getAsBooleanValue();

	@Nullable
	Float getAsFloat();

	float getAsFloatValue();

	@Nullable
	Double getAsDouble();

	double getAsDoubleValue();

	char getAsChar();

	@Nullable
	Character getAsCharacter();

	@Nullable
	BigInteger getAsBigInteger();

	@Nullable
	BigDecimal getAsBigDecimal();

	@Nullable
	Number getAsNumber();

	/**
	 * 是否可以转换为number,此方法不代表数据的原始类型是number
	 * 
	 * @see #getAsNumber()
	 * @return
	 */
	boolean isNumber();

	boolean isEmpty();

	default boolean isNull() {
		return get() == null;
	}

	@Nullable
	Class<?> getAsClass();

	@Nullable
	Enum<?> getAsEnum(Class<?> enumType);

	/**
	 * 这并不是指基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Type type) {
		if (type == null) {
			return false;
		}

		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isEnum()) {
				return true;
			}
		}

		return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type == BigDecimal.class
				|| type == BigInteger.class || Number.class == type || type == Class.class;
	}
}
