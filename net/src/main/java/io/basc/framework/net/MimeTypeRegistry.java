package io.basc.framework.net;

import io.basc.framework.util.register.container.TreeSetContainer;

public class MimeTypeRegistry extends TreeSetContainer<MimeType> implements MimeTypes {

	public MimeTypeRegistry() {
		setComparator(MimeTypeUtils.SPECIFICITY_COMPARATOR);
	}

	@Override
	public StandardMimeTypes toList() {
		return new StandardMimeTypes(super.toList());
	}
}
