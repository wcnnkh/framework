package scw.json;

import scw.value.ValueWrapper;

public class JsonElementWrapper extends ValueWrapper implements JsonElement {
	private final JsonElement target;

	public JsonElementWrapper(JsonElement target) {
		super(target);
		this.target = target;
	}

	public JsonArray getAsJsonArray() {
		return target.getAsJsonArray();
	}

	public JsonObject getAsJsonObject() {
		return target.getAsJsonObject();
	}

	public boolean isJsonArray() {
		return target.isJsonArray();
	}

	public boolean isJsonObject() {
		return target.isJsonObject();
	}

	public String toJsonString() {
		return target.toJsonString();
	}
}
