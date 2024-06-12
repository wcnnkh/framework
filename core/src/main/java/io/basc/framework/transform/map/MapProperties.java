package io.basc.framework.transform.map;

import java.util.Map;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.element.Elements;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapProperties implements Properties {
	@SuppressWarnings("rawtypes")
	private final Map map;
	private final TypeDescriptor typeDescriptor;
	private final ConversionService conversionService;

	@SuppressWarnings({ "unchecked" })
	@Override
	public Elements<Property> getElements() {
		if (map == null) {
			return Elements.empty();
		}

		return Elements.of(map.keySet()).map((name) -> new MapProperty(map, typeDescriptor, name, conversionService));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Elements<Property> getElements(String name) {
		Elements<Property> elements = Properties.super.getElements(name);
		if (elements.isEmpty()) {
			return Elements.singleton(new MapProperty(map, typeDescriptor, name, conversionService));
		}
		return elements;
	}
}
