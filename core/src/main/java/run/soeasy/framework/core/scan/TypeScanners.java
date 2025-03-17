package run.soeasy.framework.core.scan;

import lombok.NonNull;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.core.type.filter.TypeFilter;
import run.soeasy.framework.util.collections.Elements;

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
