package io.basc.framework.json;

import io.basc.framework.core.convert.lang.EmptyValue;

public class EmptyJsonElement extends EmptyValue implements JsonElement {
	private static final long serialVersionUID = 1L;
	public static final EmptyJsonElement INSTANCE = new EmptyJsonElement();

	public JsonArray getAsJsonArray() {
		return null;
	}

	public JsonObject getAsJsonObject() {
		return null;
	}

	public boolean isJsonArray() {
		return false;
	}

	public boolean isJsonObject() {
		return false;
	}

	public String toJsonString() {
		return null;
	}
}
