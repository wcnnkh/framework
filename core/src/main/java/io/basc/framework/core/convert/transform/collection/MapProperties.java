package io.basc.framework.core.convert.transform.collection;

import java.util.Map;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Properties;
import io.basc.framework.core.convert.transform.Property;
import io.basc.framework.util.Elements;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapProperties implements Properties {
	@NonNull
	private final Map<?, ?> map;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public Elements<String> keys() {
		return Elements.of(map.keySet())
				.filter((e) -> conversionService.canConvert(typeDescriptor.getMapKeyTypeDescriptor(), String.class))
				.map((e) -> conversionService.convert(e, typeDescriptor.getMapKeyTypeDescriptor(), String.class));
	}

	@Override
	public Property get(String key) {
		return new MapProperty(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService);
	}

	@Override
	public Elements<Property> getAccesses(@NonNull Object key) {
		if (key instanceof String) {
			return Elements.singleton(get((String) key));
		}
		return Elements.empty();
	}
}
