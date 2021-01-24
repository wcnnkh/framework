package scw.value.factory.support;

import java.util.Iterator;
import java.util.Map;

import scw.value.factory.PropertyFactory;

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
