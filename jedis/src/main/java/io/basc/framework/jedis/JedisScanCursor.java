package io.basc.framework.jedis;

import io.basc.framework.redis.ScanCursor;
import io.basc.framework.redis.ScanIteration;
import io.basc.framework.redis.ScanOptions;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
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
		ScanResult<byte[]> result = jedis.scan(SafeEncoder.encode(String.valueOf(cursorId)),
				scanParams);
		return new ScanIteration<>(Long.parseLong(result.getCursor()), result.getResult());
	}

	@Override
	protected void doClose() {
		jedis.close();
	}
}
