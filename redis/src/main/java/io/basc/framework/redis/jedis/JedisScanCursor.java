package io.basc.framework.redis.jedis;

import io.basc.framework.redis.core.ScanCursor;
import io.basc.framework.redis.core.ScanIteration;
import io.basc.framework.redis.core.ScanOptions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.util.SafeEncoder;

public class JedisScanCursor extends ScanCursor<byte[], byte[]> {
	private final Jedis jedis;

	public JedisScanCursor(long cursorId, ScanOptions<byte[]> options, Jedis jedis) {
		super(cursorId, options);
		this.jedis = jedis;
		open();
	}

	@Override
	protected ScanIteration<byte[]> doScan(long cursorId, ScanOptions<byte[]> options) {
		ScanParams scanParams = JedisUtils.toScanParams(options);
		redis.clients.jedis.ScanResult<byte[]> result = jedis.scan(SafeEncoder.encode(String.valueOf(cursorId)),
				scanParams);
		return new ScanIteration<>(Long.parseLong(result.getCursor()), result.getResult());
	}

	@Override
	protected void doClose() {
		jedis.close();
	}
}
