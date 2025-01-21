package io.basc.framework.core.convert.transform.collection;

import java.util.Map;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.transform.Property;
import lombok.NonNull;

public class MapProperty extends GenericMapAccess implements Property {

	@SuppressWarnings("rawtypes")
	public MapProperty(@NonNull Map map, @NonNull String key, @NonNull TypeDescriptor typeDescriptor,
			@NonNull ConversionService conversionService) {
		super(map, key, typeDescriptor, conversionService);
	}

	@Override
	public String getName() {
		return (String) getKey();
	}

}
