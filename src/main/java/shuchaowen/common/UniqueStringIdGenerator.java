package shuchaowen.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.common.utils.XTime;
import shuchaowen.memcached.Memcached;
import shuchaowen.memcached.MemcachedIdGenerator;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisIdGernerator;

/**
 * 这是一个定长的27位字符串
 * 
 * @author shuchaowen
 *
 */
public final class UniqueStringIdGenerator implements IdGenerator<String> {
	private static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";
	private final IdGenerator<Long> idGenerator;

	public UniqueStringIdGenerator(Memcached memcached) {
		this.idGenerator = new MemcachedIdGenerator(memcached, this.getClass().getName(), 0);
	}

	public UniqueStringIdGenerator(Redis redis) {
		this.idGenerator = new RedisIdGernerator(redis, this.getClass().getName(), 0);
	}

	public String next() {
		return new SimpleDateFormat(TIME_FORMAT).format(new Date())
				+ StringUtils.complemented(Math.abs(idGenerator.next().intValue()) + "", '0', 10);
	}

	/**
	 * 获取这个id的创建时间
	 * @param id
	 * @return
	 */
	public static long getCts(String id) {
		if (id.length() < 17) {
			throw new ShuChaoWenRuntimeException(id);
		}

		return XTime.getTime(id.substring(0, 17), TIME_FORMAT);
	}
}
