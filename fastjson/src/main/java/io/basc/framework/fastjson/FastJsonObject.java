package io.basc.framework.fastjson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.SetElements;
import io.basc.framework.util.collections.Elements;

public final class FastJsonObject extends AbstractJson<String> implements JsonObject, JSONAware, Serializable {
	private static final long serialVersionUID = 1L;
	private JSONObject jsonObject;

	public FastJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public boolean put(String key, Object value) {
		jsonObject.put(key, value);
		return true;
	}

	public JsonObject getJsonObject(String key) {
		JSONObject json = jsonObject.getJSONObject(key);
		return json == null ? null : new FastJsonObject(json);
	}

	public JsonArray getJsonArray(String key) {
		com.alibaba.fastjson.JSONArray json = jsonObject.getJSONArray(key);
		return json == null ? null : new FastJsonArray(json);
	}

	public JsonElement get(String key) {
		String text = jsonObject.getString(key);
		if (text == null) {
			return JsonElement.EMPTY;
		}

		return new FastJsonElement(text);
	}

	public boolean containsKey(String key) {
		return jsonObject.containsKey(key);
	}

	public String toJsonString() {
		return JSON.toJSONString(jsonObject);
	}

	public <T> T getObjectSupport(String key, Class<? extends T> type) {
		return jsonObject.getObject(key, type);
	}

	public Object getObjectSupport(String key, Type type) {
		return jsonObject.getObject(key, type);
	}

	public int size() {
		return jsonObject.size();
	}

	public String toJSONString() {
		return toJsonString();
	}

	@Override
	public int hashCode() {
		return jsonObject.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FastJsonObject) {
			return jsonObject.equals(((FastJsonObject) obj).jsonObject);
		}

		return false;
	}

	public Set<String> keySet() {
		return jsonObject.keySet();
	}

	public boolean remove(String key) {
		return jsonObject.remove(key) != null;
	}

	@Override
	public Elements<String> keys() {
		return new SetElements<>(jsonObject.keySet());
	}
}
