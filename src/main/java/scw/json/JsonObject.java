package scw.json;

import java.lang.reflect.Type;
import java.util.Collection;

public interface JsonObject {

	void put(String key, Object value);

	JsonObject getJsonObject(String key);

	JsonArray getJsonArray(String key);

	<T> T getObject(String key, Class<? extends T> type);

	Object getObject(String key, Type type);

	JsonElement get(String key);

	Collection<String> keys();

	boolean containsKey(String key);

	String toJsonString();

	String getString(String key);

	int getIntValue(String key);
	
	Integer getInteger(String key);

	boolean getBooleanValue(String key);

	Long getLong(String key);

	long getLongValue(String key);
}
