package io.basc.framework.util.sequences;

import io.basc.framework.util.Range;

public class UnsupportedSequenceRangeException extends UnsupportedOperationException {
	private static final long serialVersionUID = 1L;

	public UnsupportedSequenceRangeException(Range<?> supportedRange, Range<?> range) {
		super("Unsupported range " + range + ", only supported " + supportedRange);
	}
}
