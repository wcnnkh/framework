package io.basc.framework.transform.map;

import java.util.Map;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.transform.Property;
import io.basc.framework.util.ObjectUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapProperty implements Property {
	private final Map<Object, Object> map;
	private final TypeDescriptor mapTypeDescriptor;
	private final Object name;
	private final ConversionService conversionService;

	@Override
	public Object getValue() {
		return map.get(name);
	}

	@Override
	public boolean isPresent() {
		return map.containsKey(name);
	}

	@Override
	public String getName() {
		if (conversionService.canConvert(mapTypeDescriptor.getMapKeyTypeDescriptor(), String.class)) {
			return conversionService.convert(name, mapTypeDescriptor.getMapKeyTypeDescriptor(), String.class);
		} else {
			return ObjectUtils.toString(name);
		}
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return mapTypeDescriptor.getMapValueTypeDescriptor();
	}

	@Override
	public void setValue(Object value) {
		map.put(name, value);
	}
}
