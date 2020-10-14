package scw.json.support;

import java.lang.reflect.Type;

import scw.json.AbstractJSONSupport;
import scw.json.EmptyJsonElement;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.JsonObject;
import scw.json.gson.Gson;
import scw.json.gson.GsonJsonElement;

public final class BuiltinGsonSupport extends AbstractJSONSupport {
	public static final BuiltinGsonSupport INSTANCE = new BuiltinGsonSupport();

	private static final Gson GSON = new Gson().newBuilder()
			.registerTypeAdapterFactory(ExtendBuiltInGsonTypeAdapter.FACTORY).create();

	public JsonArray parseArray(String text) {
		GsonJsonElement gsonJsonElement = GSON.fromJson(text, GsonJsonElement.class);
		return new BuiltInGsonJsonArray(gsonJsonElement.getAsJsonArray(), GSON);
	}

	public JsonObject parseObject(String text) {
		GsonJsonElement jsonElement = GSON.fromJson(text, GsonJsonElement.class);
		return new BuiltInGsonJsonObject(jsonElement.getAsJsonObject(), GSON);
	}

	public <T> T parseObjectInternal(String text, Class<T> type) {
		return GSON.fromJson(text, type);
	}

	public <T> T parseObjectInternal(String text, Type type) {
		return GSON.fromJson(text, type);
	}

	public JsonElement parseJson(String text) {
		GsonJsonElement gsonJsonElement = GSON.fromJson(text, GsonJsonElement.class);
		return new BuiltInGsonElement(gsonJsonElement, GSON, EmptyJsonElement.INSTANCE);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		return GSON.toJson(obj);
	}
}
