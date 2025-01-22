package io.basc.framework.core.convert.transform.stereotype.collection;

import java.util.Map;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class MapTemplate<K> extends KeyValuesTemplate<K, GenericMapAccess> {

	/**
	 * @param map
	 * @param valueTypeDescriptor 值的类型
	 * @param conversionService
	 */
	public MapTemplate(@NonNull Map<? extends K, ?> map, @NonNull TypeDescriptor valueTypeDescriptor,
			@NonNull ConversionService conversionService) {
		super(() -> Elements.of(map.keySet()),
				(key) -> new GenericMapAccess(map, key, valueTypeDescriptor, conversionService));
	}
}
