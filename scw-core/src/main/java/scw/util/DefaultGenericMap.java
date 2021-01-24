package scw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultGenericMap<K, V> extends AbstractGenericMap<K, V> {
	private final java.util.Map<K, V> targetMap;
	
	public DefaultGenericMap(boolean concurrent) {
		this(concurrent ? new ConcurrentHashMap<K, V>() : new HashMap<K, V>());
	}
	
	public DefaultGenericMap(java.util.Map<K, V> targetMap) {
		this.targetMap = targetMap;
	}

	@Override
	protected Map<K, V> getTargetMap() {
		return targetMap;
	}
	
	@Override
	public DefaultGenericMap<K, V> clone() {
		if(targetMap instanceof ConcurrentHashMap){
			return new DefaultGenericMap<K, V>(new ConcurrentHashMap<K, V>(targetMap));
		}else{
			return new DefaultGenericMap<K, V>(new HashMap<K, V>(targetMap));
		}
	}
}
