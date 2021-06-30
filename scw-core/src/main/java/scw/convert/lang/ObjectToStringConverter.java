package scw.convert.lang;

import scw.convert.ConversionException;
import scw.convert.ConversionService;
import scw.convert.Converter;
import scw.convert.TypeDescriptor;
import scw.convert.annotation.JSON;
import scw.core.Ordered;
import scw.json.JSONSupportAccessor;

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
