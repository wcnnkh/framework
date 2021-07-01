package scw.redis.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.util.SafeEncoder;
import scw.redis.core.KeyBoundCursor;
import scw.redis.core.ScanIteration;
import scw.redis.core.ScanOptions;

public class JedisKeyBoundCursor extends KeyBoundCursor<byte[], byte[]> {
	private final Jedis jedis;

	public JedisKeyBoundCursor(byte[] key, long cursorId, ScanOptions<byte[]> options, Jedis jedis) {
		super(key, cursorId, options);
		this.jedis = jedis;
		open();
	}

	@Override
	protected ScanIteration<byte[]> doScan(byte[] key, long cursorId, ScanOptions<byte[]> options) {
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
