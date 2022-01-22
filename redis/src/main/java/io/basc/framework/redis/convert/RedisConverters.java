package io.basc.framework.redis.convert;

import io.basc.framework.codec.Encoder;
import io.basc.framework.data.domain.Range;
import io.basc.framework.util.ArrayUtils;

public class RedisConverters {
	private static final String INCLUSIVE_LEFT = "[";
	private static final String INCLUSIVE_RIGHT = "[";
	
	private RedisConverters() {
	}

	public static byte[] convertLowerBound(Range.Bound<? extends byte[]> bound, Encoder<String, byte[]> encoder) {
		if (bound.isBounded()) {
			if (bound.isInclusive()) {
				return ArrayUtils.merge(encoder.encode(INCLUSIVE_LEFT), (byte[]) bound.getValue().get());
			} else {
				return bound.getValue().get();
			}
		} else {
			return encoder.encode("-");
		}
	}

	public static byte[] convertUpperBound(Range.Bound<? extends byte[]> bound, Encoder<String, byte[]> encoder) {
		if (bound.isBounded()) {
			if (bound.isInclusive()) {
				return ArrayUtils.merge((byte[]) bound.getValue().get(), encoder.encode(INCLUSIVE_RIGHT));
			} else {
				return bound.getValue().get();
			}
		} else {
			return encoder.encode("+");
		}
	}
}
