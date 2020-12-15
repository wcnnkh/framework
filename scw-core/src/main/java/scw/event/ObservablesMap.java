package scw.event;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;


public class ObservablesMap<K, V> extends Observables<java.util.Map<K, V>>{

	public ObservablesMap(boolean concurrent) {
		super(concurrent);
	}

	@Override
	protected Map<K, V> merge(List<Map<K, V>> list) {
		if(CollectionUtils.isEmpty(list)){
			return Collections.emptyMap();
		}
		
		Map<K, V> map = new LinkedHashMap<K, V>();
		for(Map<K, V> item : list){
			if(CollectionUtils.isEmpty(item)){
				continue;
			}
			
			map.putAll(item);
		}
		
		if(map.isEmpty()){
			return Collections.emptyMap();
		}
		
		return Collections.unmodifiableMap(map);
	}


}
