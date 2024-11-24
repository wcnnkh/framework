package io.basc.framework.core.env;

import java.util.Map;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.collection.KeyValuesMapping;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public class MapProperties extends KeyValuesMapping<String, Property> implements Properties {
	private final Map<?, ?> map;

	public MapProperties(@NonNull Map<?, ?> map, TypeDescriptor typeDescriptor,
			@NonNull ConversionService conversionService) {
		super(() -> Elements.of(map.keySet())
				.filter((e) -> conversionService.canConvert(typeDescriptor.getMapKeyTypeDescriptor(), String.class))
				.map((e) -> conversionService.convert(e, typeDescriptor.getMapKeyTypeDescriptor(), String.class)),
				(key) -> new MapProperty(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService));
		this.map = map;
	}

	@Override
	public Property get(String key) {
		if (map.containsKey(key)) {
			return null;
		}

		return getCreator().apply(key);
	}
}
