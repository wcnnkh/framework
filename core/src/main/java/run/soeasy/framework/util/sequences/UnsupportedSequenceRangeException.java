package run.soeasy.framework.util.sequences;

import run.soeasy.framework.util.Range;

public class UnsupportedSequenceRangeException extends UnsupportedOperationException {
	private static final long serialVersionUID = 1L;

	public UnsupportedSequenceRangeException(Range<?> supportedRange, Range<?> range) {
		super("Unsupported range " + range + ", only supported " + supportedRange);
	}
}
