package scw.util.value.property;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import scw.util.value.Value;

public abstract class AbstractMapPropertyFactory<T> extends AbstractPropertyFactory{
	protected abstract Map<String, T> getMap();
	
	public Value get(String key) {
		Map<String, T> map = getMap();
		if(map == null){
			return null;
		}
		
		if(map.containsKey(key)){
			return createValue(map.get(key));
		}
		return null;
	}
	
	protected abstract Value createValue(T value);

	public Enumeration<String> enumerationKeys() {
		Map<String, T> map = getMap();
		if(map == null){
			return Collections.emptyEnumeration();
		}
		
		return Collections.enumeration(map.keySet());
	}

}
