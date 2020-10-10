package scw.json;

import java.util.Collection;

public interface JsonObject extends Json<String> {

	void put(String key, Object value);

	boolean containsKey(String key);

	Collection<String> keys();
}