package run.soeasy.framework.core.transform.indexed;

import lombok.NonNull;
import run.soeasy.framework.core.transform.indexed.IndexedMapping.IndexedMappingWrapper;

public class RandomAccessIndexedMapping<T extends IndexedAccessor, W extends IndexedMapping<T>>
		extends RandomAccessIndexedTemplate<T, W> implements IndexedMappingWrapper<T, W> {

	public RandomAccessIndexedMapping(@NonNull W source) {
		super(source);
	}

	@Override
	public IndexedMapping<T> randomAccess() {
		return this;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return IndexedMappingWrapper.super.size();
	}
}
