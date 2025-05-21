package run.soeasy.framework.sequences;

import run.soeasy.framework.core.domain.Range;

public class UnsupportedSequenceRangeException extends UnsupportedOperationException {
	private static final long serialVersionUID = 1L;

	public UnsupportedSequenceRangeException(Range<?> supportedRange, Range<?> range) {
		super("Unsupported range " + range + ", only supported " + supportedRange);
	}
}
