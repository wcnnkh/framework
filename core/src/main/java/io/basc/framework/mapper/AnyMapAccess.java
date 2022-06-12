package io.basc.framework.mapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConvertibleEnumeration;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.CollectionFactory;

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
	public Enumeration<String> keys() throws ConversionException {
		if (map == null || map.isEmpty()) {
			return Collections.emptyEnumeration();
		}

		return new ConvertibleEnumeration<Object, String>(Collections.enumeration(map.keySet()),
				(e) -> conversionService.convert(e, mapType.getElementTypeDescriptor(), String.class));
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
		if(parameter == null || parameter.isEmpty()) {
			return ;
		}
		
		if (map == null) {
			map = CollectionFactory.createMap(mapType.getType(), mapType.getMapKeyTypeDescriptor().getType(), 16);
		}

		Object key = parameter.getName();
		key = conversionService.convert(key, mapType.getMapKeyTypeDescriptor());
		if(parameter.isNull()) {
			map.remove(key);
		}else {
			map.put(key, parameter.convert(mapType.getMapValueTypeDescriptor(), conversionService));
		}
	}
}
