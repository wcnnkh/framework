package scw.orm.sql.support;

import scw.core.Constants;
import scw.data.Counter;
import scw.generator.id.IdGenerator;
import scw.generator.id.SequenceId;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.orm.sql.AbstractGeneratorService;

public class DefaultGeneratorService extends AbstractGeneratorService {
	private final IdGenerator<SequenceId> sequeueIdGenerator;
	private final Counter counter;
	private final LockFactory lockFactory;

	public DefaultGeneratorService(IdGenerator<SequenceId> sequeueIdGenerator, Counter counter,
			LockFactory lockFactory) {
		this.sequeueIdGenerator = sequeueIdGenerator;
		this.counter = counter;
		this.lockFactory = lockFactory;
	}

	protected String getCacheKey(GeneratorContext generatorContext) {
		StringBuilder sb = new StringBuilder(64);
		if (Constants.DEFAULT_PREFIX != null) {
			sb.append(Constants.DEFAULT_PREFIX);
		}

		sb.append("generator:");
		sb.append(generatorContext.getMappingContext().getDeclaringClass().getName());
		sb.append("&");
		sb.append(generatorContext.getMappingContext().getColumn().getName());
		return sb.toString();
	}

	@Override
	public SequenceId generateSequeueId(GeneratorContext generatorContext) {
		return sequeueIdGenerator.next();
	}

	protected long getMaxId(GeneratorContext generatorContext) {
		Long value = generatorContext.getOrmOperations().getMaxValue(Long.class,
				generatorContext.getMappingContext().getDeclaringClass(),
				generatorContext.getMappingContext().getColumn().getName());
		return value == null ? 0 : value;
	};

	@Override
	public Number generateNumber(GeneratorContext generatorContext) {
		String key = getCacheKey(generatorContext);
		if (!counter.isExist(key)) {
			// 不存在
			Lock lock = lockFactory.getLock(key + "&lock");
			try {
				lock.lock();
				if (!counter.isExist(key)) {
					long maxId = getMaxId(generatorContext);
					return counter.incr(key, 1, maxId + 1);
				}
			} finally {
				lock.unlock();
			}
		}
		return counter.incr(key, 1);
	}
}
