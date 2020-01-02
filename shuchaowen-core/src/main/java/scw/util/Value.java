package scw.util;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface Value {
	<T> T getAsObject(Class<? extends T> type);

	Object getAsObject(Type type);

	String getAsString();

	Byte getAsByte();

	byte getAsByteValue();

	Short getAsShort();

	short getAsShortValue();

	Integer getAsInteger();

	int getAsIntValue();

	Long getAsLong();

	long getAsLongValue();

	Boolean getAsBoolean();

	boolean getAsBooleanValue();

	Float getAsFloat();

	float getAsFloatValue();

	Double getAsDouble();

	double getAsDoubleValue();

	char getAsChar();

	Character getAsCharacter();

	BigInteger getAsBigInteger();

	BigDecimal getAsBigDecimal();

	Number getAsNumber();

	Class<?> getAsClass();

	Enum<?> getAsEnum(Class<?> enumType);
}
