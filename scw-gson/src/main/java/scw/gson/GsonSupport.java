package scw.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import scw.json.AbstractJSONSupport;
import scw.json.EmptyJsonElement;
import scw.json.JsonElement;

public final class GsonSupport extends AbstractJSONSupport {
	public static final GsonSupport INSTANCE = new GsonSupport();

	private static final Gson GSON = new GsonBuilder().registerTypeAdapterFactory(ExtendGsonTypeAdapter.FACTORY)
			.create();

	public JsonElement parseJson(String text) {
		com.google.gson.JsonElement gsonJsonElement = GSON.fromJson(text, com.google.gson.JsonElement.class);
		return new GsonElement(gsonJsonElement, GSON, EmptyJsonElement.INSTANCE);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		return GSON.toJson(obj);
	}
}
