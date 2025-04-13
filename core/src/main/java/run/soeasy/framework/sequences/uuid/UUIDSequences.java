package run.soeasy.framework.sequences.uuid;

import java.util.UUID;

import run.soeasy.framework.core.Range;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class UUIDSequences extends ConfigurableServices<UUIDSequence> implements UUIDSequence {
	private static UUIDSequences global;

	/**
	 * 全局的UUID序列实现
	 * 
	 * @return
	 */
	public static UUIDSequences global() {
		if (global == null) {
			synchronized (UUIDSequences.class) {
				if (global == null) {
					global = new UUIDSequences();
					global.register(RandomUUIDSequence.getInstance());
					global.configure();
				}
			}
		}
		return global;
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
}
