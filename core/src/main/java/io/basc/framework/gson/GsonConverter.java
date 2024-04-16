package io.basc.framework.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.JsonConverter;
import io.basc.framework.json.JsonException;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class GsonConverter implements JsonConverter {
	private final Gson gson;

	public GsonConverter() {
		this(new GsonBuilder().create());
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws JsonException {
		JsonElement jsonElement;
		if (source instanceof JsonElement) {
			jsonElement = (JsonElement) source;
		} else {
			jsonElement = gson.toJsonTree(source, sourceType.getResolvableType().getType());
		}
		return gson.fromJson(jsonElement, targetType.getResolvableType().getType());
	}

}
