package io.basc.framework.mapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public class MapToObjectMapper extends AbstractObjectMapper<Map<String, Object>, RuntimeException> {

	@Override
	public void reverseTransform(Object value, ParameterDescriptor descriptor, Map<String, Object> target,
			TypeDescriptor targetType) throws RuntimeException {
		target.put(descriptor.getName(), value);
	}

	@Override
	public Enumeration<String> keys(Map<String, Object> source) {
		return Collections.enumeration(source.keySet());
	}

	@Override
	public Processor<String, Value, RuntimeException> getValueByNameProcessor(Map<String, Object> source,
			TypeDescriptor sourceType, TypeDescriptor targetType) throws RuntimeException {
		return (name) -> new AnyValue(source.get(name));
	}
}
