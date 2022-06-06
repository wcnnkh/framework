package io.basc.framework.mapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public class MapAccess<E extends Throwable> implements ObjectAccess<E> {
	private final Map<String, Object> sourceMap;

	public MapAccess(Map<String, Object> sourceMap) {
		this.sourceMap = sourceMap;
	}

	@Override
	public Enumeration<String> keys() throws E {
		return sourceMap == null ? Collections.emptyEnumeration() : Collections.enumeration(sourceMap.keySet());
	}

	@Override
	public Value get(String name) throws E {
		Object value = sourceMap.get(name);
		return value == null ? null : new AnyValue(value);
	}

	@Override
	public void set(String name, Value value) throws E {
		sourceMap.put(name, value.get());
	}

}
