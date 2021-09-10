package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.JSON;
import io.basc.framework.json.JSONSupportAccessor;
import io.basc.framework.json.JsonElement;

public class JsonToObjectConversionService extends JSONSupportAccessor implements ConversionService {

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if(sourceType == null || targetType == null) {
			return false;
		}
		
		return targetType.isAnnotationPresent(JSON.class);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		JsonElement jsonElement = getJsonSupport().parseJson(source);
		if (jsonElement == null) {
			return null;
		}

		return jsonElement.getAsObject(targetType);
	}
}
