package run.soeasy.framework.messaging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.io.MimeType;

public interface MediaTypes extends Elements<MediaType>, Comparable<MediaTypes> {

	public static final MediaTypes EMPTY = new EmptyMimeTypes();

	/**
	 * 排序处理后进行构造
	 * 
	 * @param elements
	 * @return
	 */
	public static MediaTypes forElements(@NonNull Elements<? extends MediaType> elements) {
		List<MediaType> list = elements.sorted(MediaType.SPECIFICITY_COMPARATOR).collect(Collectors.toList());
		return forSortedList(list);
	}

	/**
	 * 以元始顺序进行构造
	 * 
	 * @param list
	 * @return
	 */
	public static MediaTypes forSortedList(@NonNull List<MediaType> list) {
		if (list.isEmpty()) {
			return EMPTY;
		}
		return new StandardMimeTypes(list);
	}

	public static MediaTypes forString(String mediaType) {
		List<MediaType> list = MediaType.parseMediaTypes(mediaType);
		list.sort(MediaType.SPECIFICITY_COMPARATOR);
		return forSortedList(list);
	}

	public static MediaTypes forList(List<? extends String> mediaTypeList) {
		List<MediaType> list = MediaType.parseMediaTypes(mediaTypeList);
		list.sort(MediaType.SPECIFICITY_COMPARATOR);
		return forSortedList(list);
	}

	public static MediaTypes forArray(String... mediaType) {
		return forList(Arrays.asList(mediaType));
	}

	@Override
	default int compareTo(MediaTypes o) {
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
