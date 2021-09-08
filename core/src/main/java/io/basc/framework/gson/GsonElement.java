package io.basc.framework.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import io.basc.framework.core.ResolvableType;
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

	public String getAsString() {
		if (gsonJsonElement.isJsonArray() || gsonJsonElement.isJsonObject()) {
			return gsonJsonElement.toString();
		}
		return gsonJsonElement.getAsString();
	}
	
	@Override
	protected Object getAsNonBaseType(ResolvableType type) {
		return gson.fromJson(gsonJsonElement, type.getType());
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

	public boolean isEmpty() {
		return gsonJsonElement.isJsonNull();
	}

	public String toJSONString() {
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
}
