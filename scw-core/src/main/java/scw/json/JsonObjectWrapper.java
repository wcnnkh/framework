package scw.json;

import java.util.Collection;

public class JsonObjectWrapper extends JsonWrapper<String> implements JsonObject {
	private JsonObject target;

	public JsonObjectWrapper(JsonObject target) {
		super(target);
		this.target = target;
	}

	public void put(String key, Object value) {
		target.put(key, value);
	}

	public boolean containsKey(String key) {
		return target.containsKey(key);
	}

	public Collection<String> keys() {
		return target.keys();
	}
}
