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
					instance.doNativeConfigure();
				}
			}
		}
		return instance;
	}

	@Override
	public Range<Integer> getVersionRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID nextUUID(Range<Integer> lengthRange, Range<Integer> versionRange) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

}
