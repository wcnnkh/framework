package scw.util.value;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import scw.core.utils.StringUtils;
import scw.json.JSONUtils;

public class StringValue extends AbstractValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;

	public StringValue(String value) {
		this.value = value;
	}

	public String getAsString() {
		return value;
	}
	
	public boolean isSupportArray(){
		return true;
	}
	
	public String[] split(String value){
		return StringUtils.commonSplit(value);
	}
	
	protected Value parseValue(String text){
		return new StringValue(text);
	}
	
	@SuppressWarnings("unchecked")
	public <E> E[] getAsArray(Class<? extends E> componentType){
		String[] values = split(getAsString());
		if(values == null){
			return null;
		}
		
		Object array = Array.newInstance(componentType, value.length());
		for(int i=0; i<values.length; i++){
			Value value = parseValue(values[i]);
			if(value != null){
				Array.set(array, i, value.getAsObject(componentType));
			}
		}
		return (E[]) array;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAsObject(Class<? extends T> type) {
		if(isSupportArray() && type.isArray()){
			return (T) getAsArray(type.getComponentType());
		}
		return super.getAsObject(type);
	}
	
	@Override
	protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
		return JSONUtils.parseObject(getAsString(), type);
	}
	
	@Override
	protected <T> T getAsObjectNotSupport(Type type) {
		return JSONUtils.parseObject(getAsString(), type);
	}
}