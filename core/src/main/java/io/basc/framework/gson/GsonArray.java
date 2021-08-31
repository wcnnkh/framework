package io.basc.framework.gson;

import java.util.Iterator;

import com.google.gson.Gson;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;

public final class GsonArray extends AbstractJson<Integer>
		implements JsonArray, Converter<com.google.gson.JsonElement, JsonElement> {
	private com.google.gson.JsonArray gsonJsonArray;
	private Gson gson;

	public GsonArray(com.google.gson.JsonArray gsonJsonArray, Gson gson) {
		this.gsonJsonArray = gsonJsonArray;
		this.gson = gson;
	}

	public JsonElement convert(com.google.gson.JsonElement gsonJsonElement) {
		return new GsonElement(gsonJsonElement, gson);
	}

	public Iterator<io.basc.framework.json.JsonElement> iterator() {
		return new ConvertibleIterator<com.google.gson.JsonElement, JsonElement>(gsonJsonArray.iterator(), this);
	}

	public JsonElement getValue(Integer index) {
		com.google.gson.JsonElement element = gsonJsonArray.get(index);
		if(element == null) {
			return getDefaultValue(index);
		}
		
		return new GsonElement(element, gson);
	}

	public boolean add(Object value) {
		if (value == null) {
			return false;
		}
		gsonJsonArray.add(gson.toJsonTree(value));
		return true;
	}

	public int size() {
		return gsonJsonArray.size();
	}

	public String toJSONString() {
		return gsonJsonArray.toString();
	}

	@Override
	public int hashCode() {
		return gsonJsonArray.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof GsonArray) {
			return gsonJsonArray.equals(((GsonArray) obj).gsonJsonArray);
		}
		return false;
	}

	public boolean remove(int index) {
		return gsonJsonArray.remove(index) != null;
	}
}
