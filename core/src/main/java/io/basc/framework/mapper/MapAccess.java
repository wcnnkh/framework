package io.basc.framework.mapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

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
	public Parameter get(String name) throws E {
		Object value = sourceMap.get(name);
		return value == null ? null : new Parameter(name, value);
	}

	@Override
	public void set(Parameter parameter) throws E {
		sourceMap.put(parameter.getName(), parameter.get());
	}

}