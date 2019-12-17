package scw.json.support;

import java.lang.reflect.Type;
import java.util.Collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import scw.json.JsonArray;
import scw.json.JsonElement;

public class FastJsonObject implements scw.json.JsonObject {
	private JSONObject jsonObject;

	public FastJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public void put(String key, Object value) {
		jsonObject.put(key, value);
	}

	public scw.json.JsonObject getJsonObject(String key) {
		JSONObject json = jsonObject.getJSONObject(key);
		return json == null ? null : new FastJsonObject(json);
	}

	public JsonArray getJsonArray(String key) {
		com.alibaba.fastjson.JSONArray json = jsonObject.getJSONArray(key);
		return json == null ? null : new FastJsonArray(json);
	}

	public JsonElement get(String key) {
		String text = jsonObject.getString(key);
		return text == null ? null : new FastJsonElement(text);
	}

	public Collection<String> keys() {
		return jsonObject.keySet();
	}

	public boolean containsKey(String key) {
		return jsonObject.containsKey(key);
	}

	public String toJsonString() {
		return JSON.toJSONString(jsonObject, FastJSONBaseProperyFilter.BASE_PROPERY_FILTER);
	}

	@Override
	public String toString() {
		return toJsonString();
	}

	public <T> T getObject(String key, Class<? extends T> type) {
		return jsonObject.getObject(key, type);
	}

	public Object getObject(String key, Type type) {
		return jsonObject.getObject(key, type);
	}

	public String getString(String key) {
		return jsonObject.getString(key);
	}

	public int getIntValue(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? 0 : jsonElement.parseIntValue();
	}

	public Integer getInteger(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseInteger();
	}

	public boolean getBooleanValue(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? false : jsonElement.parseBooleanValue();
	}

	public Long getLong(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? null : jsonElement.parseLong();
	}

	public long getLongValue(String key) {
		JsonElement jsonElement = get(key);
		return jsonElement == null ? 0 : jsonElement.parseLongValue();
	}
}
