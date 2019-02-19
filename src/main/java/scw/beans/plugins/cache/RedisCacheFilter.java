package scw.beans.plugins.cache;

import scw.common.utils.IOUtils;
import scw.redis.Redis;

public class RedisCacheFilter extends AbstractCacheFilter {
	private final Redis redis;

	public RedisCacheFilter(Redis redis, boolean debug) {
		super(debug);
		this.redis = redis;
	}

	@Override
	protected <T> T getCache(String key, Class<T> type) throws Exception {
		byte[] bk = key.getBytes("iso-8859-1");
		byte[] data = redis.get(bk);
		if (data == null) {
			return null;
		}

		return IOUtils.byteToJavaObject(data);
	}

	@Override
	protected void setCache(String key, int exp, Class<?> type, Object data)
			throws Exception {
		if (data == null) {
			return;
		}

		byte[] bk = key.getBytes("iso-8859-1");
		byte[] buff = IOUtils.javaObjectToByte(data);
		redis.setex(bk, exp * 2, buff);
	}

}
