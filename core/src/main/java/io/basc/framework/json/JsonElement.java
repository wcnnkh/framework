package io.basc.framework.json;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.lang.Nullable;

public interface JsonElement extends Value, JsonAware, JsonConverter {
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

	@Override
	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws JsonException, ConversionException;
}