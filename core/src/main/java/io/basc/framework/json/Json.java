package io.basc.framework.json;

import io.basc.framework.value.ValueFactory;

public interface Json<K> extends ValueFactory<K>, JSONAware {
	int size();

	default boolean isEmpty() {
		return size() == 0;
	}

	default JsonElement getDefaultValue(K key) {
		return EmptyJsonElement.INSTANCE;
	};

	JsonElement getValue(K key);

	default JsonArray getJsonArray(K key) {
		JsonElement jsonElement = getValue(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonArray() : jsonElement.getAsJsonArray();
	}

	default JsonObject getJsonObject(K key) {
		JsonElement jsonElement = getValue(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonObject() : jsonElement.getAsJsonObject();
	}
}
