package scw.json;

import scw.value.Value;

public interface JsonElement extends Value, JsonAware {
	JsonArray getAsJsonArray();

	JsonObject getAsJsonObject();

	boolean isJsonArray();

	boolean isJsonObject();
}
