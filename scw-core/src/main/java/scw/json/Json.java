package scw.json;

import scw.value.ValueFactory;

public interface Json<K> extends ValueFactory<K>, JSONAware {
	int size();

	boolean isEmpty();

	default JsonElement getDefaultValue(K key) {
		return EmptyJsonElement.INSTANCE;
	};

	JsonElement getValue(K key);

	default JsonArray getJsonArray(K key) {
		JsonElement jsonElement = getValue(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonArray()
				: jsonElement.getAsJsonArray();
	}

	default JsonObject getJsonObject(K key) {
		JsonElement jsonElement = getValue(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonObject()
				: jsonElement.getAsJsonObject();
	}
}
