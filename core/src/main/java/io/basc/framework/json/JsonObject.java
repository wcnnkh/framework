package io.basc.framework.json;

import io.basc.framework.util.Pair;

import java.util.Set;

public interface JsonObject extends Json<String>, Iterable<Pair<String, JsonElement>> {
	static final String PREFIX = "{";
	static final String SUFFIX = "}";

	Set<String> keySet();

	boolean remove(String key);

	boolean put(String key, Object value);
}