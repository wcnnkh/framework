package scw.json.support;

import java.lang.reflect.Type;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.JSONSupport;
import scw.json.gson.Gson;
import scw.json.gson.GsonJsonElement;

public final class BuiltinGsonSupport implements JSONSupport {
	private static final Gson GSON = new Gson();

	public String toJSONString(Object obj) {
		return GSON.toJson(obj);
	}

	public JsonArray parseArray(String text) {
		GsonJsonElement gsonJsonElement = GSON.toJsonTree(text);
		return new BuiltInGsonJsonArray(gsonJsonElement.getAsJsonArray(), GSON);
	}

	public JsonObject parseObject(String text) {
		GsonJsonElement gsonJsonElement = GSON.toJsonTree(text);
		return new BuiltInGsonJsonObject(gsonJsonElement.getAsJsonObject(), GSON);
	}

	public <T> T parseObject(String text, Class<T> type) {
		return GSON.fromJson(text, type);
	}

	public <T> T parseObject(String text, Type type) {
		return GSON.fromJson(text, type);
	}
}
