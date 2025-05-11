package run.soeasy.framework.core.transform.indexed;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

public interface IndexedAccessor extends TypedValueAccessor, IndexedDescriptor {

	public static interface IndexedAccessorWrapper<W extends IndexedAccessor>
			extends IndexedAccessor, TypedValueAccessorWrapper<W>, IndexedDescriptorWrapper<W> {
		@Override
		default IndexedAccessor reindex(Object index) {
			return getSource().reindex(index);
		}
	}

	public static class ReindexIndexedAccessor<W extends IndexedAccessor> extends IndexedAccessibleDescriptor<W>
			implements IndexedAccessorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public ReindexIndexedAccessor(@NonNull W source, Object index) {
			super(source, index);
		}

		@Override
		public IndexedAccessor reindex(Object index) {
			return new ReindexIndexedAccessor<>(getSource(), index);
		}
	}

	@Override
	default IndexedAccessor reindex(Object index) {
		return new ReindexIndexedAccessor<>(this, index);
	}

	public static IndexedAccessor forIndexedDescriptor(IndexedDescriptor indexedDescriptor) {
		return new ConvertingIndexedAccessor<>(indexedDescriptor);
	}
}
