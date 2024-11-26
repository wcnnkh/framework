package io.basc.framework.redis.convert;

import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Bound;
import io.basc.framework.util.codec.Encoder;

public class RedisConverters {
	private static final String INCLUSIVE_LEFT = "[";
	private static final String INCLUSIVE_RIGHT = "[";

	private RedisConverters() {
	}

	public static byte[] convertLowerBound(Bound<? extends byte[]> bound, Encoder<String, byte[]> encoder) {
		if (bound.isBounded()) {
			if (bound.isInclusive()) {
				return ArrayUtils.merge(encoder.encode(INCLUSIVE_LEFT), (byte[]) bound.get());
			} else {
				return bound.get();
			}
		} else {
			return encoder.encode("-");
		}
	}

	public static byte[] convertUpperBound(Bound<? extends byte[]> bound, Encoder<String, byte[]> encoder) {
		if (bound.isBounded()) {
			if (bound.isInclusive()) {
				return ArrayUtils.merge((byte[]) bound.get(), encoder.encode(INCLUSIVE_RIGHT));
			} else {
				return bound.get();
			}
		} else {
			return encoder.encode("+");
		}
	}
}
