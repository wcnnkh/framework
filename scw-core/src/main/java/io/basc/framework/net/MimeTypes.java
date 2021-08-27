package io.basc.framework.net;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

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
	
	protected MimeTypes(SortedSet<MimeType> mimeTypes, boolean readyOnly) {
		this.mimeTypes = new TreeSet<MimeType>(mimeTypes);
		this.readyOnly = readyOnly;
	}

	public Iterator<MimeType> iterator() {
		return Collections.unmodifiableCollection(mimeTypes).iterator();
	}

	public final SortedSet<MimeType> getMimeTypes() {
		return readyOnly ? Collections.unmodifiableSortedSet(mimeTypes) : mimeTypes;
	}

	public final MimeTypes add(MimeType... mimeTypes) {
		for (MimeType mimeType : mimeTypes) {
			getMimeTypes().add(mimeType);
		}
		return this;
	}

	public final boolean isReadyOnly() {
		return readyOnly;
	}

	public final MimeTypes readyOnly() {
		if(readyOnly){
			return this;
		}
		
		return new MimeTypes(mimeTypes, true);
	}

	public int compare(MimeType o1, MimeType o2) {
		return MimeTypeUtils.SPECIFICITY_COMPARATOR.compare(o1, o2);
	}
	
	@Override
	public int compareTo(MimeTypes o) {
		for (MimeType mimeType1 : mimeTypes) {
			for (MimeType mimeType2 : o.mimeTypes) {
				if(mimeType1.compareTo(mimeType2) > 0){
					return 1;
				}
			}
		}
		return -1;
	}
	
	public boolean isCompatibleWith(MimeType mimeType){
		for(MimeType mime : mimeTypes){
			if(mime.isCompatibleWith(mimeType)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public MimeTypes clone() {
		return new MimeTypes(mimeTypes, readyOnly);
	}
}
