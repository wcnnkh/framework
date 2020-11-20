package scw.json;

import java.util.Set;

public class JsonObjectWrapper extends JsonWrapper<String> implements JsonObject {
	private JsonObject target;

	public JsonObjectWrapper(JsonObject target) {
		super(target);
		this.target = target;
	}

	public boolean containsKey(String key) {
		return target.containsKey(key);
	}

	public boolean remove(String key) {
		return target.remove(key);
	}

	public boolean put(String key, Object value) {
		return target.put(key, value);
	}

	public Set<String> keySet() {
		return target.keySet();
	}
}
