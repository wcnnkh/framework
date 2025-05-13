package run.soeasy.framework.core.transform.indexed;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor;

public interface IndexedDescriptor extends AccessibleDescriptor {
	public static final IndexedDescriptor[] EMPTY_INDEXED_DESCRIPTORS = new IndexedDescriptor[0];

	@FunctionalInterface
	public static interface IndexedDescriptorWrapper<W extends IndexedDescriptor>
			extends IndexedDescriptor, AccessibleDescriptorWrapper<W> {

		@Override
		default IndexedDescriptor reindex(Object index) {
			return getSource().reindex(index);
		}

		@Override
		default Object getIndex() {
			return getSource().getIndex();
		}
	}

	@Data
	public static class IndexedAccessibleDescriptor<W extends AccessibleDescriptor>
			implements IndexedDescriptor, AccessibleDescriptorWrapper<W>, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private final W source;
		private final Object index;

		@Override
		public IndexedDescriptor reindex(Object index) {
			return new IndexedAccessibleDescriptor<>(source, index);
		}
	}

	Object getIndex();

	default IndexedDescriptor reindex(Object index) {
		return new IndexedAccessibleDescriptor<>(this, index);
	}

	public static IndexedDescriptor forAccessibleDescriptor(AccessibleDescriptor accessibleDescriptor, Object index) {
		return new IndexedAccessibleDescriptor<>(accessibleDescriptor, index);
	}

	public static IndexedDescriptor forTypeDescriptor(TypeDescriptor typeDescriptor, Object index) {
		CustomizeIndexedDescriptor indexedDescriptor = new CustomizeIndexedDescriptor(typeDescriptor);
		indexedDescriptor.setIndex(index);
		return indexedDescriptor;
	}

}
