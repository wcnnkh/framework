package scw.json;

import scw.value.ValueFactoryWrapper;

public class BasicJsonDefinitionWrapper<K> extends ValueFactoryWrapper<K> implements BasicJsonDefinition<K> {
	private final BasicJsonDefinition<K> target;

	public BasicJsonDefinitionWrapper(BasicJsonDefinition<K> target) {
		super(target);
		this.target = target;
	}

	public int size() {
		return target.size();
	}

	public boolean isEmpty() {
		return target.isEmpty();
	}

	public JsonElement get(K key) {
		return target.get(key);
	}

	public JsonArray getJsonArray(K key) {
		return target.getJsonArray(key);
	}

	public JsonObject getJsonObject(K key) {
		return target.getJsonObject(key);
	}

	public String toJsonString() {
		return target.toJsonString();
	}
}
