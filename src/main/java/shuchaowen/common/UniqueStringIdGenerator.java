package shuchaowen.common;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedIdGenerator;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisIdGernerator;

/**
 * 不确定长度的
 * 最长为26个字符
 * @author shuchaowen
 */
public final class UniqueStringIdGenerator implements IdGenerator<String> {
	private final IdGenerator<Long> idGenerator;

	public UniqueStringIdGenerator(Memcached memcached) {
		this.idGenerator = new MemcachedIdGenerator(memcached, this.getClass().getName(), 0);
	}

	public UniqueStringIdGenerator(Redis redis) {
		this.idGenerator = new RedisIdGernerator(redis, this.getClass().getName(), 0);
	}

	public String next() {
		return StringUtils.reversed(StringUtils.complemented(Long.toString(System.currentTimeMillis(), 36), '0', 13))
				+ Long.toString(Math.abs(idGenerator.next()), 36);
	}

	/**
	 * 获取这个id的创建时间
	 * 
	 * @param id
	 * @return
	 */
	public static long getCts(String id) {
		if (id.length() <= 13) {
			throw new ShuChaoWenRuntimeException(id);
		}
		
		return Long.parseLong(StringUtils.reversed(id.substring(0, 13)), 36);
	}
}
