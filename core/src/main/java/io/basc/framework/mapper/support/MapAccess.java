package io.basc.framework.mapper.support;

import java.util.Map;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Parameter;
import io.basc.framework.mapper.access.ObjectAccess;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapAccess implements ObjectAccess {
	private final ConversionService conversionService;
	private Map map;
	private final TypeDescriptor typeDescriptor;

	public MapAccess(Map map, TypeDescriptor typeDescriptor, ConversionService conversionService) {
		this.map = map;
		this.typeDescriptor = typeDescriptor;
		this.conversionService = conversionService;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Elements<String> keys() throws ConversionException {
		if (map == null || map.isEmpty()) {
			return Elements.empty();
		}
		return new ElementSet<>(map.keySet())
				.map((e) -> conversionService.convert(e, typeDescriptor.getElementTypeDescriptor(), String.class));
	}

	@Override
	public Parameter get(String name) throws ConversionException {
		if (map == null) {
			return null;
		}

		Object value = map.get(name);
		if (value == null) {
			return null;
		}
		return new Parameter(name, value);
	}

	@Override
	public void set(Parameter parameter) throws ConversionException {
		if (parameter == null || parameter.isEmpty()) {
			return;
		}

		if (map == null) {
			map = CollectionFactory.createMap(typeDescriptor.getType(),
					typeDescriptor.getMapKeyTypeDescriptor().getType(), 16);
		}

		Object key = parameter.getName();
		key = conversionService.convert(key, typeDescriptor.getMapKeyTypeDescriptor());
		if (parameter.isPresent()) {
			map.put(key, parameter.convert(typeDescriptor.getMapValueTypeDescriptor(), conversionService));
		} else {
			map.remove(key);
		}
	}
}
