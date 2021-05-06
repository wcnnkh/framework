package scw.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.ResolvableType;
import scw.core.utils.ClassUtils;
import scw.lang.Nullable;

public interface Value {
	final Value[] EMPTY_ARRAY = new Value[0];

	@Nullable
	<T> T getAsObject(Class<T> type);

	@Nullable
	Object getAsObject(Type type);

	@Nullable
	Object getAsObject(ResolvableType type);

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

	boolean isEmpty();

	boolean isNumber();

	@Nullable
	Class<?> getAsClass();

	@Nullable
	Enum<?> getAsEnum(Class<?> enumType);

	/**
	 * 这并不是基本数据类型，这是指Value可以直接转换的类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBaseType(Class<?> type) {
		if(type == null) {
			return false;
		}
		return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class
				|| Number.class.isAssignableFrom(type) || type.isEnum()
				|| type == Class.class;
	}
}
