package io.basc.framework.convert.lang;

import java.util.function.Function;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.annotation.JSON;
import io.basc.framework.core.Ordered;
import io.basc.framework.json.JsonSupportAccessor;

public class ObjectToStringConverter extends JsonSupportAccessor
		implements ConversionService, Function<Object, String>, Ordered {

	public String apply(Object o) {
		return o == null ? null : o.toString();
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		return targetType.getType() == String.class;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		JSON json = sourceType.getAnnotation(JSON.class);
		if (json == null) {
			return apply(source);
		}

		return getJsonSupport().toJsonString(source);
	}
}
