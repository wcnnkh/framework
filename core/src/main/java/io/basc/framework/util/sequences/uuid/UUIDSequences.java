package io.basc.framework.util.sequences.uuid;

import java.util.UUID;

import io.basc.framework.util.Range;
import io.basc.framework.util.spi.ConfigurableServices;

public class UUIDSequences extends ConfigurableServices<UUIDSequence> implements UUIDSequence {
	private static UUIDSequences instance;

	public static UUIDSequences getInstance() {
		if (instance == null) {
			synchronized (UUIDSequences.class) {
				if (instance == null) {
					instance = new UUIDSequences();
					instance.register(RandomUUIDSequence.getInstance());
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	@Override
	public Range<Integer> getLengthRange() {
		return Range.unionAll(map((e) -> e.getLengthRange()), Integer::compare);
	}

	@Override
	public Range<Integer> getVersionRange() {
		return Range.unionAll(map((e) -> e.getVersionRange()), Integer::compare);
	}

	@Override
	public UUID nextUUID(Range<Integer> lengthRange, Range<Integer> versionRange) throws UnsupportedOperationException {
		for (UUIDSequence uuidSequence : this) {
			if (uuidSequence.getLengthRange().contains(lengthRange, Integer::compare)
					&& uuidSequence.getVersionRange().contains(versionRange, Integer::compare)) {
				return uuidSequence.nextUUID();
			}
		}
		throw new UnsupportedOperationException(
				"Unsupported length range " + lengthRange + " or version range " + versionRange);
	}

	public static String getUUID() {
		return getInstance().next();
	}
}
