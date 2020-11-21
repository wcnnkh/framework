package scw.json.parser;

import java.util.Set;

import scw.json.AbstractJson;
import scw.json.JSONException;
import scw.json.JsonElement;
import scw.json.JsonObject;

public class DefaultJsonObject extends AbstractJson<String> implements
		JsonObject {
	private SimpleJSONObject simpleJSONObject;

	public DefaultJsonObject(String text) {
		Object json = JSONValue.parse(text);
		if (json instanceof SimpleJSONObject) {
			simpleJSONObject = (SimpleJSONObject) json;
		} else {
			throw new JSONException("This is not a JSON array:" + text);
		}
	}

	public int size() {
		return simpleJSONObject.size();
	}

	public JsonElement getValue(String key) {
		Object value = simpleJSONObject.get(key);
		return value == null ? null : new DefaultJsonElement(value);
	}

	public String toJSONString() {
		return simpleJSONObject.toJSONString();
	}

	@SuppressWarnings("unchecked")
	public Set<String> keySet() {
		return simpleJSONObject.keySet();
	}

	public boolean containsKey(String key) {
		return simpleJSONObject.containsKey(key);
	}

	public boolean remove(String key) {
		return simpleJSONObject.remove(key) != null;
	}

	@SuppressWarnings("unchecked")
	public boolean put(String key, Object value) {
		simpleJSONObject.put(key, value);
		return true;
	}

}
