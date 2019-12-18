package scw.json.support;

import java.util.Iterator;

import scw.core.Converter;
import scw.core.utils.IteratorConvert;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.gson.Gson;

public final class BuiltInGsonJsonArray extends JsonArray {
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

	public JsonElement get(Integer index) {
		scw.json.gson.JsonElement element = jsonArray.get(index);
		return element == null ? null : new BuiltInGsonElement(element, gson);
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
}
