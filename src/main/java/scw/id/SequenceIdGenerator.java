package scw.id;

import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

public class SequenceIdGenerator implements IdGenerator<SequenceId> {
	private final IdFactory<Long> idFactory;
	private final String time_format;

	public SequenceIdGenerator(Memcached memcached, String time_format) {
		this.idFactory = new MemcachedIdFactory(memcached);
		this.time_format = time_format;
	}

	public SequenceIdGenerator(Redis redis, String time_format) {
		this.idFactory = new RedisIdFactory(redis);
		this.time_format = time_format;
	}

	public SequenceId next() {
		long t = System.currentTimeMillis();
		long number = idFactory.generator(this.getClass().getName());
		if (number < 0) {
			number = Long.MAX_VALUE + number;
		}
		return new SequenceId(t, XTime.format(t, time_format) + Long.toString(number, Character.MAX_RADIX));
	}
}
