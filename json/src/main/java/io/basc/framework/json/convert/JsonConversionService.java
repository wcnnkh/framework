package io.basc.framework.json.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.AbstractConversionService;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;

public class JsonConversionService extends AbstractConversionService {

	private boolean isJsonType(Class<?> type, boolean isAssignableFrom) {
		if (isAssignableFrom) {
			return JsonElement.class.isAssignableFrom(type) || JsonArray.class.isAssignableFrom(type)
					|| JsonObject.class.isAssignableFrom(type);
		} else {
			return JsonElement.class == type || JsonArray.class == type || JsonObject.class == type;
		}
	}

	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return isJsonType(sourceType.getType(), true) || isJsonType(targetType.getType(), false);
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (isJsonType(sourceType.getType(), true)) {
			JsonElement jsonElement = (JsonElement) source;
			return jsonElement.getAsObject(targetType.getResolvableType().getType());
		} else {
			String text = (String) getConversionService().convert(source, sourceType,
					TypeDescriptor.valueOf(String.class));
			return getJsonSupport().parseObject(text, targetType.getResolvableType().getType());
		}
	}

}
