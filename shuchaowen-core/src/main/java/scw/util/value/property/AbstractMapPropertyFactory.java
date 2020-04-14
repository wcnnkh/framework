package scw.util.value.property;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Assert;
import scw.io.resource.ResourceOperations;
import scw.util.value.StringValue;
import scw.util.value.Value;

public abstract class AbstractMapPropertyFactory extends
		AbstractPropertyFactory {
	protected abstract Map<String, Value> getTargetMap();

	public Value get(String key) {
		return getTargetMap().get(key);
	}

	public Enumeration<String> enumerationKeys() {
		return Collections.enumeration(getTargetMap().keySet());
	}

	public Value remove(String key) {
		Assert.requiredArgument(key != null, "key");
		return getTargetMap().remove(key);
	}

	public Value put(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return getTargetMap().put(key, value);
	}

	public Value put(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, createValue(value));
	}

	protected Value createValue(Object value) {
		if (value instanceof Value) {
			return (Value) value;
		}

		return new StringValue(value == null ? null : value.toString());
	}

	public void clear() {
		getTargetMap().clear();
	}

	public void loadProperties(ResourceOperations resourceOperations,
			String resource) {
		if (resourceOperations.isExist(resource)) {
			Properties properties = resourceOperations.getProperties(resource);
			if (properties != null) {
				loadProperties(properties);
			}
		}
	}

	public void loadProperties(Properties properties) {
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

				put(key.toString(), value);
			}
		}
	}
}
