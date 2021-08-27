package io.basc.framework.core.parameter;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ParameterDescriptors extends Iterable<ParameterDescriptor> {
	Class<?> getDeclaringClass();

	int size();

	Object getSource();

	Class<?>[] getTypes();

	ParameterDescriptor getParameterDescriptor(int index);

	ParameterDescriptor getParameterDescriptor(String name);

	ParameterDescriptor[] toArray();

	default Map<String, Object> getParameterMap(Object[] args) {
		int size = size();
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(size);
		for (int i = 0; i < size; i++) {
			ParameterDescriptor parameterDescriptor = getParameterDescriptor(i);
			map.put(parameterDescriptor.getName(), args[i]);
		}
		return map;
	}
}
