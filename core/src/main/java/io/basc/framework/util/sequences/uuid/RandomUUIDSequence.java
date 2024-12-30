package io.basc.framework.util.sequences.uuid;

import java.util.UUID;

import io.basc.framework.util.Range;

/**
 * Static factory to retrieve a type 4 (pseudo randomly generated) UUID.
 *
 * The {@code UUID} is generated using a cryptographically strong pseudo random
 * number generator.
 * 
 * @author shuchaowen
 *
 */
public class RandomUUIDSequence extends FixedVersionUUIDSequence {
	public static final Range<Integer> VERSION = Range.just(4);

	private static volatile RandomUUIDSequence instance;

	public static RandomUUIDSequence getInstance() {
		if (instance == null) {
			synchronized (RandomUUIDSequence.class) {
				if (instance == null) {
					instance = new RandomUUIDSequence();
				}
			}
		}
		return instance;
	}

	@Override
	public final Range<Integer> getVersionRange() {
		return VERSION;
	}

	@Override
	public UUID nextUUID() {
		return UUID.randomUUID();
	}

	public static String getUUID() {
		return getInstance().next();
	}
}
