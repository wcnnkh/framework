package scw.servlet;

import javax.servlet.ServletRequest;

public interface Request extends ServletRequest {
	String getString(String name);

	Byte getByte(String name);

	byte getByteValue(String name);

	Short getShort(String name);

	short getShortValue(String name);

	Integer getInteger(String name);

	int getIntValue(String name);

	Long getLong(String name);

	long getLongValue(String name);

	Boolean getBoolean(String key);

	boolean getBooleanValue(String name);

	Float getFloat(String name);

	float getFloatValue(String name);

	Double getDouble(String name);

	double getDoubleValue(String name);

	char getChar(String name);

	Character getCharacter(String name);

	long getCreateTime();

	<T> T getParameter(Class<T> type, String name);
	
	<T> T getBean(Class<T> type);

	<T> T getBean(Class<T> type, String name);
}
