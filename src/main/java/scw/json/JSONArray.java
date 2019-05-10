package scw.json;

public interface JSONArray {

	String getString(int index);

	byte getByteValue(int index);

	Byte getByte(int index);

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

	JSONObject getJsonObject(int index);

	JSONArray getJsonArray(int index);

	<T> T getObject(int index, Class<T> type);

	int size();
	
	String toJSONString();
}
