package run.soeasy.framework.json;

import run.soeasy.framework.core.streaming.Streamable;

public interface JsonArray extends JsonElement, Streamable<JsonElement> {
	@Override
	default boolean isJsonArray() {
		return true;
	}

	@Override
	default JsonArray getAsJsonArray() {
		return this;
	}
}
