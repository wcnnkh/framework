package io.basc.framework.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.basc.framework.json.AbstractJSONSupport;
import io.basc.framework.json.JsonElement;

public final class GsonSupport extends AbstractJSONSupport {
	public static final GsonSupport INSTANCE = new GsonSupport();

	private final Gson gson;

	public GsonSupport() {
		this.gson = new GsonBuilder().registerTypeAdapterFactory(ExtendGsonTypeAdapter.FACTORY).create();
	}

	public GsonSupport(Gson gson) {
		this.gson = gson;
	}

	public Gson getGson() {
		return gson;
	}

	public JsonElement parseJson(String text) {
		com.google.gson.JsonElement gsonJsonElement = gson.fromJson(text, com.google.gson.JsonElement.class);
		return new GsonElement(gsonJsonElement, gson);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		return gson.toJson(obj);
	}
}
