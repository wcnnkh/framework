package scw.orm.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.utils.NumberUtils;
import scw.data.Counter;
import scw.data.generator.SequenceId;
import scw.data.generator.SequenceIdGenerator;
import scw.data.memory.MemoryDataOperations;
import scw.env.Sys;
import scw.locks.LockFactory;
import scw.locks.ReentrantLockFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.MapperUtils;
import scw.orm.MaxValueFactory;
import scw.orm.ObjectRelationalMapping;
import scw.orm.generator.annotation.CreateTime;
import scw.orm.generator.annotation.Generator;
import scw.orm.generator.annotation.UUID;
import scw.util.XUtils;

public class DefaultGeneratorProcessor implements GeneratorProcessor {
	private static Logger logger = LoggerFactory.getLogger(DefaultGeneratorProcessor.class);
	private final ObjectRelationalMapping objectRelationalMapping;
	private final SequenceIdGenerator sequeueIdGenerator;
	private final Counter counter;
	private final LockFactory lockFactory;
	private final MaxValueFactory maxValueFactory;

	public DefaultGeneratorProcessor(ObjectRelationalMapping objectRelationalMapping, MaxValueFactory maxValueFactory) {
		this(objectRelationalMapping, maxValueFactory, new SequenceIdGenerator(), new MemoryDataOperations(),
				new ReentrantLockFactory());
	}

	public DefaultGeneratorProcessor(ObjectRelationalMapping objectRelationalMapping, MaxValueFactory maxValueFactory,
			SequenceIdGenerator sequeueIdGenerator, Counter counter, LockFactory lockFactory) {
		this.objectRelationalMapping = objectRelationalMapping;
		this.maxValueFactory = maxValueFactory;
		this.sequeueIdGenerator = sequeueIdGenerator;
		this.counter = counter;
		this.lockFactory = lockFactory;
	}

	@Override
	public <T> void process(Class<? extends T> entityClass, Object entity) {
		Map<Object, Object> contextMap = new HashMap<Object, Object>();
		for (Field field : objectRelationalMapping.getFields(entityClass)) {
			if (MapperUtils.isExistValue(field, entity)) {
				// 存在默认值 ，忽略
				continue;
			}
			process(entityClass, entity, field, contextMap);
		}
	}

	public <T> Object getUUID(Class<? extends T> entityClass, Object entity, Field field,
			Map<Object, Object> contextMap) {
		Object uuid = contextMap.get(UUID.class);
		if (uuid == null) {
			uuid = XUtils.getUUID();
			contextMap.put(UUID.class, uuid);
		}
		return uuid;
	}

	public <T> long getCreateTime(Class<? extends T> entityClass, Object entity, Field field,
			Map<Object, Object> contextMap) {
		Object time = contextMap.get(CreateTime.class);
		if (time == null) {
			time = Sys.currentTimeMillis();
			contextMap.put(CreateTime.class, time);
		}
		return (long) time;
	}

	public <T> SequenceId getSequenceId(Class<? extends T> entityClass, Object entity, Field field,
			Map<Object, Object> contextMap) {
		SequenceId sequenceId = (SequenceId) contextMap.get(SequenceId.class);
		if (sequenceId == null) {
			sequenceId = sequeueIdGenerator.next(getCreateTime(entityClass, entity, field, contextMap));
			contextMap.put(SequenceId.class, sequenceId);
		}
		return sequenceId;
	}

	public <T> Number getMaxId(Class<? extends T> entityClass, Object entity, Field field) {
		Number number = maxValueFactory.getMaxValue(Number.class, entityClass, field);
		return number == null ? 0 : number;
	};

	protected <T> String getCacheKey(Class<? extends T> entityClass, Object entity, Field field) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("generator:");
		sb.append(entityClass);
		sb.append(":");
		sb.append(field.getSetter().getName());
		return sb.toString();
	}

	public <T> Number generateNumber(Class<? extends T> entityClass, Object entity, Field field) {
		String key = getCacheKey(entityClass, entity, field);
		if (!counter.isExist(key)) {
			// 不存在
			Lock lock = lockFactory.getLock(key + "&lock");
			try {
				lock.lock();
				if (!counter.isExist(key)) {
					Number maxId = getMaxId(entityClass, entity, field);
					return counter.incr(key, 1, maxId.longValue() + 1);
				}
			} finally {
				lock.unlock();
			}
		}
		return counter.incr(key, 1);
	}

	public <T> void process(Class<? extends T> entityClass, Object entity, Field field,
			Map<Object, Object> contextMap) {
		if (AnnotatedElementUtils.isAnnotated(field, UUID.class)) {
			field.getSetter().set(entity, getUUID(entityClass, entity, field, contextMap));
			return;
		}

		if (AnnotatedElementUtils.isAnnotated(field, CreateTime.class)) {
			field.getSetter().set(entity, getCreateTime(entityClass, entity, field, contextMap));
		}

		if (AnnotatedElementUtils.isAnnotated(field, Generator.class)) {
			if (String.class == field.getSetter().getType()) {
				SequenceId sequenceId = getSequenceId(entityClass, entity, field, contextMap);
				field.getSetter().set(entity, sequenceId.getId());
			} else if (NumberUtils.isNumber(field.getSetter().getType())) {
				Number number = generateNumber(entityClass, entity, field);
				field.getSetter().set(entity, number, Sys.env.getConversionService());
			} else {
				logger.warn("not generator: " + field);
			}
		}
	}
}
