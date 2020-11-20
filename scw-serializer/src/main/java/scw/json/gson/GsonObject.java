package scw.json.gson;

import java.util.Set;

import com.google.gson.Gson;

import scw.json.AbstractJson;
import scw.json.JsonElement;
import scw.json.JsonObject;

public final class GsonObject extends AbstractJson<String> implements JsonObject {
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
		return gsonJsonElement == null ? null : new GsonElement(gsonJsonElement, gson, getDefaultValue(key));
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
}
