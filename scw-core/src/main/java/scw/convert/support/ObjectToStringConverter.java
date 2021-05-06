package scw.convert.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import scw.convert.ConversionException;
import scw.convert.ConversionService;
import scw.convert.Converter;
import scw.convert.TypeDescriptor;
import scw.json.JSONSupport;
import scw.json.JSONUtils;

public class ObjectToStringConverter implements ConversionService, Converter<Object, String> {
	private JSONSupport jsonSupport;

	public JSONSupport getJsonSupport() {
		return jsonSupport == null ? JSONUtils.getJsonSupport() : jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public String convert(Object o) {
		return String.valueOf(o);
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

	@Target({ElementType.FIELD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface JSON {
	}
}
