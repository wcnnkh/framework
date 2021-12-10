package io.basc.framework.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public class MimeTypes implements Comparator<MimeType>, Iterable<MimeType>, Comparable<MimeTypes>, Cloneable {
	public static final MimeTypes EMPTY = new MimeTypes(Collections.emptySortedSet());
	private final SortedSet<MimeType> mimeTypes;
	private boolean readyOnly;

	public MimeTypes() {
		this.mimeTypes = new TreeSet<MimeType>(this);
	}

	public MimeTypes(SortedSet<MimeType> mimeTypes) {
		this(mimeTypes, false);
	}

	public MimeTypes(String... mimeTypes) {
		this.mimeTypes = new TreeSet<>(this);
		if (mimeTypes != null) {
			for (String mimeType : mimeTypes) {
				if (StringUtils.isEmpty(mimeType)) {
					continue;
				}

				this.mimeTypes.add(MimeTypeUtils.parseMimeType(mimeType));
			}
		}
	}

	protected MimeTypes(SortedSet<MimeType> mimeTypes, boolean readyOnly) {
		this.mimeTypes = readyOnly ? Collections.unmodifiableSortedSet(mimeTypes) : new TreeSet<>(mimeTypes);
		this.readyOnly = readyOnly;
	}

	public Iterator<MimeType> iterator() {
		return Collections.unmodifiableCollection(mimeTypes).iterator();
	}

	public final SortedSet<MimeType> getMimeTypes() {
		return readyOnly ? Collections.unmodifiableSortedSet(mimeTypes) : mimeTypes;
	}

	public final List<String> getRawMimeTypes() {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		List<String> list = new ArrayList<>(mimeTypes.size());
		for (MimeType mimeType : mimeTypes) {
			list.add(mimeType.toString());
		}
		return list;
	}

	public boolean isEmpty() {
		return mimeTypes.isEmpty();
	}

	public final MimeTypes add(MimeType... mimeTypes) {
		for (MimeType mimeType : mimeTypes) {
			this.mimeTypes.add(mimeType);
		}
		return this;
	}

	public final MimeTypes addAll(Iterable<? extends MimeType> mimeTypes) {
		for (MimeType mimeType : mimeTypes) {
			this.mimeTypes.add(mimeType);
		}
		return this;
	}

	public final boolean isReadyOnly() {
		return readyOnly;
	}

	public final MimeTypes readyOnly() {
		if (readyOnly) {
			return this;
		}

		return new MimeTypes(mimeTypes, true);
	}

	public int compare(MimeType o1, MimeType o2) {
		return MimeTypeUtils.SPECIFICITY_COMPARATOR.compare(o1, o2);
	}

	@Override
	public int compareTo(MimeTypes o) {
		if (this.equals(o)) {
			return 0;
		}

		for (MimeType mimeType1 : mimeTypes) {
			for (MimeType mimeType2 : o.mimeTypes) {
				if (mimeType1.compareTo(mimeType2) > 0) {
					return 1;
				}
			}
		}
		return -1;
	}

	public boolean isCompatibleWith(MimeType mimeType) {
		for (MimeType mime : mimeTypes) {
			if (mime.isCompatibleWith(mimeType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MimeTypes clone() {
		return new MimeTypes(mimeTypes, readyOnly);
	}

	@Override
	public int hashCode() {
		return mimeTypes.hashCode();
	}

	@Override
	public String toString() {
		return MimeTypeUtils.toString(mimeTypes);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof MimeTypes) {
			return CollectionUtils.equals(mimeTypes, ((MimeTypes) obj).mimeTypes);
		}
		return false;
	}
}
