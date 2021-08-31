package io.basc.framework.fastjson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.Pair;

public final class FastJsonObject extends AbstractJson<String> implements JsonObject, JSONAware, Serializable, Converter<Entry<String, Object>, Pair<String, JsonElement>> {
	private static final long serialVersionUID = 1L;
	private JSONObject jsonObject;

	public FastJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public boolean put(String key, Object value) {
		jsonObject.put(key, value);
		return true;
	}

	public io.basc.framework.json.JsonObject getJsonObject(String key) {
		JSONObject json = jsonObject.getJSONObject(key);
		return json == null ? null : new FastJsonObject(json);
	}

	public JsonArray getJsonArray(String key) {
		com.alibaba.fastjson.JSONArray json = jsonObject.getJSONArray(key);
		return json == null ? null : new FastJsonArray(json);
	}

	public JsonElement getValue(String key) {
		String text = jsonObject.getString(key);
		if(text == null) {
			return getDefaultValue(key);
		}
		
		return new FastJsonElement(text);
	}

	public boolean containsKey(String key) {
		return jsonObject.containsKey(key);
	}

	public String toJsonString() {
		return JSON.toJSONString(jsonObject, ExtendFastJsonValueFilter.INSTANCE);
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
		if(obj == null){
			return false;
		}
		
		if(obj instanceof FastJsonObject){
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

	public Iterator<Pair<String, JsonElement>> iterator() {
		return new ConvertibleIterator<Entry<String, Object>, Pair<String, JsonElement>>(jsonObject.entrySet().iterator(), this);
	}

	public Pair<String, JsonElement> convert(Entry<String, Object> k) {
		return new Pair<String, JsonElement>(k.getKey(), getValue(k.getKey()));
	}
}