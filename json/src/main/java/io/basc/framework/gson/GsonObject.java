package io.basc.framework.gson;

import java.util.Set;

import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.SetElements;
import io.basc.framework.util.collection.Elements;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(of = "jsonObject", callSuper = false)
@RequiredArgsConstructor
@Getter
public final class GsonObject extends AbstractJson<String> implements JsonObject {
	private final com.google.gson.JsonObject jsonObject;
	private final GsonConverter converter;

	public boolean put(String key, Object value) {
		if (value == null) {
			return false;
		}

		jsonObject.add(key, converter.getGson().toJsonTree(value));
		return true;
	}

	public JsonElement get(String key) {
		com.google.gson.JsonElement gsonJsonElement = jsonObject.get(key);
		if (gsonJsonElement == null) {
			return JsonElement.EMPTY;
		}

		return new GsonElement(gsonJsonElement, converter);
	}

	public Set<String> keySet() {
		return jsonObject.keySet();
	}

	public boolean containsKey(String key) {
		return jsonObject.has(key);
	}

	public String toJsonString() {
		return jsonObject.toString();
	}

	public int size() {
		return jsonObject.size();
	}

	public boolean remove(String key) {
		return jsonObject.remove(key) != null;
	}

	@Override
	public Elements<String> keys() {
		return new SetElements<>(jsonObject.keySet());
	}
}
