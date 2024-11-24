package io.basc.framework.transform.map;

import java.util.Map;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MapProperties implements Properties {
	public static final TypeDescriptor UNKNOWN_TYPE_DESCRIPTOR = TypeDescriptor.map(Map.class, Object.class,
			Object.class);
	@SuppressWarnings("rawtypes")
	private final Map map;
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();

	@SuppressWarnings("rawtypes")
	public MapProperties(Map map) {
		this(map, map == null ? UNKNOWN_TYPE_DESCRIPTOR : TypeDescriptor.forObject(map));
	}

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
