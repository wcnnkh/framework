package scw.json;

import scw.util.value.Value;

public interface JsonElement extends Value {
	JsonArray getAsJsonArray();

	JsonObject getAsJsonObject();
}
