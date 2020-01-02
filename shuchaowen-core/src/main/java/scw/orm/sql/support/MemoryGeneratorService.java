package scw.orm.sql.support;

import scw.data.Counter;
import scw.id.SequenceIdGenerator;
import scw.locks.JdkLockFactory;

public class MemoryGeneratorService extends DefaultGeneratorService {

	public MemoryGeneratorService(Counter counter) {
		super(new SequenceIdGenerator(), counter, new JdkLockFactory());
	}
}
