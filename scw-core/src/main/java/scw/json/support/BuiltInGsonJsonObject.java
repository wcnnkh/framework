package scw.json.support;

import java.util.Collection;

import scw.json.AbstractJson;
import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.json.gson.Gson;

public final class BuiltInGsonJsonObject extends AbstractJson<String> implements JsonObject {
	private scw.json.gson.GsonJsonObject gsonJsonObject;
	private Gson gson;

	public BuiltInGsonJsonObject(scw.json.gson.GsonJsonObject gsonJsonObject, Gson gson) {
		this.gsonJsonObject = gsonJsonObject;
		this.gson = gson;
	}

	public void put(String key, Object value) {
		if (value == null) {
			return;
		}

		gsonJsonObject.add(key, gson.toJsonTree(value));
	}

	public JsonElement get(String key) {
		scw.json.gson.GsonJsonElement gsonJsonElement = gsonJsonObject.get(key);
		return gsonJsonElement == null ? null : new BuiltInGsonElement(gsonJsonElement, gson, getDefaultValue(key));
	}

	public Collection<String> keys() {
		return gsonJsonObject.keySet();
	}

	public boolean containsKey(String key) {
		return gsonJsonObject.has(key);
	}

	public String toJsonString() {
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
		
		if(obj instanceof BuiltInGsonJsonObject){
			return gsonJsonObject.equals(((BuiltInGsonJsonObject) obj).gsonJsonObject);
		}
		
		return false;
	}
}
