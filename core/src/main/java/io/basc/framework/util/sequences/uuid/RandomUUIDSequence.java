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

	@Override
	public final Range<Integer> getVersionRange() {
		return VERSION;
	}

	@Override
	public UUID nextUUID() {
		return UUID.randomUUID();
	}
}
