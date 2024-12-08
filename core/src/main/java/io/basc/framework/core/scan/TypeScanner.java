package io.basc.framework.core.scan;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public interface TypeScanner extends Scanner<MetadataReader> {
	@Override
	default Elements<MetadataReader> scan(@NonNull String locationPattern, ResourceFilter resourceFilter) {
		return scan(locationPattern, resourceFilter, null);
	}

	Elements<MetadataReader> scan(@NonNull String locationPattern, ResourceFilter resourceFilter,
			TypeFilter typeFilter);
}
