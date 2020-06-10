package scw.io.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.io.ResourceUtils;
import scw.value.StringValue;
import scw.value.Value;

public class MapBuilder implements Serializable {
	private static final long serialVersionUID = 1L;
	protected final Map<Object, Value> valueMap = createValueMap();

	public Map<Object, Value> getValueMap() {
		return Collections.unmodifiableMap(valueMap);
	}

	protected Map<Object, Value> createValueMap() {
		return new HashMap<Object, Value>(8);
	}

	public void loading(Properties properties) {
		if (properties == null) {
			return;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			valueMap.put(entry.getKey(), new StringValue(entry.getValue().toString()));
		}
	}

	public void loading(String resource) {
		Properties properties = ResourceUtils.getResourceOperations().getFormattedProperties(resource);
		loading(properties);
	}
}
