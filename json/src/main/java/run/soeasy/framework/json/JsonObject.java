package run.soeasy.framework.json;

import run.soeasy.framework.core.streaming.Mapping;

public interface JsonObject extends JsonElement, Mapping<String, JsonElement> {

	@Override
	default boolean isJsonObject() {
		return true;
	}

	@Override
	default JsonObject getAsJsonObject() {
		return this;
	}
}
