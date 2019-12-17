package scw.json.support;

import java.lang.reflect.Type;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.gson.Gson;
import scw.json.gson.JsonElement;
import scw.util.AbstractValue;

public class BuiltInGsonElement extends AbstractValue implements scw.json.JsonElement {
	private JsonElement jsonElement;
	private Gson gson;

	public BuiltInGsonElement(JsonElement jsonElement, Gson gson) {
		this.jsonElement = jsonElement;
		this.gson = gson;
	}

	public String parseString() {
		return jsonElement.toString();
	}

	@Override
	protected <T> T notSupportParse(Class<? extends T> type) {
		return gson.fromJson(jsonElement, type);
	}

	@Override
	protected <T> T notSupportParse(Type type) {
		return gson.fromJson(jsonElement, type);
	}

	public JsonArray parseJsonArray() {
		return new BuiltInGsonJsonArray(jsonElement.getAsJsonArray(), gson);
	}

	public JsonObject parseJsonObject() {
		return new BuiltInGsonJsonObject(jsonElement.getAsJsonObject(), gson);
	}
}
