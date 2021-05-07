package scw.json;

import java.util.Iterator;

public class JsonArrayWrapper extends JsonWrapper<Integer, JsonArray> implements
		JsonArray {

	public JsonArrayWrapper(JsonArray target) {
		super(target);
	}

	public Iterator<JsonElement> iterator() {
		return targetFactory.iterator();
	}

	public boolean add(Object element) {
		return targetFactory.add(element);
	}

	public boolean remove(int index) {
		return targetFactory.remove(index);
	}
}
