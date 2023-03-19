package io.basc.framework.mapper;

import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.util.StringUtils;

public interface ParameterDescriptors extends AnnotatedElement, Iterable<ParameterDescriptor> {
	Class<?> getDeclaringClass();

	int size();

	Object getSource();

	Class<?>[] getTypes();

	ParameterDescriptor[] toArray();

	default ParameterDescriptor getParameterDescriptor(int index) {
		int i = 0;
		Iterator<ParameterDescriptor> iterator = iterator();
		while (iterator.hasNext()) {
			ParameterDescriptor descriptor = iterator.next();
			if (i == index) {
				return descriptor;
			}
			i++;
		}
		return null;
	}

	default ParameterDescriptor getParameterDescriptor(String name) {
		for (ParameterDescriptor descriptor : this) {
			if (StringUtils.equals(descriptor.getName(), name)) {
				return descriptor;
			}
		}
		return null;
	}

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
