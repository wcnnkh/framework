package io.basc.framework.redis.jedis;

import io.basc.framework.redis.core.ScanOptions;
import redis.clients.jedis.ScanParams;

public final class JedisUtils {
	private JedisUtils() {
	}

	public static ScanParams toScanParams(ScanOptions<byte[]> options) {
		ScanParams scanParams = new ScanParams();
		if (options != null) {
			scanParams.match(options.getPattern());
			if (options.getCount() != null) {
				scanParams.count(options.getCount().intValue());
			}
		}
		return scanParams;
	}
}
