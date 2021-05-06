package scw.convert.support;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.json.JsonArray;
import scw.json.JsonElement;
import scw.json.JsonObject;

public class JsonConversionService implements ConversionService{
	private JSONSupport jsonSupport;
	
	public JSONSupport getJsonSupport() {
		return jsonSupport == null? JSONUtils.getJsonSupport():jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

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
