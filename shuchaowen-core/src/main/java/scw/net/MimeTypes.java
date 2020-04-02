package scw.net;

import java.util.TreeSet;

public class MimeTypes extends TreeSet<MimeType> {
	private static final long serialVersionUID = 1L;

	public MimeTypes() {
		super(MimeTypeUtils.SPECIFICITY_COMPARATOR);
	}

	public MimeTypes add(MimeType... mimeTypes) {
		for (MimeType mimeType : mimeTypes) {
			add(mimeType);
		}
		return this;
	}
}
