package scw.id;

import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

/**
 * 流水号生成器
 * 
 * @author asus1
 *
 */
public class SequenceNumberIdFactory implements IdFactory<TimeStampId> {
	private final IdFactory<Long> idFactory;
	private final String timeFormat;

	public SequenceNumberIdFactory(Memcached memcached, String timeFormat) {
		this.idFactory = new MemcachedIdFactory(memcached);
		this.timeFormat = timeFormat;
	}

	public SequenceNumberIdFactory(Redis redis, String timeFormat) {
		this.idFactory = new RedisIdFactory(redis);
		this.timeFormat = timeFormat;
	}

	public TimeStampId generator(String name) {
		long t = System.currentTimeMillis();
		long number = idFactory.generator(name);
		if (number < 0) {
			number = Long.MAX_VALUE + number;
		}

		String id = XTime.format(t, timeFormat)
				+ StringUtils.complemented(
						Long.toString(number, Character.MAX_RADIX), '0', 13);
		return new TimeStampId(t, id);
	}
}
