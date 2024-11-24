package io.basc.framework.json.convert;

import java.util.function.Function;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.annotation.JSON;
import io.basc.framework.json.JsonSupportAccessor;
import io.basc.framework.util.ObjectUtils;

public class ObjectToString extends JsonSupportAccessor
		implements Converter<Object, String, ConversionException>, Function<Object, String> {
	public static final ObjectToString DEFAULT = new ObjectToString();

	@Override
	public String apply(Object t) {
		return ObjectUtils.toString(t);
	}

	@Override
	public String convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		JSON json = sourceType.getAnnotation(JSON.class);
		if (json == null) {
			return apply(source);
		}

		return getJsonSupport().toJsonString(source);
	}

}
