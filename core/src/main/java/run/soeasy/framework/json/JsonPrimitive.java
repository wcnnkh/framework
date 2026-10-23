package run.soeasy.framework.json;

import run.soeasy.framework.core.domain.Value;

public interface JsonPrimitive extends JsonElement, Value {
	@Override
	default boolean isJsonPrimitive() {
		return true;
	}

	@Override
	default JsonPrimitive getAsJsonPrimitive() {
		return this;
	}

	boolean isCharSequence();
}