package scw.json;

import java.io.Serializable;

public interface JSONArrayReadOnly extends Serializable{
	String getString(int index);

	byte getByteValue(int index);

	Byte getByte(int index);
	
	boolean getBooleanValue(int index);
	
	Boolean getBoolean(int index);

	short getShortValue(int index);

	Short getShort(int index);

	int getIntValue(int index);

	Integer getInteger(int index);

	long getLongValue(int index);

	Long getLong(int index);

	float getFloatValue(int index);

	Float getFloat(int index);

	double getDoubleValue(int index);

	Double getDouble(int index);

	JSONObject getJSONObject(int index);

	JSONArray getJSONArray(int index);

	<T> T getObject(int index, Class<T> type);

	String toJSONString();
}
