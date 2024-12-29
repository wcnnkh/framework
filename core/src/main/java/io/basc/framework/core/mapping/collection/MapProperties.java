package io.basc.framework.core.mapping.collection;

import java.util.Map;
import java.util.Set;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.mapping.Properties;
import io.basc.framework.core.mapping.Property;
import io.basc.framework.util.Elements;
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
	private ConversionService conversionService;

	@Override
	public Elements<String> keys() {
		return Elements.of((Set<Object>) map.keySet())
				.filter((e) -> conversionService.canConvert(typeDescriptor.getMapKeyTypeDescriptor(),
						TypeDescriptor.valueOf(String.class)))
				.map((e) -> (String) conversionService.convert(e, typeDescriptor.getMapKeyTypeDescriptor(),
						TypeDescriptor.valueOf(String.class)));
	}

	@Override
	public Property get(String key) {
		return new MapProperty(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService);
	}

	@Override
	public Elements<Property> getAccessors(@NonNull Object key) {
		if (key instanceof String) {
			return Elements.singleton(get((String) key));
		}
		return Elements.empty();
	}

	public void put(Object key, Object value) {
		if (map == null) {
			throw new UnsupportedOperationException("The map container does not exist");
		}

		((Map<Object, Object>) map).put(key, value);
	}
}
