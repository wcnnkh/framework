package io.basc.framework.json;

import io.basc.framework.value.ValueFactory;

public interface Json<K> extends ValueFactory<K>, JsonAware {
	int size();

	default boolean isEmpty() {
		return size() == 0;
	}

	JsonElement get(K key);

	default JsonArray getJsonArray(K key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.getAsJsonArray();
	}

	default JsonObject getJsonObject(K key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.getAsJsonObject();
	}
}
