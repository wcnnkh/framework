package scw.value;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueFactory<K> extends BaseValueFactory<K>{
	Byte getByte(K key);

	byte getByteValue(K key);

	Short getShort(K key);

	short getShortValue(K key);

	Integer getInteger(K key);
	
	int getIntValue(K key);
	
	Long getLong(K key);

	long getLongValue(K key);
	
	Boolean getBoolean(K key);
	
	boolean getBooleanValue(K key);
	
	Float getFloat(K key);

	float getFloatValue(K key);

	Double getDouble(K key);

	double getDoubleValue(K key);

	char getChar(K key);

	Character getCharacter(K key);

	String getString(K key);

	BigInteger getBigInteger(K key);

	BigDecimal getBigDecimal(K key);

	Number getNumber(K key);

	Class<?> getClass(K key);

	Enum<?> getEnum(K key, Class<?> enumType);

	<T> T getObject(K key, Class<? extends T> type);

	Object getObject(K key, Type type);
	
	Object getValue(K key, Type type, Object defaultValue);
	
	<T> T getValue(K key, Class<? extends T> type, T defaultValue);
}
