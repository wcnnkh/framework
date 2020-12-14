package scw.sql.orm.support.generation;

import scw.core.instance.annotation.SPI;
import scw.data.generator.SequenceIdGenerator;
import scw.memcached.Memcached;
import scw.memcached.locks.MemcachedLockFactory;

@SPI(order=Integer.MIN_VALUE)
public class MemcachedGeneratorService extends DefaultGeneratorService {

	public MemcachedGeneratorService(Memcached memcached) {
		super(new SequenceIdGenerator(memcached), memcached, new MemcachedLockFactory(memcached));
	}
}
