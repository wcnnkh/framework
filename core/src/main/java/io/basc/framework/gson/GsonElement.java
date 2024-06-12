package io.basc.framework.gson;

import com.google.gson.JsonElement;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonException;
import io.basc.framework.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "jsonElement", callSuper = false)
@Getter
public final class GsonElement extends AbstractJsonElement {
	private final JsonElement jsonElement;
	private final GsonConverter converter;

	@Override
	public Object getValue() {
		return jsonElement;
	}

	public String getAsString() {
		if (jsonElement.isJsonArray() || jsonElement.isJsonObject()) {
			return jsonElement.toString();
		}
		return jsonElement.getAsString();
	}

	public JsonArray getAsJsonArray() {
		return new GsonArray(jsonElement.getAsJsonArray(), converter);
	}

	public JsonObject getAsJsonObject() {
		return new GsonObject(jsonElement.getAsJsonObject(), converter);
	}

	public boolean isJsonArray() {
		return jsonElement.isJsonArray();
	}

	public boolean isJsonObject() {
		return jsonElement.isJsonObject();
	}

	public String toJsonString() {
		if (jsonElement.isJsonArray() || jsonElement.isJsonObject()) {
			return jsonElement.toString();
		}
		return jsonElement.getAsString();
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws JsonException, ConversionException {
		return converter.convert(source, sourceType, targetType);
	}
}
