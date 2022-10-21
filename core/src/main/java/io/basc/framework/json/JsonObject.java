package io.basc.framework.json;

import io.basc.framework.value.PropertyFactory;

public interface JsonObject extends Json<String>, PropertyFactory {
	static final String PREFIX = "{";
	static final String SUFFIX = "}";

	boolean remove(String key);

	boolean put(String key, Object value);
}