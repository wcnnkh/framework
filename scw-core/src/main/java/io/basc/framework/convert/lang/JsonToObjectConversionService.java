package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.JSON;
import io.basc.framework.json.JSONSupportAccessor;
import io.basc.framework.json.JsonElement;

import java.io.Reader;

public class JsonToObjectConversionService extends JSONSupportAccessor implements ConversionService {

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return (String.class == sourceType.getType() || Reader.class.isAssignableFrom(targetType.getType()))
				&& targetType.isAnnotationPresent(JSON.class);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		JsonElement jsonElement = getJsonSupport().parseJson(source);
		if (jsonElement == null) {
			return null;
		}

		return jsonElement.getAsObject(targetType.getResolvableType());
	}
}
