package scw.json;

import scw.value.ValueFactoryWrapper;

public class JsonWrapper<K, F extends Json<K>> extends
		ValueFactoryWrapper<K, F> implements Json<K> {

	public JsonWrapper(F target) {
		super(target);
	}
	
	@Override
	public JsonElement getDefaultValue(K key) {
		return targetFactory.getDefaultValue(key);
	}

	public int size() {
		return targetFactory.size();
	}

	public boolean isEmpty() {
		return targetFactory.isEmpty();
	}

	public JsonElement getValue(K key) {
		return targetFactory.getValue(key);
	}

	public JsonArray getJsonArray(K key) {
		return targetFactory.getJsonArray(key);
	}

	public JsonObject getJsonObject(K key) {
		return targetFactory.getJsonObject(key);
	}

	public String toJSONString() {
		return targetFactory.toJSONString();
	}
}
