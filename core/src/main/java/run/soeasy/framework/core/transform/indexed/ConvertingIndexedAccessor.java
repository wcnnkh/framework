package run.soeasy.framework.core.transform.indexed;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.ConvertingValue;
import run.soeasy.framework.core.transform.indexed.IndexedDescriptor.IndexedDescriptorWrapper;

public class ConvertingIndexedAccessor<W extends IndexedDescriptor> extends ConvertingValue<W>
		implements IndexedAccessor, IndexedDescriptorWrapper<W> {
	private static final long serialVersionUID = 1L;

	public ConvertingIndexedAccessor(@NonNull W source) {
		super(source);
	}

	@Override
	public IndexedAccessor reindex(Object index) {
		return IndexedAccessor.super.reindex(index);
	}
}
