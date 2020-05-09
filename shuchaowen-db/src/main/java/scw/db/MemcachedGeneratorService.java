package scw.db;

import scw.data.generation.SequenceIdGenerator;
import scw.data.locks.MemcachedLockFactory;
import scw.data.memcached.Memcached;
import scw.sql.orm.support.generation.DefaultGeneratorService;

public class MemcachedGeneratorService extends DefaultGeneratorService {

	public MemcachedGeneratorService(Memcached memcached) {
		super(new SequenceIdGenerator(memcached), memcached, new MemcachedLockFactory(memcached));
	}
}
