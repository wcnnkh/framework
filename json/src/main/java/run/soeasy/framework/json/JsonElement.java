package run.soeasy.framework.json;

import java.io.IOException;

import run.soeasy.framework.core.io.Exportable;

public interface JsonElement extends Exportable {
	default boolean isJsonObject() {
		return this instanceof JsonObject;
	}

	default JsonObject getAsJsonObject() {
		if (isJsonObject()) {
			return (JsonObject) this;
		}
		throw new IllegalStateException("Not a JSON Object: " + this);
	}

	default boolean isJsonArray() {
		return this instanceof JsonArray;
	}

	default JsonArray getAsJsonArray() {
		if (isJsonArray()) {
			return (JsonArray) this;
		}
		throw new IllegalStateException("Not a JSON Array: " + this);
	}

	default boolean isJsonNull() {
		return this instanceof JsonNull;
	}

	default JsonNull getAsJsonNull() {
		if (isJsonNull()) {
			return (JsonNull) this;
		}
		throw new IllegalStateException("Not a JSON Null: " + this);
	}

	default boolean isJsonPrimitive() {
		return this instanceof JsonPrimitive;
	}

	default JsonPrimitive getAsJsonPrimitive() {
		if (isJsonPrimitive()) {
			return (JsonPrimitive) this;
		}
		throw new IllegalStateException("Not a JSON Primitive: " + this);
	}

	default String toJsonString() {
		StringBuilder sb = new StringBuilder();
		try {
			export(sb);
		} catch (IOException e) {
			// ignore
		}
		return sb.toString();
	}

	public static String escaping(String value) {
		int size = value.length();
		StringBuilder sb = new StringBuilder(Math.toIntExact(Math.round(size * 1.25)));
		for (int i = 0; i < size; i++) {
			char c = value.charAt(i);
			if (c == '"') {
				sb.append("\\\"");
			} else if (c == '\\') {
				sb.append("\\\\");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
