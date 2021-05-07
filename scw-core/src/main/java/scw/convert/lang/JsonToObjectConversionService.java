package scw.convert.lang;

import java.io.Reader;

import scw.convert.ConversionException;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.annotation.JSON;
import scw.json.JSONSupportAccessor;
import scw.json.JsonElement;

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
