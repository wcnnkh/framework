package scw.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Assert;
import scw.util.stream.Processor;

public class SimpleMapper<S, E extends Throwable> implements Mapper<S, E> {
	private final Map<Class<?>, Processor<S, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Processor<S, T, E> getProcessor(Class<T> type) {
		return (Processor<S, T, E>) map.get(type);
	}

	@Override
	public <T> void register(Class<T> type, Processor<S, ? extends T, ? extends E> processor) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(processor != null, "processor");
		map.put(type, processor);
	}

}
