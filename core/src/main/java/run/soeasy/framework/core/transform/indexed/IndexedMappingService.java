package run.soeasy.framework.core.transform.indexed;

import run.soeasy.framework.core.transform.service.MappingService;

public class IndexedMappingService<T extends IndexedAccessor> extends MappingService<Object, T, IndexedMapping<T>> {
	public IndexedMappingService() {
		
	}
}