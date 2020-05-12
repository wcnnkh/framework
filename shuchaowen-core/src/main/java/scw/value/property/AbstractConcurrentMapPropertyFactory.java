package scw.value.property;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import scw.io.support.ResourceOperations;
import scw.value.Value;

public abstract class AbstractConcurrentMapPropertyFactory extends
		AbstractMapPropertyFactory {

	@Override
	protected abstract ConcurrentMap<String, Value> getTargetMap();

	public Value putIfAbsent(String key, Value value) {
		return getTargetMap().putIfAbsent(key, value);
	}

	public Value putIfAbsent(String key, Object value) {
		return getTargetMap().putIfAbsent(key, createValue(value));
	}

	public void loadProperties(ResourceOperations resourceOperations,
			String resource, boolean putIfAbsent) {
		if (putIfAbsent) {
			if (resourceOperations.isExist(resource)) {
				Properties properties = resourceOperations
						.getFormattedProperties(resource);
				if (properties != null) {
					loadProperties(properties, putIfAbsent);
				}
			}
		} else {
			loadProperties(resourceOperations, resource);
		}
	}

	public void loadProperties(Properties properties, boolean putIfAbsent) {
		if (putIfAbsent) {
			if (properties != null) {
				for (Entry<Object, Object> entry : properties.entrySet()) {
					Object key = entry.getKey();
					if (key == null) {
						continue;
					}

					Object value = entry.getValue();
					if (value == null) {
						continue;
					}

					putIfAbsent(key.toString(), value);
				}
			}
		} else {
			loadProperties(properties);
		}
	}
}
