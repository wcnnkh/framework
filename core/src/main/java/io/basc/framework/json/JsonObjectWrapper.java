package io.basc.framework.json;

import io.basc.framework.util.element.Elements;

public class JsonObjectWrapper extends JsonWrapper<String, JsonObject> implements JsonObject {

	public JsonObjectWrapper(JsonObject target) {
		super(target);
	}

	public boolean containsKey(String key) {
		return wrappedTarget.containsKey(key);
	}

	public boolean remove(String key) {
		return wrappedTarget.remove(key);
	}

	public boolean put(String key, Object value) {
		return wrappedTarget.put(key, value);
	}

	@Override
	public Elements<String> keys() {
		return wrappedTarget.keys();
	}
}
