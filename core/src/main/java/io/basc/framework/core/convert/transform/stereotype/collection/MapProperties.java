package io.basc.framework.core.convert.transform.stereotype.collection;

import java.util.Map;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RequiredArgsConstructor
public class MapProperties implements Properties {
	@NonNull
	private final Map map;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public Elements<Property> getElements() {
		return Elements.of(map.keySet()).map((key) -> createProperty(key));
	}

	@Override
	public Property get(String key) {
		return map.containsKey(key) ? createProperty(key) : null;
	}

	@Override
	public boolean hasKey(String key) {
		return map.containsKey(key);
	}

	@Override
	public Elements<Property> getValues(String key) {
		Property property = get(key);
		return property == null ? Elements.empty() : Elements.singleton(property);
	}

	private Property createProperty(Object key) {
		return new MapProperty(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService);
	}

	@Override
	public Elements<Property> getAccessors(@NonNull Object key) {
		return Elements.singleton(createProperty(key));
	}

	public void put(Object key, Object value) {
		if (map == null) {
			throw new UnsupportedOperationException("The map container does not exist");
		}

		((Map<Object, Object>) map).put(key, value);
	}
}
