package scw.json.parser;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import scw.convert.Converter;
import scw.core.IteratorConverter;
import scw.json.AbstractJson;
import scw.json.JSONException;
import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.util.Pair;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultJsonObject extends AbstractJson<String> implements
		JsonObject, Converter<Entry, Pair<String, JsonElement>>, Serializable {
	private static final long serialVersionUID = 1L;
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

	public Set<String> keySet() {
		return simpleJSONObject.keySet();
	}

	public boolean containsKey(String key) {
		return simpleJSONObject.containsKey(key);
	}

	public boolean remove(String key) {
		return simpleJSONObject.remove(key) != null;
	}

	public boolean put(String key, Object value) {
		simpleJSONObject.put(key, value);
		return true;
	}
	
	public Pair<String, JsonElement> convert(Entry k) {
		Object key = k.getValue();
		Object value = simpleJSONObject.get(key);
		return new Pair<String, JsonElement>(String.valueOf(key), value == null ? null : new DefaultJsonElement(value));
	}

	public Iterator<Pair<String, JsonElement>> iterator() {
		return new IteratorConverter<Entry, Pair<String, JsonElement>>(simpleJSONObject.entrySet().iterator(), this);
	}

}
