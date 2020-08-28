package scw.json.support;

import java.lang.reflect.Type;

import scw.json.AbstractJsonElement;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.gson.Gson;
import scw.json.gson.GsonJsonElement;
import scw.value.Value;

public final class BuiltInGsonElement extends AbstractJsonElement {
	private GsonJsonElement gsonJsonElement;
	private Gson gson;

	public BuiltInGsonElement(GsonJsonElement gsonJsonElement, Gson gson, Value defaultValue) {
		super(defaultValue);
		this.gsonJsonElement = gsonJsonElement;
		this.gson = gson;
	}

	public String getAsString() {
		return gsonJsonElement.toString();
	}

	@Override
	protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
		return gson.fromJson(gsonJsonElement, type);
	}

	@Override
	protected Object getAsObjectNotSupport(Type type) {
		return gson.fromJson(gsonJsonElement, type);
	}

	public JsonArray getAsJsonArray() {
		return new BuiltInGsonJsonArray(gsonJsonElement.getAsJsonArray(), gson);
	}

	public JsonObject getAsJsonObject() {
		return new BuiltInGsonJsonObject(gsonJsonElement.getAsJsonObject(), gson);
	}

	public boolean isJsonArray() {
		return gsonJsonElement.isJsonArray();
	}

	public boolean isJsonObject() {
		return gsonJsonElement.isJsonObject();
	}

	public boolean isEmpty() {
		return gsonJsonElement.isJsonNull();
	}
}
