package io.basc.framework.core.convert.transform.collection;

import java.util.Map;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public class MapMapping<K> extends KeyValuesMapping<K, GenericMapAccess> {

	/**
	 * @param map
	 * @param valueTypeDescriptor 值的类型
	 * @param conversionService
	 */
	public MapMapping(@NonNull Map<? extends K, ?> map, @NonNull TypeDescriptor valueTypeDescriptor,
			@NonNull ConversionService conversionService) {
		super(() -> Elements.of(map.keySet()),
				(key) -> new GenericMapAccess(map, key, valueTypeDescriptor, conversionService));
	}
}
