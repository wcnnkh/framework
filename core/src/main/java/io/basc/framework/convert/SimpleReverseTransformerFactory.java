package io.basc.framework.convert;

import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleReverseTransformerFactory<T, E extends Throwable> extends SimpleTransformerFactory<T, E>
		implements ReverseTransformerFactory<T, E> {
	private final ConcurrentHashMap<Class<?>, ReverseTransformer<?, ? extends T, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isReverseTransformerRegistred(Class<?> type) {
		return map.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> ReverseTransformer<S, T, E> getReverseTransformer(Class<? extends S> type) {
		return (ReverseTransformer<S, T, E>) map.get(type);
	}

	@Override
	public <S> void registerReverseTransformer(Class<S> type,
			ReverseTransformer<? extends S, ? extends T, ? extends E> transformer) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(transformer != null, "transformer");
		map.put(type, transformer);
	}
}
