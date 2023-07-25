package io.basc.framework.fastjson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Iterator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONAware;

import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.element.ConvertibleIterator;

public final class FastJsonArray extends AbstractJson<Integer> implements JsonArray, JSONAware, Serializable {
	private static final long serialVersionUID = 1L;
	private JSONArray jsonArray;

	public FastJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public JsonElement convert(Object k) {
		if (k == null) {
			return null;
		}

		if (k instanceof JsonElement) {
			return (JsonElement) k;
		}
		return new FastJsonElement(k.toString());
	}

	public Iterator<JsonElement> iterator() {
		return new ConvertibleIterator<Object, JsonElement>(jsonArray.iterator(), this::convert);
	}

	public JsonElement get(Integer index) {
		String text = jsonArray.getString(index);
		if (text == null) {
			return JsonElement.EMPTY;
		}

		return new FastJsonElement(text);
	}

	public boolean remove(int index) {
		Object object = jsonArray.remove(index);
		return object != null;
	}

	public JsonObject getJsonObject(int index) {
		com.alibaba.fastjson.JSONObject json = jsonArray.getJSONObject(index);
		return json == null ? null : new FastJsonObject(json);
	}

	public io.basc.framework.json.JsonArray getJsonArray(int index) {
		JSONArray jarr = jsonArray.getJSONArray(index);
		return jarr == null ? null : new FastJsonArray(jarr);
	}

	public boolean add(Object value) {
		jsonArray.add(value);
		return true;
	}

	public int size() {
		return jsonArray.size();
	}

	public String toJsonString() {
		return JSON.toJSONString(jsonArray);
	}

	public <T> T getObject(int index, Class<? extends T> type) {
		return jsonArray.getObject(index, type);
	}

	public Object getObject(int index, Type type) {
		return jsonArray.getObject(index, type);
	}

	public String getString(int index) {
		return jsonArray.getString(index);
	}

	public String toJSONString() {
		return toJsonString();
	}

	@Override
	public int hashCode() {
		return jsonArray.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FastJsonArray) {
			return jsonArray.equals(((FastJsonArray) obj).jsonArray);
		}

		return false;
	}
}
