package scw.json;

import scw.value.factory.ConvertibleValueFactory;

public interface Json<K> extends ConvertibleValueFactory<K>, JSONAware{
	int size();

	boolean isEmpty();
	
	JsonElement getValue(K key);

	JsonArray getJsonArray(K key);

	JsonObject getJsonObject(K key);
}
