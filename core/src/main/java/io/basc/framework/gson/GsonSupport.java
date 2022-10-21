package io.basc.framework.gson;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.basc.framework.json.AbstractJsonSupport;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonException;

public final class GsonSupport extends AbstractJsonSupport {
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
	public JsonElement parseJson(Reader reader) throws IOException, JsonException {
		com.google.gson.JsonElement gsonJsonElement = gson.fromJson(reader, com.google.gson.JsonElement.class);
		return new GsonElement(gsonJsonElement, gson);
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		return gson.toJson(obj);
	}
}
