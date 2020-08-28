package scw.json.support;

import java.util.Iterator;

import scw.core.Converter;
import scw.core.IteratorConvert;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.gson.Gson;

public final class BuiltInGsonJsonArray extends JsonArray {
	private scw.json.gson.GsonJsonArray gsonJsonArray;
	private Gson gson;

	public BuiltInGsonJsonArray(scw.json.gson.GsonJsonArray gsonJsonArray, Gson gson) {
		this.gsonJsonArray = gsonJsonArray;
	}

	public Iterator<scw.json.JsonElement> iterator() {
		return new IteratorConvert<scw.json.gson.GsonJsonElement, JsonElement>(gsonJsonArray.iterator(),
				new Converter<scw.json.gson.GsonJsonElement, JsonElement>() {

					public JsonElement convert(scw.json.gson.GsonJsonElement k) {
						return new BuiltInGsonElement(k, gson, getDefaultValue(null));
					}
				});
	}

	public JsonElement get(Integer index) {
		scw.json.gson.GsonJsonElement element = gsonJsonArray.get(index);
		return element == null ? null : new BuiltInGsonElement(element, gson, getDefaultValue(index));
	}

	public void add(Object value) {
		if (value == null) {
			return;
		}
		gsonJsonArray.add(gson.toJsonTree(value));
	}

	public int size() {
		return gsonJsonArray.size();
	}

	public String toJsonString() {
		return gsonJsonArray.toString();
	}
}
