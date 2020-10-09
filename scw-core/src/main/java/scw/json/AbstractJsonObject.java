package scw.json;

import java.util.Collection;

public abstract class AbstractJsonObject extends AbstractJson<String> implements JsonObject {
	public abstract void put(String key, Object value);

	public abstract boolean containsKey(String key);

	public abstract Collection<String> keys();
}