package scw.id;

import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

public class SequenceIdFactory implements IdFactory<SequenceId> {
	private static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";
	private final IdFactory<Long> idFactory;
	private final boolean hideTime;

	public SequenceIdFactory(Memcached memcached, boolean hideTime) {
		this.idFactory = new MemcachedIdFactory(memcached);
		this.hideTime = hideTime;
	}

	public SequenceIdFactory(Redis redis, boolean hideTime) {
		this.idFactory = new RedisIdFactory(redis);
		this.hideTime = hideTime;
	}

	public SequenceId generator(String name) {
		long t = System.currentTimeMillis();
		long number = idFactory.generator(name);
		if (number < 0) {
			number = Long.MAX_VALUE + number;
		}

		String id;
		if (hideTime) {
			id = XTime.format(t, TIME_FORMAT) + Long.toString(number, Character.MAX_RADIX);
		} else {
			id = StringUtils.complemented(Long.toString(t, Character.MAX_RADIX), '0', 13)
					+ Long.toString(number, Character.MAX_RADIX);
		}
		return new SequenceId(t, id);
	}
}
