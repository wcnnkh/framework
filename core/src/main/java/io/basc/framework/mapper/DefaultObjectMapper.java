package io.basc.framework.mapper;

import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;

public class DefaultObjectMapper extends AbstractObjectMapper<Map<String, Object>, RuntimeException> {

	@Override
	public ObjectAccess<RuntimeException> getObjectAccess(Map<String, Object> source, TypeDescriptor sourceType) {
		return new MapAccess<RuntimeException>(source);
	}

}
