package io.basc.framework.gson;

import com.google.gson.JsonElement;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.AbstractJsonElement;
import io.basc.framework.json.JsonArray;
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

	@Override
	public <E extends Throwable> Object convert(TypeDescriptor targetType,
			Converter<? super Object, ? extends Object, E> converter) throws E {
		return super.convert(targetType, (s, st, tt) -> {
			if (converter.canConvert(st, tt)) {
				converter.convert(s, st, tt);
			}
			return getConverter().convert(s, st, tt);
		});
	}

	public String toJsonString() {
		if (jsonElement.isJsonArray() || jsonElement.isJsonObject()) {
			return jsonElement.toString();
		}
		return jsonElement.getAsString();
	}
}
