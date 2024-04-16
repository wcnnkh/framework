package io.basc.framework.io.scan;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;

public class TypeScanners extends Scanners<TypeScanner, MetadataReader> implements TypeScanner {

	public TypeScanners() {
		setServiceClass(TypeScanner.class);
	}

	@Override
	public Elements<MetadataReader> scan(String location, @Nullable ResourceFilter resourceFilter,
			@Nullable TypeFilter typeFilter) {
		return getSelector().apply(getServices().filter((e) -> e.canScan(location))
				.map((e) -> e.scan(location, resourceFilter, typeFilter)));
	}

}
