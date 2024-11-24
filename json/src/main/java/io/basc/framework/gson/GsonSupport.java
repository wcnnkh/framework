package io.basc.framework.gson;

import java.io.IOException;
import java.io.Reader;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonSupport;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class GsonSupport extends AbstractJsonSupport {
	public static final GsonSupport INSTANCE = new GsonSupport();
	@NonNull
	private GsonConverter converter = new GsonConverter();

	public JsonElement parseJson(String text) {
		com.google.gson.JsonElement gsonJsonElement = converter.getGson().fromJson(text,
				com.google.gson.JsonElement.class);
		return new GsonElement(gsonJsonElement, converter);
	}

	@Override
	public JsonElement parseJson(Reader reader) throws IOException, JsonException {
		com.google.gson.JsonElement gsonJsonElement = converter.getGson().fromJson(reader,
				com.google.gson.JsonElement.class);
		return new GsonElement(gsonJsonElement, converter);
	}
	
	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return true;
	}

	@Override
	protected String toJsonStringInternal(Object obj) {
		return converter.getGson().toJson(obj);
	}
}
