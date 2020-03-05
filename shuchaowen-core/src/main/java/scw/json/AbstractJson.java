package scw.json;

import scw.util.value.AbstractValueFactory;

public abstract class AbstractJson<K> extends AbstractValueFactory<K, JsonElement> {

	@Override
	public JsonElement getDefaultValue() {
		return DefaultJsonElementValue.DEFAULT_JSON_ELEMENT_VALUE;
	}

	public abstract int size();

	public JsonObject getJsonObject(K key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? getDefaultValue().getAsJsonObject() : jsonElement.getAsJsonObject();
	}

	public JsonArray getJsonArray(K key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? getDefaultValue().getAsJsonArray() : jsonElement.getAsJsonArray();
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
