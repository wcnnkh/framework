package run.soeasy.framework.util.sequences.uuid;

import java.util.UUID;

import run.soeasy.framework.util.Range;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.sequences.FixedLengthStringSequence;
import run.soeasy.framework.util.sequences.UnsupportedSequenceRangeException;

public abstract class FixedVersionUUIDSequence extends FixedLengthStringSequence implements UUIDSequence {

	@Override
	public UUID nextUUID(Range<Integer> lengthRange, Range<Integer> versionRange) throws UnsupportedOperationException {
		if (!getLengthRange().contains(lengthRange, Integer::compare)) {
			throw new UnsupportedSequenceRangeException(getLengthRange(), lengthRange);
		}

		if (!getVersionRange().contains(versionRange, Integer::compare)) {
			throw new UnsupportedSequenceRangeException(getVersionRange(), versionRange);
		}
		return nextUUID();
	}

	@Override
	public final String next() {
		UUID uuid = nextUUID();
		return StringUtils.removeChar(uuid.toString(), '-');
	}

	@Override
	public abstract UUID nextUUID();
}
