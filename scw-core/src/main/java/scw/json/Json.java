package scw.json;

import scw.value.ValueFactory;

public interface Json<K> extends ValueFactory<K> {
	int size();

	boolean isEmpty();

	JsonElement get(K key);

	JsonArray getJsonArray(K key);

	JsonObject getJsonObject(K key);

	String toJsonString();
}
