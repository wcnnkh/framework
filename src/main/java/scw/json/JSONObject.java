package scw.json;

import java.io.Serializable;
import java.util.Map;

public interface JSONObject extends Map<String, Object>, Serializable {

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

	JSONObject getJSONObject(String key);

	JSONArray getJSONArray(String key);

	<T> T getObject(String key, Class<T> type);

	String toJSONString();
}
