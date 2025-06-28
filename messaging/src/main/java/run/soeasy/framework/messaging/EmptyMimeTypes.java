package run.soeasy.framework.messaging;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

class EmptyMimeTypes implements MediaTypes, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Iterator<MediaType> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Stream<MediaType> stream() {
		return Stream.empty();
	}

}