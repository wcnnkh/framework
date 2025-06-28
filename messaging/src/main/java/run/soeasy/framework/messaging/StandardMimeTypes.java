package run.soeasy.framework.messaging;

import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.StandardListElements;
import run.soeasy.framework.io.MimeTypeUtils;

public class StandardMimeTypes extends StandardListElements<MediaType, List<MediaType>>
		implements MediaTypes {
	private static final long serialVersionUID = 1L;

	public StandardMimeTypes(@NonNull List<MediaType> source) {
		super(source);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof MediaTypes) {
			return CollectionUtils.unorderedEquals(toList(), ((MediaTypes) obj).toList());
		}
		return false;
	}

	@Override
	public String toString() {
		return MimeTypeUtils.toString(this);
	}

}
