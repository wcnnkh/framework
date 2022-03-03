package io.basc.framework.json;

import java.util.Set;
import java.util.stream.Stream;

import io.basc.framework.util.Pair;
import io.basc.framework.util.XUtils;

public interface JsonObject extends Json<String>, Iterable<Pair<String, JsonElement>> {
	static final String PREFIX = "{";
	static final String SUFFIX = "}";

	Set<String> keySet();

	boolean remove(String key);

	boolean put(String key, Object value);

	default Stream<Pair<String, JsonElement>> stream() {
		return XUtils.stream(this.iterator());
	}
}