package io.basc.framework.sequences;

import io.basc.framework.util.Range;

public interface StringSequence extends Sequence<String> {

	Range<Integer> getLengthRange();

	@Override
	default String next() {
		return next(getLengthRange());
	}

	String next(Range<Integer> lengthRange) throws UnsupportedOperationException;
}
