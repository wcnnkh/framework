package scw.json;

import scw.value.ValueWrapper;

public class JsonElementWrapper extends ValueWrapper<JsonElement> implements
		JsonElement {
	private static final long serialVersionUID = 1L;

	public JsonElementWrapper(JsonElement target) {
		super(target);
	}

	public JsonArray getAsJsonArray() {
		return targetValue.getAsJsonArray();
	}

	public JsonObject getAsJsonObject() {
		return targetValue.getAsJsonObject();
	}

	public boolean isJsonArray() {
		return targetValue.isJsonArray();
	}

	public boolean isJsonObject() {
		return targetValue.isJsonObject();
	}

	public String toJSONString() {
		return targetValue.toJSONString();
	}
}
