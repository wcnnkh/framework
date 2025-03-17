package run.soeasy.framework.util.sequences.uuid;

import java.util.UUID;

import run.soeasy.framework.util.Range;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.sequences.StringSequence;

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
