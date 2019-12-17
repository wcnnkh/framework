package scw.json;

import java.lang.reflect.Type;

public interface JsonArray extends Iterable<JsonElement>{
	JsonElement get(int index);

	JsonObject getJsonObject(int index);

	JsonArray getJsonArray(int index);
	
	<T> T getObject(int index, Class<? extends T> type);
	
	Object getObject(int index, Type type);

	void add(Object value);

	int size();

	String toJsonString();
	
	String getString(int index);
	
	Byte getByte(int index);
}
