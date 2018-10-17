package shuchaowen.core.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class PrimaryKeyValue<T> implements Serializable{
	private static final long serialVersionUID = 4587200583501849080L;
	private Map<String, T> dataMap;

	private String getKey(Object... params) {
		StringBuilder sb = new StringBuilder(64);
		for (int i = params.length - 1; i >= 0; i--) {
			sb.append(params[i]);
			sb.append("#");
		}
		return sb.toString();
	}
	
	public void put(Object[] primaryKey, T value) {
		if(dataMap == null){
			dataMap = new HashMap<String, T>();
		}
		dataMap.put(getKey(primaryKey), value);
	}

	public void put(PrimaryKeyParameter primaryKey, T value) {
		if(dataMap == null){
			dataMap = new HashMap<String, T>();
		}
		dataMap.put(getKey(primaryKey.getParams()), value);
	}

	public T get(Object... primaryKey) {
		return dataMap == null? null:dataMap.get(getKey(primaryKey));
	}
	
	public T get(PrimaryKeyParameter primaryKey) {
		return dataMap == null? null:dataMap.get(getKey(primaryKey.getParams()));
	}
	
	public Collection<T> values(){
		return dataMap == null? null:dataMap.values();
	}
	
	public void putAll(PrimaryKeyValue<T> primaryKeyValue){
		if(dataMap == null){
			dataMap = new HashMap<String, T>();
		}
		
		dataMap.putAll(primaryKeyValue.dataMap);
	}
}
