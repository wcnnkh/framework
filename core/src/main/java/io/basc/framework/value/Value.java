package io.basc.framework.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;

public interface Value {
	final Value[] EMPTY_ARRAY = new Value[0];
	
	/**
	 * 获取来源值
	 * @return
	 */
	@Nullable
	Object getSourceValue();

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
	 * @see #getAsNumber()
	 * @return
	 */
	boolean isNumber();

	boolean isEmpty();

	@Nullable
	Class<?> getAsClass();

	@Nullable
	Enum<?> getAsEnum(Class<?> enumType);
}
