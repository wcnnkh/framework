package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.JSON;
import io.basc.framework.core.Ordered;
import io.basc.framework.json.JSONSupportAccessor;

public class ObjectToStringConverter extends JSONSupportAccessor implements ConversionService, Converter<Object, String>, Ordered {

	public String convert(Object o) {
		return o == null? null:o.toString();
	}
	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return targetType.getType() == String.class;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		JSON json = sourceType.getAnnotation(JSON.class);
		if (json == null) {
			return convert(source);
		}

		return getJsonSupport().toJSONString(source);
	}
}
