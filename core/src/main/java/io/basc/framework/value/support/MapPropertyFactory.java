package io.basc.framework.value.support;

import io.basc.framework.value.PropertyFactory;

import java.util.Iterator;
import java.util.Map;

public class MapPropertyFactory extends MapValueFactory<String> implements PropertyFactory{
	
	public MapPropertyFactory(Map<String, ?> map){
		super(map);
	}
	
	public Iterator<String> iterator() {
		return map.keySet().iterator();
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

}
