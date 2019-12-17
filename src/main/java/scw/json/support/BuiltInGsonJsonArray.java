package scw.json.support;

import java.lang.reflect.Type;
import java.util.Iterator;

import scw.core.Converter;
import scw.core.utils.IteratorConvert;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.json.gson.Gson;

public class BuiltInGsonJsonArray implements scw.json.JsonArray {
	private scw.json.gson.JsonArray jsonArray;
	private Gson gson;

	public BuiltInGsonJsonArray(scw.json.gson.JsonArray jsonArray, Gson gson) {
		this.jsonArray = jsonArray;
	}

	public Iterator<scw.json.JsonElement> iterator() {
		return new IteratorConvert<scw.json.gson.JsonElement, JsonElement>(jsonArray.iterator(),
				new Converter<scw.json.gson.JsonElement, JsonElement>() {

					public JsonElement convert(scw.json.gson.JsonElement k) throws Exception {
						return new BuiltInGsonElement(k, gson);
					}
				});
	}

	public JsonElement get(int index) {
		scw.json.gson.JsonElement element = jsonArray.get(index);
		return element == null ? null : new BuiltInGsonElement(element, gson);
	}

	public JsonObject getJsonObject(int index) {
		JsonElement element = get(index);
		return element == null ? null : element.parseJsonObject();
	}

	public JsonArray getJsonArray(int index) {
		JsonElement element = get(index);
		return element == null ? null : element.parseJsonArray();
	}

	public void add(Object value) {
		if (value == null) {
			return;
		}
		jsonArray.add(gson.toJsonTree(value));
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJsonString() {
		return jsonArray.toString();
	}

	@Override
	public String toString() {
		return toJsonString();
	}

	public <T> T getObject(int index, Class<? extends T> type) {
		JsonElement jsonElement = get(index);
		return jsonElement == null ? null : jsonElement.parseObject(type);
	}

	public Object getObject(int index, Type type) {
		JsonElement jsonElement = get(index);
		return jsonElement == null ? null : jsonElement.parseObject(type);
	}

	public String getString(int index) {
		JsonElement jsonElement = get(index);
		return jsonElement == null ? null : jsonElement.parseString();
	}

	public Byte getByte(int index) {
		JsonElement jsonElement = get(index);
		return jsonElement == null ? null : jsonElement.parseByte();
	}
}
