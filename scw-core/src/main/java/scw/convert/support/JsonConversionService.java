package scw.convert.support;

import scw.convert.TypeDescriptor;
import scw.json.JSONSupport;
import scw.json.JsonElement;

public class JsonConversionService extends AbstractConversionService{
	private final JSONSupport jsonSupport;
	
	public JsonConversionService(JSONSupport jsonSupport){
		this.jsonSupport = jsonSupport;
	}
	
	private boolean isJsonType(Class<?> type){
		return JsonElement.class.isAssignableFrom(type);
	}
	
	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return isJsonType(sourceType.getType()) || isJsonType(targetType.getType());
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(isJsonType(sourceType.getType())){
			JsonElement jsonElement = (JsonElement) source;
			return jsonElement.getAsObject(targetType.getResolvableType().getType());
		}else{
			String text = jsonSupport.toJSONString(source);
			return jsonSupport.parseObject(text, targetType.getResolvableType().getType());
		}
	}

}
