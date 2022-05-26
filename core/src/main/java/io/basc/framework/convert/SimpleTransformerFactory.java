package io.basc.framework.convert;

import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleTransformerFactory<S, E extends Throwable> implements TransformerFactory<S, E> {
	private final ConcurrentHashMap<Class<?>, Transformer<S, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isTransformerRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		return (Transformer<S, T, E>) map.get(type);
	}

	@Override
	public <T> void registerTransformer(Class<T> type, Transformer<S, ? extends T, ? extends E> transformer) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(transformer != null, "transformer");
		map.put(type, transformer);
	}

}
