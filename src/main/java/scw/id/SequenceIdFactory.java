package scw.id;

import scw.common.utils.StringUtils;
import scw.common.utils.XTime;
import scw.memcached.Memcached;
import scw.redis.Redis;

public class SequenceIdFactory implements IdFactory<SequenceId> {
	private static final String DEFAULT_TIME_FORMAT = "yyyyMMddHHmmss";
	private final IdFactory<Long> idFactory;
	private final String time_format;

	public SequenceIdFactory(Memcached memcached) {
		this(memcached, DEFAULT_TIME_FORMAT);
	}

	public SequenceIdFactory(Memcached memcached, String time_format) {
		this.idFactory = new MemcachedIdFactory(memcached);
		this.time_format = time_format;
	}

	public SequenceIdFactory(Redis redis) {
		this(redis, DEFAULT_TIME_FORMAT);
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

		String id;
		if (StringUtils.isNull(time_format)) {
			id = Long.toString(t, Character.MAX_RADIX);
			id = StringUtils.reversed(id) + Long.toString(number, Character.MAX_RADIX);
		} else {
			id = XTime.format(t, time_format) + Long.toString(number, Character.MAX_RADIX);
		}
		return new SequenceId(t, id);
	}
}
