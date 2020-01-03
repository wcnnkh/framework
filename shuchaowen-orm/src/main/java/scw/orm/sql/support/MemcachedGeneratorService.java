package scw.orm.sql.support;

import scw.data.memcached.Memcached;
import scw.generator.id.SequenceIdGenerator;
import scw.locks.MemcachedLockFactory;

public class MemcachedGeneratorService extends DefaultGeneratorService {

	public MemcachedGeneratorService(Memcached memcached) {
		super(new SequenceIdGenerator(memcached), memcached, new MemcachedLockFactory(memcached));
	}
}
