package scw.json;

import scw.lang.Nullable;
import scw.value.Value;

public interface JsonElement extends Value, JSONAware {
	@Nullable
	JsonArray getAsJsonArray();

	@Nullable
	JsonObject getAsJsonObject();

	boolean isJsonArray();

	boolean isJsonObject();
}
