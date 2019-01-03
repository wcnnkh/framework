package scw.id;

import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

/**
 * 把时间按指定方式格式化后接上一个自增的10位int
 * 
 * @author shuchaowen
 *
 */
public class TimeStampIdGenerator implements IdGenerator<TimeStampId> {
	private final IdGenerator<Long> idGenerator;
	private final String timeFormat;

	public TimeStampIdGenerator(Memcached memcached, String timeFormat) {
		this.idGenerator = new MemcachedIdGenerator(memcached, this.getClass().getName() + "#" + timeFormat, 0);
		this.timeFormat = timeFormat;
	}

	public TimeStampIdGenerator(Redis redis, String timeFormat) {
		this.idGenerator = new RedisIdGenerator(redis, this.getClass().getName() + "#" + timeFormat, 0);
		this.timeFormat = timeFormat;
	}

	public TimeStampIdGenerator(Redis redis, String incrKey, String timeFormat) {
		this.idGenerator = new RedisIdGenerator(redis, incrKey, 0);
		this.timeFormat = timeFormat;
	}

	public TimeStampIdGenerator(Memcached memcached, String incrKey, String timeFormat) {
		this.idGenerator = new MemcachedIdGenerator(memcached, incrKey, 0);
		this.timeFormat = timeFormat;
	}

	public TimeStampId next() {
		long t = System.currentTimeMillis();
		String id = XTime.format(t, timeFormat)
				+ StringUtils.complemented(Math.abs(idGenerator.next().intValue()) + "", '0', 10);
		return new TimeStampId(t, id);
	}

}
