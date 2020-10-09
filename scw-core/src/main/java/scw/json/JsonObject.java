package scw.json;

import java.util.Collection;

public interface JsonObject extends BasicJsonDefinition<String> {

	void put(String key, Object value);

	boolean containsKey(String key);

	Collection<String> keys();
}