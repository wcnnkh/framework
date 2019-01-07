package scw.id;

import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

public class SequenceIdFactory implements IdFactory<SequenceId> {
	private final IdFactory<Long> idFactory;
	private final String time_format;

	public SequenceIdFactory(Memcached memcached, String time_format) {
		this.idFactory = new MemcachedIdFactory(memcached);
		this.time_format = time_format;
	}

	public SequenceIdFactory(Redis redis, String time_format) {
		this.idFactory = new RedisIdFactory(redis);
		this.time_format = time_format;
	}

	public SequenceId generator(String name) {
		long t = System.currentTimeMillis();
		long number = idFactory.generator(name);
		if (number < 0) {
			number = Long.MAX_VALUE + number;
		}

		return new SequenceId(t, XTime.format(t, time_format) + Long.toString(number, Character.MAX_RADIX));
	}
}
