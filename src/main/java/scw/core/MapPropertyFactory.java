package scw.core;

import java.util.Map;

public final class MapPropertyFactory extends SystemPropertyFactory {
	@SuppressWarnings("rawtypes")
	private final Map map;

	@SuppressWarnings("rawtypes")
	public MapPropertyFactory(Map map) {
		this.map = map;
	}

	@Override
	public String getProperty(String key) {
		if(map == null){
			return super.getProperty(key);
		}
		
		Object value = map.get(key);
		return value == null ? super.getProperty(key) : value.toString();
	}

}
