package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.JSONSupportAccessor;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;

public class JsonConversionService extends JSONSupportAccessor implements ConversionService{

	private boolean isJsonType(Class<?> type){
		return JsonElement.class.isAssignableFrom(type) || JsonArray.class == type || JsonObject.class == type;
	}
	
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return isJsonType(sourceType.getType()) || isJsonType(targetType.getType());
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(isJsonType(sourceType.getType())){
			JsonElement jsonElement = (JsonElement) source;
			return jsonElement.getAsObject(targetType.getResolvableType().getType());
		}else{
			String text = getJsonSupport().toJSONString(source);
			return getJsonSupport().parseObject(text, targetType.getResolvableType().getType());
		}
	}

}
