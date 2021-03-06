package scw.sql.orm.support.generation;

import scw.context.annotation.Provider;
import scw.data.generator.SequenceIdGenerator;
import scw.memcached.Memcached;
import scw.memcached.locks.MemcachedLockFactory;

@Provider
public class MemcachedGeneratorService extends DefaultGeneratorService {

	public MemcachedGeneratorService(Memcached memcached) {
		super(new SequenceIdGenerator(memcached), memcached, new MemcachedLockFactory(memcached));
	}
}
