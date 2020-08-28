package scw.json;

public abstract class JsonArray extends AbstractJson<Integer> implements Iterable<JsonElement> {
	public abstract void add(Object value);

	public abstract JsonElement get(Integer index);
}