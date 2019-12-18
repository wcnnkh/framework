package scw.json;

import java.util.Collection;

public abstract class JsonObject extends AbstractJson<String> {

	public abstract void put(String key, Object value);

	public abstract Collection<String> keys();

	public abstract boolean containsKey(String key);
}
