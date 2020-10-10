package scw.json;

import java.util.Iterator;

public class JsonArrayWrapper extends JsonWrapper<Integer> implements JsonArray {
	private final JsonArray target;

	public JsonArrayWrapper(JsonArray target) {
		super(target);
		this.target = target;
	}

	public Iterator<JsonElement> iterator() {
		return target.iterator();
	}

	public void add(Object value) {
		target.add(value);
	}
}
