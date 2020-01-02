package scw.json.support;

import java.util.Collection;

import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.json.gson.Gson;

public final class BuiltInGsonJsonObject extends JsonObject {
	private scw.json.gson.JsonObject jsonObject;
	private Gson gson;

	public BuiltInGsonJsonObject(scw.json.gson.JsonObject jsonObject, Gson gson) {
		this.jsonObject = jsonObject;
		this.gson = gson;
	}

	public void put(String key, Object value) {
		if (value == null) {
			return;
		}

		jsonObject.add(key, gson.toJsonTree(value));
	}

	public JsonElement get(String key) {
		scw.json.gson.JsonElement jsonElement = jsonObject.get(key);
		return jsonElement == null ? null : new BuiltInGsonElement(jsonElement, gson);
	}

	public Collection<String> keys() {
		return jsonObject.keySet();
	}

	public boolean containsKey(String key) {
		return jsonObject.has(key);
	}

	public String toJsonString() {
		return jsonObject.toString();
	}

	@Override
	public int size() {
		return jsonObject.size();
	}
}
