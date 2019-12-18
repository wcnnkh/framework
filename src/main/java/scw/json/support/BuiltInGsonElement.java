package scw.json.support;

import java.lang.reflect.Type;

import scw.json.AbstractJsonElement;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.gson.Gson;
import scw.json.gson.JsonElement;

public final class BuiltInGsonElement extends AbstractJsonElement {
	private JsonElement jsonElement;
	private Gson gson;

	public BuiltInGsonElement(JsonElement jsonElement, Gson gson) {
		this.jsonElement = jsonElement;
		this.gson = gson;
	}

	public String getAsString() {
		return jsonElement.toString();
	}

	@Override
	protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
		return gson.fromJson(jsonElement, type);
	}

	@Override
	protected <T> T getAsObjectNotSupport(Type type) {
		return gson.fromJson(jsonElement, type);
	}

	public JsonArray getAsJsonArray() {
		return new BuiltInGsonJsonArray(jsonElement.getAsJsonArray(), gson);
	}

	public JsonObject getAsJsonObject() {
		return new BuiltInGsonJsonObject(jsonElement.getAsJsonObject(), gson);
	}
}
