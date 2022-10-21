package io.basc.framework.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;

public final class GsonElement extends AbstractJsonElement {
	private JsonElement gsonJsonElement;
	private Gson gson;

	public GsonElement(JsonElement gsonJsonElement, Gson gson) {
		this.gsonJsonElement = gsonJsonElement;
		this.gson = gson;
	}

	@Override
	public Object getSource() {
		return gsonJsonElement;
	}

	public String getAsString() {
		if (gsonJsonElement.isJsonArray() || gsonJsonElement.isJsonObject()) {
			return gsonJsonElement.toString();
		}
		return gsonJsonElement.getAsString();
	}

	public JsonArray getAsJsonArray() {
		return new GsonArray(gsonJsonElement.getAsJsonArray(), gson);
	}

	public JsonObject getAsJsonObject() {
		return new GsonObject(gsonJsonElement.getAsJsonObject(), gson);
	}

	public boolean isJsonArray() {
		return gsonJsonElement.isJsonArray();
	}

	public boolean isJsonObject() {
		return gsonJsonElement.isJsonObject();
	}

	public String toJsonString() {
		if (gsonJsonElement.isJsonArray() || gsonJsonElement.isJsonObject()) {
			return gsonJsonElement.toString();
		}
		return gsonJsonElement.getAsString();
	}

	@Override
	public int hashCode() {
		return gsonJsonElement.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof GsonElement) {
			return gsonJsonElement.equals(((GsonElement) obj).gsonJsonElement);
		}
		return false;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return gson.fromJson(gsonJsonElement, targetType.getResolvableType().getType());
	}
}
