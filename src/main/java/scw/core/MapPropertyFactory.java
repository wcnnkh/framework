package scw.core;

import java.util.Map;

public final class MapPropertyFactory extends SystemPropertyFactory {
	@SuppressWarnings("rawtypes")
	private final Map map;
	private final boolean includeSystem;

	@SuppressWarnings("rawtypes")
	public MapPropertyFactory(Map map, boolean includeSystem) {
		this.map = map;
		this.includeSystem = includeSystem;
	}

	@Override
	public String getProperty(String key) {
		if (map == null) {
			return getSystemProperty(key);
		}

		Object value = map.get(key);
		return value == null ? getSystemProperty(key) : value.toString();
	}

	private String getSystemProperty(String key) {
		return includeSystem ? super.getProperty(key) : null;
	}
}
