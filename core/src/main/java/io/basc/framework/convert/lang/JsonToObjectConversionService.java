package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.JSON;

public class JsonToObjectConversionService extends AbstractConversionService {

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return sourceType.isAnnotationPresent(JSON.class) || targetType.isAnnotationPresent(JSON.class);
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		String content = (String) getConversionService().convert(source, sourceType,
				TypeDescriptor.valueOf(String.class));
		if (content == null) {
			return null;
		}

		return getJsonSupport().parseJson(content).getAsObject(targetType);
	}
}
