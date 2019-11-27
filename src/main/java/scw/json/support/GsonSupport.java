package scw.json.support;

import java.lang.reflect.Type;

import scw.json.JSONArray;
import scw.json.JSONObject;
import scw.json.JsonSupport;
import scw.json.gson.Gson;
import scw.json.gson.JsonArray;
import scw.json.gson.JsonElement;
import scw.json.gson.JsonObject;

public class GsonSupport implements JsonSupport {
	private static final Gson GSON = new Gson();

	public String toJSONString(Object obj) {
		return GSON.toJson(obj);
	}

	public JSONArray parseArray(String text) {
		JsonElement jsonElement = GSON.toJsonTree(text);
		return new MyGsonJSONArrayWrapper(jsonElement.getAsJsonArray(), GSON);
	}

	public JSONObject parseObject(String text) {
		JsonElement jsonElement = GSON.toJsonTree(text);
		return new MyGsonJSONObjectWrapper(jsonElement.getAsJsonObject(), GSON);
	}

	public <T> T parseObject(String text, Class<T> type) {
		return GSON.fromJson(text, type);
	}

	public <T> T parseObject(String text, Type type) {
		return GSON.fromJson(text, type);
	}

	public JSONArray createJSONArray() {
		return new MyGsonJSONArrayWrapper(new JsonArray(), GSON);
	}

	public JSONObject createJSONObject() {
		return new MyGsonJSONObjectWrapper(new JsonObject(), GSON);
	}

}
