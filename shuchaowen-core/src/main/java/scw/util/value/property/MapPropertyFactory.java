package scw.util.value.property;

import java.util.Map;

import scw.util.value.StringValue;
import scw.util.value.Value;

public class MapPropertyFactory<T> extends AbstractMapPropertyFactory<T>{
	private Map<String, T> map;
	
	public MapPropertyFactory(Map<String, T> map){
		this.map = map;
	}
	
	@Override
	protected Map<String, T> getMap() {
		return map;
	}

	@Override
	protected Value createValue(T value) {
		return value == null? null:new StringValue(value.toString());
	}
}
