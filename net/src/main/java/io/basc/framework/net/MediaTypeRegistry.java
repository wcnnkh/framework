package io.basc.framework.net;

import io.basc.framework.util.register.container.TreeSetContainer;

public class MediaTypeRegistry extends TreeSetContainer<MediaType> implements MediaTypes {

	public MediaTypeRegistry() {
		setComparator(MediaType.SPECIFICITY_COMPARATOR);
	}

	@Override
	public StandardMimeTypes toList() {
		return new StandardMimeTypes(super.toList());
	}
}
