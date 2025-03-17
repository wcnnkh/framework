package run.soeasy.framework.core.scan;

import lombok.NonNull;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.core.type.filter.TypeFilter;
import run.soeasy.framework.util.collections.Elements;

public interface TypeScanner extends Scanner<MetadataReader> {
	@Override
	default Elements<MetadataReader> scan(@NonNull String locationPattern, ResourceFilter resourceFilter) {
		return scan(locationPattern, resourceFilter, null);
	}

	Elements<MetadataReader> scan(@NonNull String locationPattern, ResourceFilter resourceFilter,
			TypeFilter typeFilter);
}
