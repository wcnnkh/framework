package scw.redis.core.convert;

import scw.codec.Encoder;
import scw.core.utils.ArrayUtils;
import scw.data.domain.Range;

public class RedisConverters {
	private static final String INCLUSIVE_LEFT = "[";
	private static final String INCLUSIVE_RIGHT = "[";

	public static byte[] convertLowerBound(Range.Bound<? extends byte[]> bound, Encoder<String, byte[]> encoder) {
		if(bound.isBounded()) {
			if(bound.isInclusive()) {
				return ArrayUtils.merge(encoder.encode(INCLUSIVE_LEFT), (byte[])bound.getValue().get());
			}else {
				return bound.getValue().get();
			}
		}else {
			return encoder.encode("-");
		}
	}

	public static byte[] convertUpperBound(Range.Bound<? extends byte[]> bound, Encoder<String, byte[]> encoder) {
		if(bound.isBounded()) {
			if(bound.isInclusive()) {
				return ArrayUtils.merge((byte[])bound.getValue().get(), encoder.encode(INCLUSIVE_RIGHT));
			}else {
				return bound.getValue().get();
			}
		}else {
			return encoder.encode("+");
		}
	}
}
