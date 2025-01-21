package io.basc.framework.net;

import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

public interface MimeTypes extends Elements<MimeType>, Comparable<MimeTypes> {

	public static class StandardMimeTypes extends StandardListElements<MimeType, List<MimeType>> implements MimeTypes {
		private static final long serialVersionUID = 1L;

		public StandardMimeTypes(@NonNull List<MimeType> source) {
			super(source);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof MimeTypes) {
				return CollectionUtils.unorderedEquals(toList(), ((MimeTypes) obj).toList());
			}
			return false;
		}

		@Override
		public String toString() {
			return MimeTypeUtils.toString(this);
		}

	}

	/**
	 * 排序处理后进行构造
	 * 
	 * @param elements
	 * @return
	 */
	public static MimeTypes forElements(@NonNull Elements<? extends MimeType> elements) {
		List<MimeType> list = elements.sorted(MimeTypeUtils.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
		return forSortedList(list);
	}

	/**
	 * 以元始顺序进行构造
	 * 
	 * @param list
	 * @return
	 */
	public static MimeTypes forSortedList(@NonNull List<MimeType> list) {
		return new StandardMimeTypes(list);
	}

	@Override
	default int compareTo(MimeTypes o) {
		if (this.equals(o)) {
			return 0;
		}

		for (MimeType mimeType1 : this) {
			for (MimeType mimeType2 : o) {
				if (mimeType1.compareTo(mimeType2) > 0) {
					return 1;
				}
			}
		}
		return -1;
	}

	@Override
	boolean equals(Object obj);

	default Elements<String> getRawElements() {
		if (isEmpty()) {
			return Elements.empty();
		}

		return map((e) -> e.toString());
	}

	@Override
	int hashCode();

	default boolean isCompatibleWith(MimeType mimeType) {
		for (MimeType mime : this) {
			if (mime.isCompatibleWith(mimeType)) {
				return true;
			}
		}
		return false;
	}
}
