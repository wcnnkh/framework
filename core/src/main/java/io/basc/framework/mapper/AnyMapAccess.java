package io.basc.framework.mapper;

import java.util.Map;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AnyMapAccess<E extends Throwable> implements ObjectAccess<E> {
	private final ConversionService conversionService;
	private Map map;
	private final TypeDescriptor mapType;

	public AnyMapAccess(Map map, TypeDescriptor mapType, ConversionService conversionService) {
		this.map = map;
		this.mapType = mapType;
		this.conversionService = conversionService;
	}

	@Override
	public Elements<String> keys() throws ConversionException {
		if (map == null || map.isEmpty()) {
			return Elements.empty();
		}
		return new ElementSet<>(map.keySet())
				.map((e) -> conversionService.convert(e, mapType.getElementTypeDescriptor(), String.class));
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
			map = CollectionFactory.createMap(mapType.getType(), mapType.getMapKeyTypeDescriptor().getType(), 16);
		}

		Object key = parameter.getName();
		key = conversionService.convert(key, mapType.getMapKeyTypeDescriptor());
		if (parameter.isPresent()) {
			map.put(key, parameter.convert(mapType.getMapValueTypeDescriptor(), conversionService));
		} else {
			map.remove(key);
		}
	}
}
