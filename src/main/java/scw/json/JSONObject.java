package scw.json;

import java.util.Set;

public interface JSONObject {
	
	String getString(String key);

	byte getByteValue(String key);

	Byte getByte(String key);

	short getShortValue(String key);

	Short getShort(String key);

	int getIntValue(String key);

	Integer getInteger(String key);

	long getLongValue(String key);

	Long getLong(String key);

	float getFloatValue(String key);

	Float getFloat(String key);

	double getDoubleValue(String key);

	Double getDouble(String key);

	JSONObject getJsonObject(String key);

	JSONArray getJsonArray(String key);

	<T> T getObject(String key, Class<T> type);

	Set<String> keySet();
	
	String toJSONString();
}
