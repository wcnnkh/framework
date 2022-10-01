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

	@Override
	public final void reverseTransform(Object source, Class<? extends Object> sourceType, T target) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, sourceType, target);
	}

	@Override
	public final void reverseTransform(Object source, Class<? extends Object> sourceType, T target,
			Class<? extends T> targetType) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, Class<? extends Object> sourceType, T target,
			TypeDescriptor targetType) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, sourceType, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, T target) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, target);
	}

	@Override
	public final void reverseTransform(Object source, T target, Class<? extends T> targetType) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, T target, TypeDescriptor targetType) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, target, targetType);
	}

	@Override
	public final void reverseTransform(Object source, TypeDescriptor sourceType, T target) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, sourceType, target);
	}

	@Override
	public final void reverseTransform(Object source, TypeDescriptor sourceType, T target,
			Class<? extends T> targetType) throws E {
		ReverseTransformerFactory.super.reverseTransform(source, sourceType, target, targetType);
	}
}
