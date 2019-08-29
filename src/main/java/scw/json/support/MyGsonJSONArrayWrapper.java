package scw.json.support;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;

import scw.json.JSONArray;
import scw.json.JSONObject;
import scw.json.gson.Gson;
import scw.json.gson.JsonArray;
import scw.json.gson.JsonElement;

public class MyGsonJSONArrayWrapper extends AbstractJSONArray {
	private static final long serialVersionUID = 1L;
	private final Gson gson;
	private final JsonArray jsonArray;

	public MyGsonJSONArrayWrapper(JsonArray jsonArray, Gson gson) {
		this.jsonArray = jsonArray;
		this.gson = gson;
	}

	public String getString(int index) {
		if (jsonArray == null) {
			return null;
		}

		JsonElement jsonElement = jsonArray.get(index);
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return null;
		}

		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsString();
		}

		return jsonElement.toString();
	}

	public JSONObject getJSONObject(int index) {
		if (jsonArray == null) {
			return null;
		}

		JsonElement element = jsonArray.get(index);
		if (element == null || element.isJsonNull() || !element.isJsonObject()) {
			return null;
		}

		return new MyGsonJSONObjectWrapper(element.getAsJsonObject(), gson);
	}

	public JSONArray getJSONArray(int index) {
		if (jsonArray == null) {
			return null;
		}

		JsonElement element = jsonArray.get(index);
		if (element == null || element.isJsonNull() || !element.isJsonArray()) {
			return null;
		}

		return new MyGsonJSONArrayWrapper(element.getAsJsonArray(), gson);
	}

	public <T> T getObject(int index, Class<T> type) {
		if (jsonArray == null) {
			return null;
		}

		JsonElement element = jsonArray.get(index);
		if (element == null) {
			return null;
		}

		return gson.fromJson(element, type);
	}

	public <T> T getObject(int index, Type type) {
		if (jsonArray == null) {
			return null;
		}

		JsonElement element = jsonArray.get(index);
		if (element == null) {
			return null;
		}

		return gson.fromJson(element, type);
	}

	public String toJSONString() {
		return jsonArray.toString();
	}

	public int size() {
		return jsonArray == null ? 0 : jsonArray.size();
	}

	public boolean isEmpty() {
		return jsonArray == null || jsonArray.size() == 0;
	}

	public Object get(int index) {
		return jsonArray == null ? null : jsonArray.get(index);
	}

	public Iterator<?> iterator() {
		return jsonArray == null ? Collections.emptyIterator() : jsonArray.iterator();
	}

	public void add(Object element) {
		if (jsonArray == null) {
			return;
		}

		jsonArray.add(gson.toJsonTree(element));
	}

	public void remove(int index) {
		if (jsonArray == null) {
			return;
		}

		jsonArray.remove(index);
	}

}
