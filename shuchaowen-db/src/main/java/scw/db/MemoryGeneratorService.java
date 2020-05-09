package scw.db;

import scw.data.Counter;
import scw.data.generation.SequenceIdGenerator;
import scw.locks.JdkLockFactory;
import scw.sql.orm.support.generation.DefaultGeneratorService;

public class MemoryGeneratorService extends DefaultGeneratorService {

	public MemoryGeneratorService(Counter counter) {
		super(new SequenceIdGenerator(), counter, new JdkLockFactory());
	}
}
