package run.soeasy.framework.core.transform.indexed;

import java.util.Map;

import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.indexed.collection.MapMapping;

@FunctionalInterface
public interface PropertyMapping extends IndexedMapping<IndexedAccessor> {
	public static interface PropertyMappingWrapper<W extends PropertyMapping>
			extends PropertyMapping, IndexedMappingWrapper<IndexedAccessor, W> {
	}

	public static PropertyMapping forMap(Map<? extends String, ?> map) {
		return new MapMapping(map, TypeDescriptor.map(map.getClass(), String.class, Object.class));
	}
}
