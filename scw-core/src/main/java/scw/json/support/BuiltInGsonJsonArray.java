package scw.json.support;

import java.util.Iterator;

import scw.core.Converter;
import scw.core.IteratorConverter;
import scw.json.EmptyJsonElement;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.gson.Gson;
import scw.json.gson.GsonJsonElement;

public final class BuiltInGsonJsonArray extends JsonArray implements Converter<GsonJsonElement, JsonElement> {
	private scw.json.gson.GsonJsonArray gsonJsonArray;
	private Gson gson;

	public BuiltInGsonJsonArray(scw.json.gson.GsonJsonArray gsonJsonArray, Gson gson) {
		this.gsonJsonArray = gsonJsonArray;
		this.gson = gson;
	}

	public JsonElement convert(GsonJsonElement gsonJsonElement) {
		return new BuiltInGsonElement(gsonJsonElement, gson, EmptyJsonElement.INSTANCE);
	}

	public Iterator<scw.json.JsonElement> iterator() {
		return new IteratorConverter<scw.json.gson.GsonJsonElement, JsonElement>(gsonJsonArray.iterator(), this);
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
