package io.basc.framework.transform.factory.support;

import java.util.TreeMap;

import io.basc.framework.core.convert.transform.ReversibleTransformer;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.transform.factory.config.ReversibleTransformerRegistry;
import io.basc.framework.util.register.StandardRegistration;
import io.basc.framework.util.register.Registration;

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
		return StandardRegistration.of(() -> reversibleTransformerMap.remove(targetType));
	}
}
