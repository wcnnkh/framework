package io.basc.framework.io.scan;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public interface TypeScanner extends Scanner<MetadataReader> {
	@Override
	default Elements<MetadataReader> scan(String locationPattern, @Nullable ResourceFilter resourceFilter) {
		return scan(locationPattern, resourceFilter, null);
	}

	Elements<MetadataReader> scan(String locationPattern, @Nullable ResourceFilter resourceFilter,
			@Nullable TypeFilter typeFilter);
}
