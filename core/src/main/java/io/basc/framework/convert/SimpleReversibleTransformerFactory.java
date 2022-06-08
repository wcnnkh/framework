package io.basc.framework.convert;

import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.util.Assert;

public class SimpleReversibleTransformerFactory<S, E extends Throwable> extends SimpleReverseTransformerFactory<S, E>
		implements ReversibleTransformerFactory<S, E> {
	private final ConcurrentHashMap<Class<?>, ReversibleTransformer<S, ?, ? extends E>> map = new ConcurrentHashMap<>();

	@Override
	public boolean isReversibleTransformerRegistred(Class<?> type) {
		return map.contains(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> type) {
		return (ReversibleTransformer<S, T, E>) map.get(type);
	}

	@Override
	public <T> void registerReversibleTransformer(Class<T> type,
			ReversibleTransformer<S, ? extends T, ? extends E> reverser) {
		Assert.requiredArgument(type != null, "type");
		Assert.requiredArgument(reverser != null, "reverser");
		map.put(type, reverser);
	}

	@Override
	public boolean isTransformerRegistred(Class<?> type) {
		return super.isTransformerRegistred(type) || isReversibleTransformerRegistred(type);
	}

	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> type) {
		Transformer<S, T, E> transformer = super.getTransformer(type);
		if (transformer == null) {
			return getReversibleTransformer(type);
		}
		return transformer;
	}

	@Override
	public boolean isReverseTransformerRegistred(Class<?> type) {
		return super.isReverseTransformerRegistred(type) || isReversibleTransformerRegistred(type);
	}

	@Override
	public <R> ReverseTransformer<R, S, E> getReverseTransformer(Class<? extends R> type) {
		ReverseTransformer<R, S, E> transformer = super.getReverseTransformer(type);
		if (transformer != null) {
			return transformer;
		}
		return getReversibleTransformer(type);
	}
}
