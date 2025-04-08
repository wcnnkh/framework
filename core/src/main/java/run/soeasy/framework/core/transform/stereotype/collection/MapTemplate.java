package run.soeasy.framework.core.transform.stereotype.collection;

import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.collection.Elements;

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
