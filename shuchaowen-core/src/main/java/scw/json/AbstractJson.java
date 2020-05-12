package scw.json;

import scw.value.DefaultValueFactory;

public abstract class AbstractJson<K> extends DefaultValueFactory<K> {
	
	public abstract JsonElement get(K key);

	@Override
	public JsonElement getDefaultValue(K key) {
		return DefaultJsonElementValue.DEFAULT_JSON_ELEMENT_VALUE;
	}

	public abstract int size();

	public JsonObject getJsonObject(K key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonObject() : jsonElement.getAsJsonObject();
	}

	public JsonArray getJsonArray(K key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonArray() : jsonElement.getAsJsonArray();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public abstract String toJsonString();

	@Override
	public String toString() {
		return toJsonString();
	}
}
