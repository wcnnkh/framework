package scw.json;

import java.util.Iterator;
import java.util.Set;

import scw.util.Pair;

public class JsonObjectWrapper extends JsonWrapper<String, JsonObject> implements JsonObject {

	public JsonObjectWrapper(JsonObject target) {
		super(target);
	}

	public boolean containsKey(String key) {
		return targetFactory.containsKey(key);
	}

	public boolean remove(String key) {
		return targetFactory.remove(key);
	}

	public boolean put(String key, Object value) {
		return targetFactory.put(key, value);
	}

	public Set<String> keySet() {
		return targetFactory.keySet();
	}

	public Iterator<Pair<String, JsonElement>> iterator() {
		return targetFactory.iterator();
	}
}
