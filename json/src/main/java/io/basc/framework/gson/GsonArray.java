package io.basc.framework.gson;

import java.util.Iterator;

import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.util.ConvertibleIterator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "jsonArray", callSuper = false)
public final class GsonArray extends AbstractJson<Integer> implements JsonArray {
	private final com.google.gson.JsonArray jsonArray;
	private final GsonConverter converter;

	public JsonElement convert(com.google.gson.JsonElement gsonJsonElement) {
		return new GsonElement(gsonJsonElement, converter);
	}

	public Iterator<io.basc.framework.json.JsonElement> iterator() {
		return new ConvertibleIterator<com.google.gson.JsonElement, JsonElement>(jsonArray.iterator(), this::convert);
	}

	public JsonElement get(Integer index) {
		com.google.gson.JsonElement element = jsonArray.get(index);
		if (element == null) {
			return JsonElement.EMPTY;
		}

		return new GsonElement(element, converter);
	}

	public boolean add(Object value) {
		if (value == null) {
			return false;
		}
		jsonArray.add(converter.getGson().toJsonTree(value));
		return true;
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJsonString() {
		return jsonArray.toString();
	}

	public boolean remove(int index) {
		return jsonArray.remove(index) != null;
	}
}
