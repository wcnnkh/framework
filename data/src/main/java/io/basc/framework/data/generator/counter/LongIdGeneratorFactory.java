package io.basc.framework.data.generator.counter;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.data.Counter;
import io.basc.framework.data.generator.IdGenerator;
import io.basc.framework.data.generator.IdGeneratorFactory;

public final class LongIdGeneratorFactory implements IdGeneratorFactory<Long> {
	private volatile Map<String, IdGenerator<Long>> map = new HashMap<>();
	private final Counter counter;

	public LongIdGeneratorFactory(Counter counter) {
		this.counter = counter;
	}

	public Long generator(String name) {
		return counter.incr(this.getClass().getName() + "#" + name, 1, 1);
	}

	@Override
	public IdGenerator<Long> getIdGenerator(String name) {
		IdGenerator<Long> idGenerator = map.get(name);
		if (idGenerator == null) {
			synchronized (this) {
				idGenerator = map.get(name);
				if (idGenerator == null) {
					idGenerator = new CounterIdGenerator(counter, name, 1);
					map.put(name, idGenerator);
				}
			}
		}
		return idGenerator;
	}

}
