package io.basc.framework.orm.convert;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConvertibleEnumeration;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectAccess;
import io.basc.framework.mapper.Parameter;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapAccess implements ObjectAccess<ConversionException> {
	private final ConversionService conversionService;
	private final Map map;
	private final TypeDescriptor mapType;

	public MapAccess(Map map, TypeDescriptor mapType, ConversionService conversionService) {
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
		Object value = map.get(name);
		if (value == null) {
			return null;
		}
		return new Parameter(name, value);
	}

	@Override
	public void set(Parameter parameter) throws ConversionException {
		Object key = parameter.getName();
		key = conversionService.convert(key, mapType.getMapKeyTypeDescriptor());
		map.put(key, parameter.convert(mapType.getMapValueTypeDescriptor(), conversionService));
	}
}
