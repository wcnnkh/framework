package scw.core;

public interface PrimitiveTypeValueFactory<T> {
	Byte getByte(T data);

	byte getByteValue(T data);

	Short getShort(T data);

	short getShortValue(T data);

	Integer getInteger(T data);

	int getIntValue(T data);

	Long getLong(T data);

	long getLongValue(T data);

	Boolean getBoolean(T data);

	boolean getBooleanValue(T data);

	Float getFloat(T data);

	float getFloatValue(T data);

	Double getDouble(T data);

	double getDoubleValue(T data);

	char getChar(T data);

	Character getCharacter(T data);
}
