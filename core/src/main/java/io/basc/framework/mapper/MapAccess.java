package io.basc.framework.mapper;

import java.util.Map;

import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;

public class MapAccess<E extends Throwable> implements ObjectAccess<E> {
	private final Map<String, Object> sourceMap;

	public MapAccess(Map<String, Object> sourceMap) {
		this.sourceMap = sourceMap;
	}

	@Override
	public Elements<String> keys() throws E {
		if (sourceMap == null) {
			return Elements.empty();
		}
		return new ElementSet<>(sourceMap.keySet());
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