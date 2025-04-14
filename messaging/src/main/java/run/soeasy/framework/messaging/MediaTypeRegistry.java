package run.soeasy.framework.messaging;

import run.soeasy.framework.core.exchange.container.TreeSetContainer;

public class MediaTypeRegistry extends TreeSetContainer<MediaType> implements MediaTypes {

	public MediaTypeRegistry() {
		setComparator(MediaType.SPECIFICITY_COMPARATOR);
	}

	@Override
	public StandardMimeTypes toList() {
		return new StandardMimeTypes(super.toList());
	}
}
