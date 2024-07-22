package io.basc.framework.json;

import java.util.Iterator;

public class JsonArrayWrapper extends JsonWrapper<Integer, JsonArray> implements JsonArray {

	public JsonArrayWrapper(JsonArray target) {
		super(target);
	}

	public Iterator<JsonElement> iterator() {
		return wrappedTarget.iterator();
	}

	public boolean add(Object element) {
		return wrappedTarget.add(element);
	}

	public boolean remove(int index) {
		return wrappedTarget.remove(index);
	}
}
