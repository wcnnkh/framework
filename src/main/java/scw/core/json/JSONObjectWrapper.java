package scw.core.json;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class JSONObjectWrapper extends JSONObjectReadOnlyWarpper implements
		JSONObject {
	private static final long serialVersionUID = 1L;
	private JSONObject jsonObject;

	public JSONObjectWrapper(JSONObject jsonObject) {
		super(jsonObject);
		this.jsonObject = jsonObject;
	}

	public int size() {
		return jsonObject.size();
	}

	public boolean isEmpty() {
		return jsonObject.isEmpty();
	}

	public boolean containsKey(Object key) {
		return jsonObject.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return jsonObject.containsValue(value);
	}

	public Object get(Object key) {
		return jsonObject.get(key);
	}

	public Object put(String key, Object value) {
		return jsonObject.put(key, value);
	}

	public Object remove(Object key) {
		return jsonObject.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		jsonObject.putAll(m);
	}

	public void clear() {
		jsonObject.clear();
	}

	public Set<String> keySet() {
		return jsonObject.keySet();
	}

	public Collection<Object> values() {
		return jsonObject.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return jsonObject.entrySet();
	}
}
