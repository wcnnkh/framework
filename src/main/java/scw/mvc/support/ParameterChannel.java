package scw.mvc.support;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.mvc.Channel;

public interface ParameterChannel extends Channel {
	<T> T getBean(Type type);

	<T> T getBean(String name);

	<T> T getObject(Type type);

	String getString(String name);

	BigInteger getBigInteger(String name);

	BigDecimal getBigDecimal(String name);

	Class<?> getClass(String name);

	@SuppressWarnings("rawtypes")
	Enum getEnum(String name, Class<? extends Enum> type);

	<E> E[] getArray(String name, Class<E> type);

	Object getObject(String name, Class<?> type);

	Byte getByte(String name);

	byte getByteValue(String name);

	Short getShort(String name);

	short getShortValue(String name);

	Integer getInteger(String name);

	int getIntValue(String name);

	Long getLong(String name);

	long getLongValue(String name);

	Boolean getBoolean(String name);

	boolean getBooleanValue(String name);

	Float getFloat(String name);

	float getFloatValue(String name);

	Double getDouble(String name);

	double getDoubleValue(String name);

	char getChar(String name);

	Character getCharacter(String name);
}
