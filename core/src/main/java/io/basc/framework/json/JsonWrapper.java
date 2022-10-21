package io.basc.framework.json;

import io.basc.framework.util.Wrapper;

public class JsonWrapper<K, F extends Json<K>> extends Wrapper<F> implements Json<K> {

	public JsonWrapper(F target) {
		super(target);
	}

	public int size() {
		return wrappedTarget.size();
	}

	public boolean isEmpty() {
		return wrappedTarget.isEmpty();
	}

	public JsonElement get(K key) {
		return wrappedTarget.get(key);
	}

	public String toJsonString() {
		return wrappedTarget.toJsonString();
	}

}
