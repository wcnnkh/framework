package io.basc.framework.gson;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.json.AbstractJson;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.util.Pair;

public final class GsonObject extends AbstractJson<String> implements JsonObject, Converter<Entry<String, com.google.gson.JsonElement>, Pair<String, JsonElement>> {
	private com.google.gson.JsonObject gsonJsonObject;
	private Gson gson;

	public GsonObject(com.google.gson.JsonObject gsonJsonObject, Gson gson) {
		this.gsonJsonObject = gsonJsonObject;
		this.gson = gson;
	}

	public boolean put(String key, Object value) {
		if (value == null) {
			return false;
		}

		gsonJsonObject.add(key, gson.toJsonTree(value));
		return true;
	}

	public JsonElement getValue(String key) {
		com.google.gson.JsonElement gsonJsonElement = gsonJsonObject.get(key);
		if(gsonJsonElement == null) {
			return getDefaultValue(key);
		}

		return new GsonElement(gsonJsonElement, gson);
	}

	public Set<String> keySet() {
		return gsonJsonObject.keySet();
	}

	public boolean containsKey(String key) {
		return gsonJsonObject.has(key);
	}

	public String toJSONString() {
		return gsonJsonObject.toString();
	}

	public int size() {
		return gsonJsonObject.size();
	}

	@Override
	public int hashCode() {
		return gsonJsonObject.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		if(obj instanceof GsonObject){
			return gsonJsonObject.equals(((GsonObject) obj).gsonJsonObject);
		}
		
		return false;
	}

	public boolean remove(String key) {
		return gsonJsonObject.remove(key) != null;
	}
	
	public Pair<String, JsonElement> convert(
			Entry<String, com.google.gson.JsonElement> k) {
		return new Pair<String, JsonElement>(k.getKey(), new GsonElement(k.getValue(), gson));
	}

	public Iterator<Pair<String, JsonElement>> iterator() {
		return new ConvertibleIterator<Entry<String, com.google.gson.JsonElement>, Pair<String,JsonElement>>(gsonJsonObject.entrySet().iterator(), this);
	}
}
