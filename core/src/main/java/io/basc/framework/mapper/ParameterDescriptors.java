package io.basc.framework.mapper;

import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.util.Elements;

public interface ParameterDescriptors extends AnnotatedElement {
	Class<?> getDeclaringClass();

	Object getSource();

	Elements<ParameterDescriptor> getElements();

	default Map<String, Object> getParameterMap(Object[] args) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(args.length);
		Iterator<ParameterDescriptor> iterator = getElements().iterator();
		for (int i = 0; i < args.length && iterator.hasNext(); i++) {
			ParameterDescriptor parameterDescriptor = iterator.next();
			map.put(parameterDescriptor.getName(), args[i]);
		}
		return map;
	}
}
