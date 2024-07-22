package io.basc.framework.transform.factory.support;

import java.util.TreeMap;

import io.basc.framework.register.LimitedRegistration;
import io.basc.framework.register.Registration;
import io.basc.framework.transform.ReversibleTransformer;
import io.basc.framework.transform.Transformer;
import io.basc.framework.transform.factory.config.ReversibleTransformerRegistry;

public class DefaultReversibleTransformerFactory<S, E extends Throwable> extends DefaultTransformerFactory<S, E>
		implements ReversibleTransformerRegistry<S, E> {
	private TreeMap<Class<?>, ReversibleTransformer<? super S, ?, ? extends E>> reversibleTransformerMap;

	@SuppressWarnings("unchecked")
	@Override
	public <T> ReversibleTransformer<S, T, E> getReversibleTransformer(Class<? extends T> requiredType) {
		return (ReversibleTransformer<S, T, E>) get(requiredType, reversibleTransformerMap);
	}

	@Override
	public <T> Transformer<S, T, E> getTransformer(Class<? extends T> requiredType) {
		Transformer<S, T, E> transformer = super.getTransformer(requiredType);
		if (transformer == null) {
			transformer = getReversibleTransformer(requiredType);
		}
		return transformer;
	}

	@Override
	public <T> Registration registerReversibleTransformer(Class<T> targetType,
			ReversibleTransformer<S, T, ? extends E> reversibleTransformer) {
		this.reversibleTransformerMap = register(targetType, reversibleTransformer, reversibleTransformerMap);
		return LimitedRegistration.of(() -> reversibleTransformerMap.remove(targetType));
	}
}
