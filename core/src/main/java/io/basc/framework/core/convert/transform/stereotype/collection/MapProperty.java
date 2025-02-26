package io.basc.framework.core.convert.transform.stereotype.collection;

import java.util.Map;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.transform.stereotype.Property;
import lombok.NonNull;

public class MapProperty extends GenericMapAccess implements Property {

	@SuppressWarnings("rawtypes")
	public MapProperty(@NonNull Map map, @NonNull Object key, @NonNull TypeDescriptor typeDescriptor,
			@NonNull ConversionService conversionService) {
		super(map, key, typeDescriptor, conversionService);
	}

	@Override
	public String getName() {
		Object key = getKey();
		if (key instanceof String) {
			return (String) key;
		}
		return (String) getConversionService().convert(getKey(), getMapTypeDescriptor().getMapKeyTypeDescriptor(),
				TypeDescriptor.valueOf(String.class));
	}

}
