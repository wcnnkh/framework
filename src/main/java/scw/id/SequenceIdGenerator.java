package scw.id;

import scw.memcached.Memcached;
import scw.redis.Redis;

public class SequenceIdGenerator implements IdGenerator<SequenceId> {
	private static final String DEFAULT_TIME_FORMAT = "yyyyMMddHHmmss";
	private final IdFactory<SequenceId> idFactory;

	public SequenceIdGenerator(Memcached memcached) {
		this(memcached, DEFAULT_TIME_FORMAT);
	}

	public SequenceIdGenerator(Memcached memcached, String time_format) {
		this.idFactory = new SequenceIdFactory(memcached, time_format);
	}

	public SequenceIdGenerator(Redis redis) {
		this(redis, DEFAULT_TIME_FORMAT);
	}

	public SequenceIdGenerator(Redis redis, String time_format) {
		this.idFactory = new SequenceIdFactory(redis, time_format);
	}

	public SequenceId next() {
		return idFactory.generator(this.getClass().getName());
	}
}
