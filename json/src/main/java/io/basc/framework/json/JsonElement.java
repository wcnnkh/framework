package io.basc.framework.json;

import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.lang.Nullable;

public interface JsonElement extends ValueWrapper, JsonAware {
	public static JsonElement EMPTY = new EmptyJsonElement();

	@Nullable
	JsonArray getAsJsonArray();

	@Nullable
	JsonObject getAsJsonObject();

	default boolean isJsonArray() {
		String text = getAsString();
		return text.startsWith(JsonArray.PREFIX) && text.endsWith(JsonArray.SUFFIX);
	}

	default boolean isJsonObject() {
		String text = getAsString();
		return text.startsWith(JsonObject.PREFIX) && text.endsWith(JsonObject.SUFFIX);
	}
}