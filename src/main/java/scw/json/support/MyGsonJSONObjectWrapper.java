package scw.json.support;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import scw.json.JSONArray;
import scw.json.JSONObject;
import scw.json.gson.Gson;
import scw.json.gson.JsonElement;
import scw.json.gson.JsonObject;

public class MyGsonJSONObjectWrapper extends AbstractJSONObject {
	private static final long serialVersionUID = 1L;
	private final Gson gson;
	private final JsonObject jsonObject;

	public MyGsonJSONObjectWrapper(JsonObject jsonObject, Gson gson) {
		this.jsonObject = jsonObject;
		this.gson = gson;
	}

	public void put(String key, Object value) {
		if (jsonObject == null) {
			return;
		}

		jsonObject.add(key, gson.toJsonTree(value));
	}

	public void remove(String key) {
		if (jsonObject == null) {
			return;
		}

		jsonObject.remove(key);
	}

	public String getString(String key) {
		if (jsonObject == null) {
			return null;
		}

		JsonElement element = jsonObject.get(key);
		if (element == null || element.isJsonNull()) {
			return null;
		}

		if (element.isJsonPrimitive()) {
			return element.getAsString();
		}

		return element.toString();
	}

	public JSONObject getJSONObject(String key) {
		if (jsonObject == null) {
			return null;
		}

		JsonElement element = jsonObject.get(key);
		if (element == null || !element.isJsonObject()) {
			return null;
		}

		return new MyGsonJSONObjectWrapper(element.getAsJsonObject(), gson);
	}

	public JSONArray getJSONArray(String key) {
		if (jsonObject == null) {
			return null;
		}

		JsonElement element = jsonObject.get(key);
		if (element == null || !element.isJsonArray()) {
			return null;
		}

		return new MyGsonJSONArrayWrapper(element.getAsJsonArray(), gson);
	}

	public <T> T getObject(String key, Class<T> type) {
		if (jsonObject == null) {
			return null;
		}

		JsonElement element = jsonObject.get(key);
		if (element == null) {
			return null;
		}

		return gson.fromJson(element, type);
	}

	public String toJSONString() {
		return jsonObject == null ? null : jsonObject.toString();
	}

	public <T> T getObject(String key, Type type) {
		if (jsonObject == null) {
			return null;
		}

		JsonElement element = jsonObject.get(key);
		if (element == null) {
			return null;
		}

		return gson.fromJson(element, type);
	}

	public int size() {
		return jsonObject == null ? 0 : jsonObject.size();
	}

	public boolean isEmpty() {
		return jsonObject == null || jsonObject.size() == 0;
	}

	public boolean containsKey(String key) {
		return jsonObject == null ? false : jsonObject.has(key);
	}

	public Object get(String key) {
		return jsonObject == null ? null : jsonObject.get(key);
	}

	@SuppressWarnings("unchecked")
	public Set<String> keySet() {
		return jsonObject == null ? Collections.EMPTY_SET : jsonObject.keySet();
	}

	public Iterator<String> keys() {
		return keySet().iterator();
	}

}
