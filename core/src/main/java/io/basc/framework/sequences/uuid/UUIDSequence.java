package io.basc.framework.sequences.uuid;

import java.util.UUID;

import io.basc.framework.sequences.StringSequence;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;

public interface UUIDSequence extends StringSequence {

	/**
	 * 默认32位的uuid
	 */
	public static final Range<Integer> DEFAULT_LENGTH_RANGE = Range.just(32);

	@Override
	default Range<Integer> getLengthRange() {
		return DEFAULT_LENGTH_RANGE;
	}

	Range<Integer> getVersionRange();

	@Override
	default String next(Range<Integer> lengthRange) throws UnsupportedOperationException {
		UUID uuid = nextUUID(lengthRange, getVersionRange());
		return StringUtils.removeChar(uuid.toString(), '-');
	}

	default UUID nextUUID() {
		return nextUUID(getLengthRange(), getVersionRange());
	}

	UUID nextUUID(Range<Integer> lengthRange, Range<Integer> versionRange) throws UnsupportedOperationException;
}
