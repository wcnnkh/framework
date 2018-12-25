package scw.id;

import scw.common.LongNumberConversion;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.StringUtils;
import scw.memcached.Memcached;
import scw.memcached.MemcachedIdGenerator;
import scw.redis.Redis;
import scw.redis.RedisIdGernerator;

/**
 * 不确定长度的
 * 最长为22个字符
 * @author shuchaowen
 */
public final class UniqueStringIdGenerator implements IdGenerator<String> {
	private static final LongNumberConversion LONG_NUMBER_CONVERSION = new LongNumberConversion(StringUtils.ALL);
	
	private final IdGenerator<Long> idGenerator;

	public UniqueStringIdGenerator(Memcached memcached) {
		this.idGenerator = new MemcachedIdGenerator(memcached, this.getClass().getName(), 0);
	}

	public UniqueStringIdGenerator(Redis redis) {
		this.idGenerator = new RedisIdGernerator(redis, this.getClass().getName(), 0);
	}

	public String next() {
		return StringUtils.reversed(StringUtils.complemented(LONG_NUMBER_CONVERSION.encode(System.currentTimeMillis(), 36), '0', 11))
				+ LONG_NUMBER_CONVERSION.encode(Math.abs(idGenerator.next()));
	}

	/**
	 * 获取这个id的创建时间
	 * 
	 * @param id
	 * @return
	 */
	public static long getCts(String id) {
		if (id.length() <= 11 || id.length() > 22) {
			throw new ShuChaoWenRuntimeException(id);
		}
		
		return LONG_NUMBER_CONVERSION.decode(StringUtils.reversed(id.substring(0, 13)));
	}
}
