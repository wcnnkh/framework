package io.basc.framework.core.scan;

import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public class TypeScanners extends Scanners<TypeScanner, MetadataReader> implements TypeScanner {

	public TypeScanners() {
		setServiceClass(TypeScanner.class);
	}

	@Override
	public Elements<MetadataReader> scan(@NonNull String location, ResourceFilter resourceFilter,
			TypeFilter typeFilter) {
		return getSelector().apply(
				this.filter((e) -> e.canScan(location)).map((e) -> e.scan(location, resourceFilter, typeFilter)));
	}

}
