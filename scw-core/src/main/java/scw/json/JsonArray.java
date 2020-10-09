package scw.json;

public interface JsonArray extends Iterable<JsonElement>, BasicJsonDefinition<Integer> {
	void add(Object value);

	JsonElement get(Integer index);

	JsonArray getJsonArray(Integer index);

	JsonObject getJsonObject(Integer index);
}