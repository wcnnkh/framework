package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.support.GlobalConversionService;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;
import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonUtils;
import lombok.Data;
import lombok.NonNull;

@Data
public class ValueConverter implements ConversionService {
	private static volatile ValueConverter instance;

	public static ValueConverter getInstance() {
		if (instance == null) {
			synchronized (ValueConverter.class) {
				if (instance == null) {
					instance = new ValueConverter();
				}
			}
		}
		return instance;
	}

	@NonNull
	private ConversionService conversionService = GlobalConversionService.getInstance();
	private JsonSupport jsonSupport;

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canConvertToJson(sourceType, sourceType, targetType)
				|| getConversionService().canConvert(sourceType, targetType);
	}

	protected boolean canConvertToJson(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source instanceof String) {
			String text = (String) source;
			if (targetType.isArray() || targetType.isCollection()) {
				if (text.startsWith(JsonArray.PREFIX) && text.endsWith(JsonArray.SUFFIX)) {
					return true;
				}
			} else if (text.startsWith(JsonObject.PREFIX) && text.endsWith(JsonObject.SUFFIX)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (canConvertToJson(source, sourceType, targetType)) {
			return getJsonSupport().convert(source, sourceType, targetType);
		}
		return getConversionService().convert(source, sourceType, targetType);
	}

	public JsonSupport getJsonSupport() {
		return jsonSupport == null ? JsonUtils.getSupport() : jsonSupport;
	}

}
