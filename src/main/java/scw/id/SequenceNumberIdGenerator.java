package scw.id;

import scw.memcached.Memcached;
import scw.redis.Redis;

public final class SequenceNumberIdGenerator implements IdGenerator<TimeStampId> {
	private final IdFactory<TimeStampId> idFactory;

	public SequenceNumberIdGenerator(Memcached memcached, String timeFormat) {
		this.idFactory = new SequenceNumberIdFactory(memcached, timeFormat);
	}

	public SequenceNumberIdGenerator(Redis redis, String timeFormat) {
		this.idFactory = new SequenceNumberIdFactory(redis, timeFormat);
	}

	public TimeStampId next() {
		return idFactory.generator(this.getClass().getName());
	}

}
