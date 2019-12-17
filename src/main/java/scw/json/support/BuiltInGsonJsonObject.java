package scw.json.support;

import java.lang.reflect.Type;
import java.util.Collection;

import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.json.gson.Gson;

public class BuiltInGsonJsonObject implements JsonObject {
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

	public JsonObject getJsonObject(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseJsonObject();
	}

	public JsonArray getJsonArray(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseJsonArray();
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

	public <T> T getObject(String key, Class<? extends T> type) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseObject(type);
	}

	public Object getObject(String key, Type type) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseObject(type);
	}

	@Override
	public String toString() {
		return toJsonString();
	}

	public String getString(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseString();
	}

	public int getIntValue(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? 0 : jsonElement.parseIntValue();
	}

	public boolean getBooleanValue(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? false : jsonElement.parseBooleanValue();
	}

	public Long getLong(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseLong();
	}

	public long getLongValue(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? 0 : jsonElement.parseLongValue();
	}

	public Integer getInteger(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseInteger();
	}
}
