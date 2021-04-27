package scw.json;


public abstract class AbstractJson<K> implements Json<K> {

	@Override
	public JsonElement getDefaultValue(K key) {
		return EmptyJsonElement.INSTANCE;
	}

	public JsonObject getJsonObject(K key) {
		JsonElement jsonElement = getValue(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonObject() : jsonElement.getAsJsonObject();
	}

	public JsonArray getJsonArray(K key) {
		JsonElement jsonElement = getValue(key);
		return jsonElement == null ? getDefaultValue(key).getAsJsonArray() : jsonElement.getAsJsonArray();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public String toString() {
		return toJSONString();
	}
}
